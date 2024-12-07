package tdelivery.mr_irmag.courier_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tdelivery.mr_irmag.courier_service.domain.dto.GetOrderRequest;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GetNearestOrderResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDistanceResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDurationResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.exception.CourierCacheException;
import tdelivery.mr_irmag.courier_service.exception.OrderNotFoundException;
import tdelivery.mr_irmag.courier_service.service.CourierService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "eureka.client.enabled=false")
class CourierControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourierService courierService;

    @Autowired
    private MockMvc mockMvc;

    private NearestOrderRequestDto nearestOrderRequestDto;

    @BeforeEach
    void setup() {
        nearestOrderRequestDto = new NearestOrderRequestDto();
        nearestOrderRequestDto.setPoint(new Point(10.0, 20.0));
    }

    @Test
    void getNearestOrder_OrdersFound_ReturnsOrderResponse() throws Exception {
        // Arrange
        var mockResponse = List.of(GetNearestOrderResponse.builder()
                .deliveryAddress("testAddress")
                .restaurantAddress("testRestaurant")
                .comment("testComment")
                .name("testName")
                .email("testEmail")
                .totalAmount(123.0)
                .orderLocation(new Point(10.0, 20.0))
                .distance(new GoogleDistanceResponse("text", 1))
                .duration(new GoogleDurationResponse("text", 2))
                .items(new ArrayList<>())
                .build());

        when(courierService.getNearestOrders(any(NearestOrderRequestDto.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/courier/online")
                        .contentType(MediaType.valueOf("application/json"))
                        .content(objectMapper.writeValueAsString(nearestOrderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deliveryAddress").value("testAddress"))
                .andExpect(jsonPath("$[0].restaurantAddress").value("testRestaurant"))
                .andExpect(jsonPath("$[0].email").value("testEmail"));
    }

    @Test
    void getNearestOrder_NoOrdersFound_ReturnsNotFoundError() throws Exception {
        // Arrange
        when(courierService.getNearestOrders(any(NearestOrderRequestDto.class)))
                .thenThrow(new OrderNotFoundException("No orders found near the requested location."));

        // Act & Assert
        mockMvc.perform(post("/courier/online")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(nearestOrderRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No orders found near the requested location."));
    }

    @Test
    void takeOrder_Success_OrderApprovedAndStatusChanged() throws Exception {
        // Arrange
        GetOrderRequest nearestOrderRequest = GetOrderRequest.builder()
                .radius(3)
                .point(new Point(37.6173, 55.7558))
                .orderId(UUID.randomUUID())
                .build();

        doNothing().when(courierService).takeOrder(any(GetOrderRequest.class));

        // Act & Assert
        mockMvc.perform(post("/courier/takeOrder")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(nearestOrderRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void takeOrder_CacheError_ReturnsInternalServerError() throws Exception {
        // Arrange
        GetOrderRequest nearestOrderRequest = GetOrderRequest.builder()
                .radius(3)
                .point(new Point(37.6173, 55.7558))
                .orderId(UUID.randomUUID())
                .build();

        doThrow(new CourierCacheException("No optimal order found in cache.")).when(courierService)
                .takeOrder(any(GetOrderRequest.class));

        // Act & Assert
        mockMvc.perform(post("/courier/takeOrder")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(nearestOrderRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("No optimal order found in cache."));
    }

}
