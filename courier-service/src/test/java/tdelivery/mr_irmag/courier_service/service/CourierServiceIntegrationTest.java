package tdelivery.mr_irmag.courier_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import tdelivery.mr_irmag.courier_service.domain.dto.*;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GetNearestOrderResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDistanceResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDurationResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.courier_service.exception.CourierCacheException;
import tdelivery.mr_irmag.courier_service.exception.ExternalServiceException;
import tdelivery.mr_irmag.courier_service.exception.OrderNotFoundException;
import tdelivery.mr_irmag.courier_service.exception.RouteServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "eureka.client.enabled=false")
class CourierServiceIntegrationTest {

    @InjectMocks
    private CourierService courierService;

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private RouteServiceClient routeServiceClient;

    @Mock
    private CourierCacheService courierCacheService;

    @Mock
    private MessageServiceClient messageServiceClient;


    private NearestOrderRequestDto nearestOrderRequestDto;
    private GetOrderRequest getOrderRequest;

    @BeforeEach
    void setup() {
        nearestOrderRequestDto = new NearestOrderRequestDto();
        nearestOrderRequestDto.setRadius(3);
        nearestOrderRequestDto.setPoint(new Point(10.0, 20.0));

        getOrderRequest = new GetOrderRequest();
        getOrderRequest.setRadius(3);
        getOrderRequest.setPoint(new Point(10.0, 20.0));
        getOrderRequest.setOrderId(UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c"));

        courierCacheService.removeOptimalOrder(new Point(10.0, 20.0));
        courierCacheService.removeTimeDelivery(new Point(10.0, 20.0));
    }

    @Test
    void getNearestOrders_OrdersFound_ReturnsOrderResponse() {
        // Arrange
        UUID orderId = UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c");
        List<Order> order = List.of(Order.builder()
                .id(orderId)
                .name("testName")
                .createdDate("someDate")
                .deliveryAddress("someAddress")
                .comment("someComment")
                .email("someEmail")
                .totalAmount(100.0)
                .restaurantAddress("someAddress")
                .location(new Point(10.0, 20.0))
                .items(new ArrayList<>())
                .build());

        RouteServiceResponse optimalOrder = new RouteServiceResponse(orderId,
                new Point(10.0, 20.0),
                new GoogleDistanceResponse("txt", 1),
                new GoogleDurationResponse("txt", 2));

        when(orderServiceClient.getNearestOrders(any(NearestOrderRequestDto.class))).thenReturn(order);
        when(routeServiceClient.findNearestOrder(any(RouteServiceRequest.class))).thenReturn(optimalOrder);
        when(courierCacheService.cacheOptimalOrders(any(Point.class), any(List.class))).thenReturn(order);
        when(courierCacheService.cacheTimeDelivery(any(Point.class), any(RouteServiceResponse.class))).thenReturn(optimalOrder);

        // Act
        List<GetNearestOrderResponse> response = courierService.getNearestOrders(nearestOrderRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals("testName", response.get(0).getName());
        assertEquals(2, response.get(0).getDuration().getValue());
    }

    @Test
    void getNearestOrders_NoOrdersFound_ThrowsOrderNotFoundException() {
        // Arrange
        Mockito.when(orderServiceClient.getNearestOrders(any(NearestOrderRequestDto.class))).thenReturn(Collections.emptyList());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            courierService.getNearestOrders(nearestOrderRequestDto);
        });
        assertEquals("No orders found near the requested location.", exception.getMessage());
    }

    @Test
    void getNearestOrders_RouteServiceThrowsException_ThrowsRouteServiceException() {
        // Arrange
        UUID orderId = UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c");
        Order order = Order.builder()
                .id(orderId)
                .name("testName")
                .createdDate("someDate")
                .deliveryAddress("someAddress")
                .comment("someComment")
                .email("someEmail")
                .totalAmount(100.0)
                .restaurantAddress("someAddress")
                .location(new Point(10.0, 20.0))
                .items(new ArrayList<>())
                .build();
        Mockito.when(orderServiceClient.getNearestOrders(any(NearestOrderRequestDto.class))).thenReturn(List.of(order));
        Mockito.when(routeServiceClient.findNearestOrder(any(RouteServiceRequest.class)))
                .thenThrow(new RouteServiceException("Error fetching nearest order from Route Service."));

        // Act & Assert
        RouteServiceException exception = assertThrows(RouteServiceException.class, () -> {
            courierService.getNearestOrders(nearestOrderRequestDto);
        });
        assertEquals("Error fetching nearest order from Route Service.", exception.getMessage());
    }

    @Test
    void getNearestOrders_CourierCacheThrowsException_ThrowsCourierCacheException() {
        // Arrange
        NearestOrderRequestDto request = new NearestOrderRequestDto();
        request.setPoint(new Point(10.0, 20.0));
        List<Order> orders = List.of(new Order(UUID.randomUUID(), "name"));

        RouteServiceResponse optimalOrder = new RouteServiceResponse();
        optimalOrder.setId(UUID.randomUUID());

        when(orderServiceClient.getNearestOrders(request)).thenReturn(orders);
        when(routeServiceClient.findNearestOrder(any())).thenReturn(optimalOrder);

        doThrow(new CourierCacheException("Cache error")).when(courierCacheService).cacheOptimalOrders(any(), any());

        // Act & Assert
        CourierCacheException exception = assertThrows(CourierCacheException.class, () -> {
            courierService.getNearestOrders(request);
        });

        assertEquals("Error caching order details in Courier Cache.", exception.getMessage());
    }


