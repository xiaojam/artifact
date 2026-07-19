package test.takehome.transinnovate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import test.takehome.transinnovate.config.RestConfig;
import test.takehome.transinnovate.constants.ApplicationConstants;
import test.takehome.transinnovate.dataSeed.OrderDataSeed;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.dto.OrderResponse;
import test.takehome.transinnovate.service.IntegrationService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntegrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestConfig.class)
public class IntegrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IntegrationService integrationService;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderRequest = OrderDataSeed.mockOrderRequest();
        orderResponse = OrderDataSeed.mockOrderResponse();
    }

    @Test
    @SneakyThrows
    void forwardOrder_Success() {
        when(integrationService.processOrder(eq(ApplicationConstants.ChannelType.REST), any(OrderRequest.class)))
            .thenReturn(orderResponse);

        MockHttpServletRequestBuilder mockHttpServletRequest = MockMvcRequestBuilders
            .post("/api/v1/integration/forward")
            .param("channel", "REST")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest))
            .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc
            .perform(mockHttpServletRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(orderResponse.getId())))
            .andExpect(jsonPath("$.consignor", is(orderResponse.getConsignor())))
            .andExpect(jsonPath("$.consignee", is(orderResponse.getConsignee())))
            .andExpect(jsonPath("$.invoiceTo", is(orderResponse.getInvoiceTo())))
            .andExpect(jsonPath("$.notifyParty", is(orderResponse.getNotifyParty())))
            .andExpect(jsonPath("$.collectionDate", is(orderResponse.getCollectionDate().toString())))
            .andExpect(jsonPath("$.collectionAddress", is(orderResponse.getCollectionAddress())))
            .andExpect(jsonPath("$.deliveryDate", is(orderResponse.getDeliveryDate().toString())))
            .andExpect(jsonPath("$.deliveryAddress", is(orderResponse.getDeliveryAddress())))
            .andExpect(jsonPath("$.palletType", is(orderResponse.getPalletType())))
            .andExpect(jsonPath("$.quantity", is(orderResponse.getQuantity())))
            .andExpect(jsonPath("$.weight", is(orderResponse.getWeight())))
            .andExpect(jsonPath("$.loadMeters", is(orderResponse.getLoadMeters())));
    }

    @Test
    @SneakyThrows
    void forwardOrder_MissingChannelParam() {
        MockHttpServletRequestBuilder mockHttpServletRequest = MockMvcRequestBuilders
            .post("/api/v1/integration/forward")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest));

        mockMvc
            .perform(mockHttpServletRequest)
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void forwardOrder_InvalidChannelParam() {
        MockHttpServletRequestBuilder mockHttpServletRequest = MockMvcRequestBuilders
            .post("/api/v1/integration/forward")
            .param("channel", "INVALID_CHANNEL_NGARANG")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest));

        mockMvc
            .perform(mockHttpServletRequest)
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void forwardOrder_MissingRequestBody() {
        MockHttpServletRequestBuilder mockHttpServletRequest = MockMvcRequestBuilders
            .post("/api/v1/integration/forward")
            .param("channel", "REST")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(mockHttpServletRequest)
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void forwardOrder_ServiceThrowsException() {
        when(integrationService.processOrder(any(), any()))
            .thenThrow(new RuntimeException("Connection failure"));

        MockHttpServletRequestBuilder mockHttpServletRequest = MockMvcRequestBuilders
            .post("/api/v1/integration/forward")
            .param("channel", "REST")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest));

        mockMvc
            .perform(mockHttpServletRequest)
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error", is("Integration Error")))
            .andExpect(jsonPath("$.message", is("Connection failure")));
    }
}
