package test.takehome.transinnovate.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import test.takehome.transinnovate.adapter.http.XdockClient;
import test.takehome.transinnovate.constants.ApplicationConstants.ChannelType;
import test.takehome.transinnovate.dataSeed.OrderDataSeed;
import test.takehome.transinnovate.dto.ImportResponse;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.dto.OrderResponse;
import test.takehome.transinnovate.service.SftpService;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntegrationServiceImplTest {

    @InjectMocks
    private IntegrationServiceImpl integrationService;

    @Mock
    private XdockClient xdockClient;

    @Mock
    private SftpService sftpService;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderRequest = OrderDataSeed.mockOrderRequest();
        orderResponse = OrderDataSeed.mockOrderResponse();

        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ReflectionTestUtils.setField(integrationService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(integrationService, "xmlMapper", xmlMapper);
        ReflectionTestUtils.setField(integrationService, "xdockApiUrl", "http://localhost:8080");
    }

    @Test
    void processOrder_REST_Success() throws Exception {
        when(xdockClient.sendRestOrder(any())).thenReturn(orderResponse);

        Object result = integrationService.processOrder(ChannelType.REST, orderRequest);

        assertNotNull(result);
        assertEquals(orderResponse, result);
        verify(xdockClient, times(1)).sendRestOrder(any());
    }

    @Test
    void processOrder_REST_FAILING_Success() throws Exception {
        when(xdockClient.sendRestFailingOrder(any())).thenReturn(orderResponse);

        Object result = integrationService.processOrder(ChannelType.REST_FAILING, orderRequest);

        assertNotNull(result);
        verify(xdockClient, times(1)).sendRestFailingOrder(any());
    }

    @Test
    void processOrder_SOAP_Success() throws Exception {
        when(xdockClient.sendSoapOrder(anyString())).thenReturn("<xml>success</xml>");

        Object result = integrationService.processOrder(ChannelType.SOAP, orderRequest);

        assertNotNull(result);
        assertEquals("<xml>success</xml>", result);
        verify(xdockClient, times(1)).sendSoapOrder(anyString());
    }

    @Test
    void processOrder_SFTP_REST_Success() throws Exception {
        AtomicReference<String> capturedFileName = new AtomicReference<>();
        doAnswer(invocation -> {
            capturedFileName.set(invocation.getArgument(0));
            return null;
        }).when(sftpService).uploadFile(anyString(), anyString());

        when(xdockClient.getImports()).thenAnswer(invocation -> new ImportResponse[]{
            ImportResponse.builder()
                .fileName(capturedFileName.get())
                .order(orderResponse)
                .build()
        });

        Object result = integrationService.processOrder(ChannelType.SFTP_REST, orderRequest);

        assertNotNull(result);
        assertInstanceOf(ImportResponse.class, result);
        verify(sftpService, times(1)).uploadFile(anyString(), anyString());
    }

    @Test
    void processOrder_SFTP_SOAP_Success() throws Exception {
        AtomicReference<String> capturedFileName = new AtomicReference<>();
        doAnswer(invocation -> {
            capturedFileName.set(invocation.getArgument(0));
            return null;
        }).when(sftpService).uploadFile(anyString(), anyString());

        when(xdockClient.getImports()).thenAnswer(invocation -> new ImportResponse[]{
            ImportResponse.builder()
                .fileName(capturedFileName.get())
                .order(orderResponse)
                .build()
        });

        Object result = integrationService.processOrder(ChannelType.SFTP_SOAP, orderRequest);

        assertNotNull(result);
        assertInstanceOf(ImportResponse.class, result);
        verify(sftpService, times(1)).uploadFile(anyString(), anyString());
    }

    @Test
    void processOrder_SFTP_Fail_NullResponse() throws Exception {
        doNothing().when(sftpService).uploadFile(anyString(), anyString());
        when(xdockClient.getImports()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> integrationService.processOrder(ChannelType.SFTP_REST, orderRequest));
    }

    @Test
    void processOrder_SFTP_Fail_NotFound() throws Exception {
        doNothing().when(sftpService).uploadFile(anyString(), anyString());
        when(xdockClient.getImports()).thenReturn(new ImportResponse[]{
            ImportResponse.builder().fileName("wrong_file.json").build()
        });

        assertThrows(RuntimeException.class, () -> integrationService.processOrder(ChannelType.SFTP_REST, orderRequest));
    }
}