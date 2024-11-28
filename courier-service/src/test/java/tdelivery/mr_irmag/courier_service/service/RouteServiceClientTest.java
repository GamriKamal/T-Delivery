package tdelivery.mr_irmag.courier_service.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceRequest;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RouteServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Gson gson;

    @InjectMocks
    private RouteServiceClient routeServiceClient;

    @Value("${tdelivery.route-service.url}")
    private String routeServiceUrl;

    private RouteServiceRequest request;
    private RouteServiceResponse expectedResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        request = new RouteServiceRequest();
        expectedResponse = new RouteServiceResponse();
        expectedResponse.setId(UUID.fromString("7b2041c7-796e-4f60-8702-b2d2d096da66"));

        when(gson.fromJson(anyString(), eq(RouteServiceResponse.class)))
                .thenReturn(expectedResponse);
    }

    @Test
    public void findNearestOrder_SuccessfulResponse_ReturnsRouteServiceResponse() {
        // Arrange
        String mockResponseBody = "{\"orderId\":\"7b2041c7-796e-4f60-8702-b2d2d096da66\"}";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq(routeServiceUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Act
        RouteServiceResponse actualResponse = routeServiceClient.findNearestOrder(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(UUID.fromString("7b2041c7-796e-4f60-8702-b2d2d096da66"), actualResponse.getId());
        verify(restTemplate, times(1)).exchange(eq(routeServiceUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void findNearestOrder_ErrorResponse_ThrowsHttpClientErrorException() {
        // Arrange
        ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(eq(routeServiceUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> routeServiceClient.findNearestOrder(request));
    }


    @Test
    public void findNearestOrder_EmptyResponse_ThrowsNullPointerException() {
        // Arrange
        String emptyResponseBody = "";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(emptyResponseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq(routeServiceUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> routeServiceClient.findNearestOrder(request));
    }
}