    @Test
    void takeOrder_OrderFound_Success() {
        getOrderRequest.setOrderId(UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c"));
        // Arrange
        UUID orderId = UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c");
        List<Order> order = List.of(Order.builder()
                .id(orderId)
                .name("testName")
                .createdDate("someDate")
                .deliveryAddress("someAddress")
                .comment("someComment")
                .email("someEmail")
                .totalAmount(100.0)
                .timeOfDelivery(100)
                .restaurantAddress("someAddress")
                .location(new Point(10.0, 20.0))
                .items(new ArrayList<>())
                .build());

        RouteServiceResponse timeDelivery = new RouteServiceResponse(orderId,
                new Point(10.0, 20.0),
                new GoogleDistanceResponse("txt", 1),
                new GoogleDurationResponse("txt", 2));

        Mockito.when(courierCacheService.getOptimalOrder(any(Point.class))).thenReturn(order);
        Mockito.when(courierCacheService.getTimeDelivery(any(Point.class))).thenReturn(timeDelivery);
        Mockito.when(orderServiceClient.changeStatusOfOrder(any(ProcessCourierOrderRequest.class))).thenReturn(HttpStatus.OK);

        // Act
        courierService.takeOrder(getOrderRequest);

        // Assert
        verify(orderServiceClient).changeStatusOfOrder(ProcessCourierOrderRequest.builder()
                .orderId(UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c"))
                .orderStatus(OrderStatus.SHIPPED)
                .courierPoint(new Point(10.0, 20.0))
                .timeDelivery(0)
                .build());
    }

    @Test
    void takeOrder_NoOptimalOrder_ThrowsOrderNotFoundException() {
        // Arrange
        Mockito.when(courierCacheService.getOptimalOrder(any(Point.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            courierService.takeOrder(getOrderRequest);
        });
        assertEquals("No order found with id c89a9cfc-33b3-4167-b7fc-cf39f872ba9c", exception.getMessage());
    }

    @Test
    void takeOrder_StatusUpdateFails_ThrowsExternalServiceException() {
        // Arrange
        UUID orderId = UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c");
        List<Order> order = List.of(Order.builder()
                .id(orderId)
                .name("testName")
                .createdDate("someDate")
                .deliveryAddress("someAddress")
                .comment("someComment")
                .email("someEmail")
                .totalAmount(100.0)
                .restaurantAddress("someAddress")
                .location(new Point(10.0, 20.0))
                .items(new ArrayList<>())
                .build());

        RouteServiceResponse timeDelivery = new RouteServiceResponse(orderId,
                new Point(10.0, 20.0),
                new GoogleDistanceResponse("txt", 1),
                new GoogleDurationResponse("txt", 2));

        Mockito.when(courierCacheService.getOptimalOrder(any(Point.class))).thenReturn(order);
        Mockito.when(courierCacheService.getTimeDelivery(any(Point.class))).thenReturn(timeDelivery);
        Mockito.when(orderServiceClient.changeStatusOfOrder(any(ProcessCourierOrderRequest.class))).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);


        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            courierService.takeOrder(getOrderRequest);
        });
        assertEquals("Failed to update order status to SHIPPED.", exception.getMessage());
    }


    @Test
    void takeOrder_NoTimeDeliveryInCache_ThrowsCourierCacheException() {
        // Arrange
        UUID orderId = UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c");
        List<Order> order = List.of(Order.builder()
                .id(orderId)
                .name("testName")
                .createdDate("someDate")
                .deliveryAddress("someAddress")
                .comment("someComment")
                .email("someEmail")
                .totalAmount(100.0)
                .restaurantAddress("someAddress")
                .location(new Point(10.0, 20.0))
                .items(new ArrayList<>())
                .build());

        Mockito.when(courierCacheService.getOptimalOrder(any(Point.class))).thenReturn(order);
        Mockito.when(courierCacheService.getTimeDelivery(any(Point.class))).thenReturn(null);

        // Act & Assert
        CourierCacheException exception = assertThrows(CourierCacheException.class, () -> {
            courierService.takeOrder(getOrderRequest);
        });
        assertEquals("No time delivery data found in cache.", exception.getMessage());
    }

    @Test
    void takeOrder_EmailSendingFails_ThrowsExternalServiceException() {
        // Arrange
        UUID orderId = UUID.fromString("c89a9cfc-33b3-4167-b7fc-cf39f872ba9c");
        List<Order> order = List.of(Order.builder()
                .id(orderId)
                .name("testName")
                .createdDate("someDate")
                .deliveryAddress("someAddress")
                .comment("someComment")
                .email("someEmail")
                .totalAmount(100.0)
                .restaurantAddress("someAddress")
                .location(new Point(10.0, 20.0))
                .items(new ArrayList<>())
                .build());

        RouteServiceResponse timeDelivery = new RouteServiceResponse(orderId,
                new Point(10.0, 20.0),
                new GoogleDistanceResponse("txt", 1),
                new GoogleDurationResponse("txt", 2));

        // Mocking email sending failure
        Mockito.when(courierCacheService.getOptimalOrder(any(Point.class))).thenReturn(order);
        Mockito.when(courierCacheService.getTimeDelivery(any(Point.class))).thenReturn(timeDelivery);
        Mockito.when(orderServiceClient.changeStatusOfOrder(any(ProcessCourierOrderRequest.class)))
                .thenReturn(HttpStatus.OK);
        Mockito.doThrow(new ExternalServiceException("Failed to send email notification."))
                .when(messageServiceClient).sendEmail(any(MessageRequestDto.class));

        // Act & Assert
        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            courierService.takeOrder(getOrderRequest);
        });
        assertEquals("Failed to send email notification.", exception.getMessage());
    }


}
