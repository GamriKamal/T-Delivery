package tdelivery.mr_irmag.order_service.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.RouteServiceResponse;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceClientTest {

    @InjectMocks
    private RouteServiceClient routeServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Gson gson;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field routeServiceUrlField = RouteServiceClient.class.getDeclaredField("routeServiceUrl");
        routeServiceUrlField.setAccessible(true);
        routeServiceUrlField.set(routeServiceClient, "http://some-test-url/calculateDelivery");
    }

    @Test
    void calculateDelivery_WithValidRequest_ReturnsRouteServiceResponse() {
        // Arrange
        CalculateOrderRequest request = new CalculateOrderRequest();
        String jsonResponse = "{\"distance\": 5.0, \"duration\": 10.0}";
        RouteServiceResponse expectedResponse = RouteServiceResponse.builder()
                .deliveryPrice(100.0)
                .restaurantName("someRestaurant")
                .deliveryDuration(100)
                .restaurantCoordinates(new Point(10.0, 20.0))
                .restaurantAddress("someAddress")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<CalculateOrderRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);

        when(restTemplate.exchange("http://some-test-url/calculateDelivery", HttpMethod.POST, requestEntity, String.class))
                .thenReturn(responseEntity);
        when(gson.fromJson(jsonResponse, RouteServiceResponse.class)).thenReturn(expectedResponse);

        // Act
        RouteServiceResponse actualResponse = routeServiceClient.calculateDelivery(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange("http://some-test-url/calculateDelivery", HttpMethod.POST, requestEntity, String.class);
        verify(gson).fromJson(jsonResponse, RouteServiceResponse.class);
    }

    @Test
    void calculateDelivery_WithServerError_ThrowsHttpServerErrorException() {
        // Arrange
        CalculateOrderRequest request = new CalculateOrderRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<CalculateOrderRequest> requestEntity = new HttpEntity<>(request, headers);

        when(restTemplate.exchange("http://some-test-url/calculateDelivery", HttpMethod.POST, requestEntity, String.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        // Act & Assert
        HttpServerErrorException exception = assertThrows(HttpServerErrorException.class, () -> {
            routeServiceClient.calculateDelivery(request);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        verify(restTemplate).exchange("http://some-test-url/calculateDelivery", HttpMethod.POST, requestEntity, String.class);
    }
}

