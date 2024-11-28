package tdelivery.mr_irmag.courier_service.service;

import com.google.gson.Gson;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.courier_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Gson gson;


    @InjectMocks
    private OrderServiceClient orderServiceClient;

    private final String orderServiceUrl = "http://mock-order-service-url";

    private final String changeStatusUrl = "http://mock-change-status-url";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNearestOrders_NullResponse_ShouldReturnEmptyList() {
        // Arrange
        NearestOrderRequestDto requestDto = new NearestOrderRequestDto();
        LogCaptor logCaptor = LogCaptor.forClass(OrderServiceClient.class);

        when(restTemplate.exchange(eq(orderServiceUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(null);

        // Act
        List<Order> result = orderServiceClient.getNearestOrders(requestDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(logCaptor.getWarnLogs().contains("Received null response or empty body from order service"));
    }

    @Test
    void changeStatusOfOrder_NullResponse_ShouldReturnInternalServerError() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.DELIVERED;

        when(restTemplate.exchange(eq(changeStatusUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(null);

        LogCaptor logCaptor = LogCaptor.forClass(OrderServiceClient.class);

        // Act
        HttpStatusCode result = orderServiceClient.changeStatusOfOrder(ProcessCourierOrderRequest.builder()
                .orderId(orderId)
                .orderStatus(status).build());

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result);
        assertTrue(logCaptor.getWarnLogs().contains("Received null response when attempting to change order status"));
    }

}
