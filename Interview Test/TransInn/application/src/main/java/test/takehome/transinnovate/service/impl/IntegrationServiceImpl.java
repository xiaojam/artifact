package test.takehome.transinnovate.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import test.takehome.transinnovate.adapter.http.XdockClient;
import test.takehome.transinnovate.constants.ApplicationConstants;
import test.takehome.transinnovate.dto.ImportResponse;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.service.IntegrationService;
import test.takehome.transinnovate.service.SftpService;

import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    private XdockClient xdockClient;

    @Autowired
    private RestClient restClient;

    @Autowired
    private SftpService sftpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private XmlMapper xmlMapper;

    @Value("${xdock.api.url:http://localhost:8080}")
    private String xdockApiUrl;
    @Override
    public Object processOrder(ApplicationConstants.ChannelType channel, OrderRequest request) throws Exception {
        log.info("Processing order via channel: {}", channel);

        return switch (channel) {
            case REST -> sendRest(request);
            case REST_FAILING -> sendRestFailing(request);
            case SOAP -> sendSoap(request);
            case SFTP_REST -> processSftp(request, true);
            case SFTP_SOAP -> processSftp(request, false);
        };
    }

    @Retryable(retryFor = HttpServerErrorException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Object sendRestFailing(OrderRequest request) {
        log.info("Sending order via channel REST_FAILING...");
        return this.xdockClient.sendRestFailingOrder(request);
    }

    private Object sendRest(OrderRequest request) {
        log.info("Sending order via channel REST...");
        return this.xdockClient.sendRestOrder(request);
    }

    private Object sendSoap(OrderRequest request) throws Exception {
        log.info("Sending order via channel SOAP...");
        String xmlBody = this.xmlMapper.writeValueAsString(request);
        String soapEnvelope = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
            + xmlBody + "</soap:Body></soap:Envelope>";

        return this.xdockClient.sendSoapOrder(soapEnvelope);
    }

    private Object processSftp(OrderRequest request, boolean isJson) throws Exception {
        log.info("Processing order via channel {}...", isJson ? "SFTP_REST" : "SFTP_SOAP");
        String fileName = "order_" + UUID.randomUUID() + (isJson ? ".json" : ".xml");
        String content;

        if (isJson) {
            content = this.objectMapper.writeValueAsString(request);
        } else {
            String xmlBody = this.xmlMapper.writeValueAsString(request);
            content = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                + xmlBody + "</soap:Body></soap:Envelope>";
        }

        sftpService.uploadFile(fileName, content);

        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            ImportResponse response = validateSftpImport(fileName);
            if (response != null) {
                return response;
            }
            Thread.sleep(1000);
        }

        throw new RuntimeException("Timeout: File " + fileName + " was uploaded but not processed after 10 seconds.");
    }

    private ImportResponse validateSftpImport(String fileName) {
        ImportResponse[] responses = xdockClient.getImports();
        if (responses != null) {
            log.info("Successfully retrieved import responses from Xdock Client. Validating file: {}", fileName);
            return Arrays.stream(responses)
                .filter(response -> fileName.equals(response.getFileName()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }
}
