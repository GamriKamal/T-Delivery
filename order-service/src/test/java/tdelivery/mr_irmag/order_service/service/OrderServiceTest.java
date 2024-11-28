package tdelivery.mr_irmag.order_service.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import tdelivery.mr_irmag.order_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderItemRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.RouteServiceResponse;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderResponseDto;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserInfoResponseDTO;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserOrderRequestDTO;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderItem;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.exception.OrderNotFoundException;
import tdelivery.mr_irmag.order_service.exception.OrderProcessingException;
import tdelivery.mr_irmag.order_service.exception.UserServiceCommunicationException;
import tdelivery.mr_irmag.order_service.kafka.KafkaProducerService;
import tdelivery.mr_irmag.order_service.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderCacheService orderCacheService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RouteServiceClient routeServiceClient;
    @Mock
    private MessageServiceClient messageServiceClient;
    @Mock
    private WebSocketDeliveryStatusService webSocketDeliveryStatusService;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private Gson gson;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateOrder_ValidRequest_ShouldReturnCalculationDeliveryResponse() {
        // Arrange
        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .comment("someComment")
                .items(new ArrayList<>())
                .address("someAddress")
                .build();
        RouteServiceResponse routeResponse = RouteServiceResponse.builder()
                .deliveryDuration(100)
                .deliveryPrice(100.0)
                .restaurantAddress("someRestaurantAddress")
                .restaurantCoordinates(new Point(10.0, 20.0))
                .restaurantName("someRestaurantName")
                .build();

        when(routeServiceClient.calculateDelivery(request)).thenReturn(routeResponse);

        // Act
        CalculationDeliveryResponse response = orderService.calculateOrder(request);

        // Assert
        assertNotNull(response);
        verify(orderCacheService, times(1)).cacheTotalAmount(anyString(), anyDouble());
        verify(orderCacheService, times(1)).cacheRestaurantCoordinates(anyString(), any());
    }

    @Test
    void getOrdersOfUser_UserHasOrders_ShouldReturnListOfUserOrderRequestDTO() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int page = 0;
        int size = 10;
        List<Order> orders = Arrays.asList(new Order(), new Order());
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderRepository.findAllOrderByUserId(eq(userId), any(Pageable.class))).thenReturn(orderPage);

        // Act
        List<UserOrderRequestDTO> result = orderService.getOrdersOfUser(userId, page, size);

        // Assert
        assertEquals(orders.size(), result.size());
        verify(orderRepository, times(1)).findAllOrderByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void getOrdersOfUser_NoOrders_ShouldThrowOrderNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        when(orderRepository.findAllOrderByUserId(eq(userId), any(Pageable.class))).thenReturn(Page.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrdersOfUser(userId, page, size));
    }

    @Test
    void getUsernameAndEmailOfUserById_ValidUserId_ShouldReturnUserInfoResponseDTO() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userInfoJson = "{}";
        UserInfoResponseDTO expectedResponse = new UserInfoResponseDTO();

        when(userServiceClient.getUserByID(userId)).thenReturn(userInfoJson);
        when(gson.fromJson(userInfoJson, UserInfoResponseDTO.class)).thenReturn(expectedResponse);

        // Act
        UserInfoResponseDTO result = orderService.getUsernameAndEmailOfUserById(userId);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void getUsernameAndEmailOfUserById_UserServiceException_ShouldThrowUserServiceCommunicationException() {
        // Arrange
        UUID userId = UUID.randomUUID();

        when(userServiceClient.getUserByID(userId)).thenThrow(new RuntimeException("User service error"));

        // Act & Assert
        assertThrows(UserServiceCommunicationException.class, () -> orderService.getUsernameAndEmailOfUserById(userId));
    }

    @Test
    void processOrder_ValidRequest_ShouldSaveOrderAndSendNotifications() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .comment("someComment")
                .items(new ArrayList<>())
                .address("someAddress")
                .build();

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .email("someEmail")
                .deliveryAddress("someAddress")
                .createdDate(LocalDateTime.now())
                .totalAmount(100.0)
                .name("someName")
                .status(OrderStatus.PAID)
                .restaurantAddress("someRestaurantAddress")
                .comment("someComment")
                .build();
        order.setOrderItems(List.of(OrderItem.builder()
                .id(1L)
                .price(100.0)
                .order(order)
                .quantity(2)
                .description("someDescription")
                .build()));
        order.setRestaurantCoordinates(10.0, 20.0);
        order.setUserCoordinates(10.0, 20.0);
        org.springframework.data.geo.Point restaurantPoint = new org.springframework.data.geo.Point(10.0, 20.0);
        org.springframework.data.geo.Point userPoint = new org.springframework.data.geo.Point(10.0, 20.0);
        UserInfoResponseDTO userInfo = UserInfoResponseDTO.builder().username("someName").email("someEmail").build();

        when(userServiceClient.getUserByID(userId)).thenReturn("{}");
        when(gson.fromJson(anyString(), eq(UserInfoResponseDTO.class))).thenReturn(userInfo);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        when(orderCacheService.getRestaurantCoordinates(request.getAddress())).thenReturn(restaurantPoint);
        when(orderCacheService.getTotalAmount(request.getAddress())).thenReturn(100.0);
        when(orderCacheService.getRestaurantAddress(request.getAddress())).thenReturn("someRestaurantAddress");
        when(orderCacheService.getUserCoordinates(request.getAddress())).thenReturn(userPoint);
        // Act
        assertDoesNotThrow(() -> orderService.processOrder(userId, request));

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(webSocketDeliveryStatusService, times(1)).sendOrderStatusUpdate(any(), any());
    }


    @Test
    void changeStatusOfOrder_ValidOrderId_ShouldUpdateOrderStatus() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderStatus newStatus = OrderStatus.DELIVERED;
        Order order = new Order();
        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.changeStatusOfOrder(orderId, newStatus.toString());

        // Assert
        assertEquals(newStatus, result.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(webSocketDeliveryStatusService, times(1)).sendOrderStatusUpdate(newStatus, order);
    }

    @Test
    void getOrderById_OrderExists_ShouldReturnOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order result = orderService.getOrderById(orderId);

        // Assert
        assertEquals(order, result);
    }

    @Test
    void getOrderById_OrderDoesNotExist_ShouldThrowOrderNotFoundException() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void updateSocket_ValidRequest_ShouldUpdateOrderStatusAndSendWebSocketMessage() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        ProcessCourierOrderRequest request = ProcessCourierOrderRequest.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.PREPARED)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .email("someEmail")
                .deliveryAddress("someAddress")
                .status(OrderStatus.PREPARED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Возвращаем обновленный заказ

        // Act
        orderService.updateSocket(request);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PREPARED);
        verify(orderRepository, times(1)).save(order);
        verify(webSocketDeliveryStatusService, times(1))
                .sendCourierStatusUpdate(order.getStatus(), order, request);
    }


    @Test
    void updateSocket_OrderNotFound_ShouldThrowException() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        ProcessCourierOrderRequest request = ProcessCourierOrderRequest.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.PREPARED)
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.updateSocket(request));
    }

    @Test
    void updateSocket_OrderAlreadyCancelled_ShouldThrowException() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        ProcessCourierOrderRequest request = ProcessCourierOrderRequest.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.PREPARED)
                .build();

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CANCELED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderProcessingException.class, () -> orderService.updateSocket(request));
    }

    @Test
    void getNearestOrders_ValidRequest_ShouldReturnNearestOrders() {
        // Arrange
        Point point = new Point(40.7128, 74.0060);
        int radius = 5;
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .email("someEmail")
                .deliveryAddress("someAddress")
                .status(OrderStatus.PREPARED)
                .build();

        order.setRestaurantCoordinates(10.0, 20.0);
        order.setUserCoordinates(20.0, 30.0);

        List<Order> orders = List.of(order);
        when(orderRepository.findNearestPreparedOrders(radius * 1000, point.getX(), point.getY(), 10)).thenReturn(orders);

        // Act
        List<NearestOrderResponseDto> nearestOrders = orderService.getNearestOrders(radius, point);

        // Assert
        assertThat(nearestOrders).hasSize(1);
        verify(orderRepository, times(1)).findNearestPreparedOrders(radius * 1000, point.getX(), point.getY(), 10);
    }

    @Test
    void sendEmail_ValidOrderId_ShouldSendEmail() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .email("someEmail")
                .deliveryAddress("someAddress")
                .createdDate(LocalDateTime.now())
                .totalAmount(100.0)
                .name("someName")
                .status(OrderStatus.PAID)
                .restaurantAddress("someRestaurantAddress")
                .comment("someComment")
                .build();
        order.setOrderItems(List.of(OrderItem.builder()
                .id(1L)
                .price(100.0)
                .order(order)
                .quantity(2)
                .description("someDescription")
                .build()));

        UserInfoResponseDTO userInfo = UserInfoResponseDTO.builder()
                .email("test@gmail.com")
                .username("test")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderService.getUsernameAndEmailOfUserById(userId)).thenReturn(userInfo);

        // Act
        orderService.sendEmail(orderId);

        // Assert
        verify(messageServiceClient, times(1)).sendEmail(any());
    }

}
