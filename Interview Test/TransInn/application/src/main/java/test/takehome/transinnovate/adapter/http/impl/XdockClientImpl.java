package test.takehome.transinnovate.adapter.http.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import test.takehome.transinnovate.adapter.http.XdockClient;
import test.takehome.transinnovate.dto.ImportResponse;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.dto.OrderResponse;

@Slf4j
@Component
public class XdockClientImpl implements XdockClient {

    @Autowired
    private RestClient restClient;

    @Value("${xdock.api.url}")
    private String xdockApiUrl;

    @Override
    public OrderResponse sendRestOrder(OrderRequest payload) {
        return this.restClient.post()
            .uri(xdockApiUrl + "/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .retrieve()
            .body(OrderResponse.class);
    }

    @Override
    public OrderResponse sendRestFailingOrder(OrderRequest payload) {
        return restClient.post()
            .uri(xdockApiUrl + "/orders/failing")
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .retrieve()
            .body(OrderResponse.class);
    }

    @Override
    public String sendSoapOrder(String soapEnvelope) {
        return restClient.post()
            .uri(xdockApiUrl + "/soap/orders")
            .contentType(MediaType.TEXT_XML)
            .body(soapEnvelope)
            .retrieve()
            .body(String.class);
    }

    @Override
    public ImportResponse[] getImports() {
        return restClient.get()
            .uri(xdockApiUrl + "/imports")
            .retrieve()
            .body(ImportResponse[].class);
    }
}
