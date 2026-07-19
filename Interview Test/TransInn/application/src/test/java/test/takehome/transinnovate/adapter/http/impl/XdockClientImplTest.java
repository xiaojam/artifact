package test.takehome.transinnovate.adapter.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import test.takehome.transinnovate.dataSeed.OrderDataSeed;
import test.takehome.transinnovate.dto.ImportResponse;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.dto.OrderResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
@Import(RestClient.class)
class XdockClientImplTest {

    @InjectMocks
    private XdockClientImpl xdockClient;

    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient mockRestClient = builder.build();

        ReflectionTestUtils.setField(xdockClient, "restClient", mockRestClient);
        ReflectionTestUtils.setField(xdockClient, "xdockApiUrl", BASE_URL);

        orderRequest = OrderDataSeed.mockOrderRequest();
        orderResponse = OrderDataSeed.mockOrderResponse();

        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @SneakyThrows
    void sendRestOrder_Success() {
        String responseJson = objectMapper.writeValueAsString(orderResponse);

        mockServer.expect(requestTo(BASE_URL + "/orders"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        OrderResponse result = xdockClient.sendRestOrder(orderRequest);

        assertNotNull(result);
        assertEquals(orderResponse.getId(), result.getId());
        assertEquals(orderResponse.getConsignor(), result.getConsignor());

        mockServer.verify();
    }

    @Test
    @SneakyThrows
    void sendRestFailingOrder_Success() {
        String responseJson = objectMapper.writeValueAsString(orderResponse);

        mockServer.expect(requestTo(BASE_URL + "/orders/failing"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        OrderResponse result = xdockClient.sendRestFailingOrder(orderRequest);

        assertNotNull(result);
        assertEquals(orderResponse.getId(), result.getId());
        mockServer.verify();
    }

    @Test
    void sendSoapOrder_Success() {
        String mockSoapRequest = "<soap:Envelope><soap:Body>Mock Request</soap:Body></soap:Envelope>";
        String mockSoapResponse = "<soap:Envelope><soap:Body>Mock Response</soap:Body></soap:Envelope>";

        mockServer.expect(requestTo(BASE_URL + "/soap/orders"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.TEXT_XML))
            .andExpect(content().string(mockSoapRequest))
            .andRespond(withSuccess(mockSoapResponse, MediaType.TEXT_XML));

        String result = xdockClient.sendSoapOrder(mockSoapRequest);

        assertNotNull(result);
        assertEquals(mockSoapResponse, result);
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    void getImports_Success() {
        ImportResponse[] mockImports = new ImportResponse[]{
            ImportResponse.builder()
                .fileName("order_8888.json")
                .order(orderResponse)
                .build()
        };

        String responseJson = objectMapper.writeValueAsString(mockImports);

        mockServer.expect(requestTo(BASE_URL + "/imports"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        ImportResponse[] result = xdockClient.getImports();

        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("order_8888.json", result[0].getFileName());
        assertEquals(orderResponse.getId(), result[0].getOrder().getId());

        mockServer.verify();
    }
}
