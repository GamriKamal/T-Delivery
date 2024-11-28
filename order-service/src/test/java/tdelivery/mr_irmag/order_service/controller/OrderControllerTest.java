package tdelivery.mr_irmag.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import tdelivery.mr_irmag.order_service.TestContainerBase;
import tdelivery.mr_irmag.order_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderItemRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderItemResponseDto;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderRequestDto;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderResponseDto;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserOrderRequestDTO;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.service.OrderService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends TestContainerBase {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void calculateOrder_ValidRequest_ShouldReturnCalculationDeliveryResponse() throws Exception {
        // Arrange
        CalculateOrderRequest request = new CalculateOrderRequest("comment",
                List.of(CalculateOrderItemRequest.builder()
                        .name("name")
                        .price(10.0)
                        .quantity(2)
                        .description("desc")
                        .build()), "address");
        CalculationDeliveryResponse response = CalculationDeliveryResponse.builder()
                .productPrice(10.0)
                .deliveryDuration(100)
                .userPoint(new Point(10.0, 20.0))
                .restaurantCoordinates(new Point(20.0, 30.0))
                .deliveryPrice(22.0)
                .totalPrice(42.0)
                .restaurantAddress("someAddress")
                .restaurantName("someName")
                .build();
        when(orderService.calculateOrder(any(CalculateOrderRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/order/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(42.0));
    }

    @Test
    void calculateOrder_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CalculateOrderRequest request = new CalculateOrderRequest("", List.of(), "");

        // Act & Assert
        mockMvc.perform(post("/order/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void makeOrder_ValidRequest_ShouldProcessOrder() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CalculateOrderRequest request = new CalculateOrderRequest("comment",
                List.of(CalculateOrderItemRequest.builder()
                        .name("name")
                        .price(10.0)
                        .quantity(2)
                        .description("desc")
                        .build()), "address");

        // Act & Assert
        mockMvc.perform(post("/order/makeOrder")
                        .header("id", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void makeOrder_InvalidId_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CalculateOrderRequest request = new CalculateOrderRequest("comment", new ArrayList<>(), "address");

        // Act & Assert
        mockMvc.perform(post("/order/makeOrder")
                        .header("id", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNearestOrder_ValidRequest_ShouldReturnNearestOrders() throws Exception {
        // Arrange
        NearestOrderRequestDto request = new NearestOrderRequestDto(3, new Point(55.7558, 37.6173));
        List<NearestOrderResponseDto> response = List.of(NearestOrderResponseDto.builder()
                .id(UUID.randomUUID())
                .name("Order 1234")
                .createdDate("2024-11-23")
                .deliveryAddress("123 Main St, Moscow, Russia")
                .comment("Leave at the door")
                .email("customer@gmail.com")
                .totalAmount(150.0)
                .restaurantAddress("Aksai 1")
                .location(new Point(10.0, 20.0))
                .timeOfDelivery(30)
                .items(List.of(NearestOrderItemResponseDto.builder()
                        .name("Order 1").build()))
                .build());
        when(orderService.getNearestOrders(anyInt(), any(Point.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/order/nearestOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Order 1234"));
    }

    @Test
    void getNearestOrder_InvalidRadius_ShouldReturnBadRequest() throws Exception {
        // Arrange
        NearestOrderRequestDto request = new NearestOrderRequestDto(10, new Point(55.7558, 37.6173));

        // Act & Assert
        mockMvc.perform(post("/order/nearestOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeStatus_ValidRequest_ShouldChangeStatus() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        String status = "SHIPPED";

        // Act & Assert
        mockMvc.perform(post("/order/changeStatus")
                        .header("id", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(status))
                .andExpect(status().isOk());
    }

    @Test
    void courierTakeOrder_ValidRequest_ShouldProcessOrder() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        ProcessCourierOrderRequest request = new ProcessCourierOrderRequest(id, OrderStatus.SHIPPED,
                new Point(55.7558, 37.6173), 30);

        // Act & Assert
        mockMvc.perform(post("/order/courier/takeOrder")
                        .header("id", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserOrder_InvalidId_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        int page = 0;
        int size = 10;

        // Act & Assert
        mockMvc.perform(get("/order/getUserOrder")
                        .header("id", "invalid-id")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrder_ValidRequest_ShouldCancelOrder() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/order/cancel")
                        .header("id", id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getUserOrder_ValidRequest_ShouldReturnUserOrders() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<UserOrderRequestDTO> orderList = List.of(UserOrderRequestDTO.builder()
                        .name("Order 1")
                        .createdDate(LocalDateTime.now())
                        .deliveryAddress("123 Main St, Moscow, Russia")
                        .comment("Leave at the door")
                        .totalAmount(150.0)
                        .status(OrderStatus.SHIPPED)
                .build());
        when(orderService.getOrdersOfUser(eq(userId), eq(0), eq(10))).thenReturn(orderList);

        // Act & Assert
        mockMvc.perform(get("/order/getUserOrder")
                        .header("id", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Order 1"));
    }

}
