package tdelivry.mr_irmag.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tdelivry.mr_irmag.user_service.domain.dto.UserOrderResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceClient orderServiceClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderServiceClient, "orderServiceUrl", "http://localhost:8080/orders");
    }

    @Test
    void getOrderOfUser_ValidRequest_ShouldReturnOrderList() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int page = 1;
        int size = 5;

        String expectedUrl = "http://localhost:8080/orders?page=1&size=5";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("id", userId.toString());

        List<UserOrderResponse> mockOrders = List.of(
                new UserOrderResponse("Order1", LocalDateTime.now(), "Address1", "Comment1", 100.0, "DELIVERED"),
                new UserOrderResponse("Order2", LocalDateTime.now(), "Address2", "Comment2", 200.0, "SHIPPED")
        );

        ResponseEntity<List<UserOrderResponse>> mockResponse = new ResponseEntity<>(mockOrders, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(headers)),
                Mockito.<ParameterizedTypeReference<List<UserOrderResponse>>>any()
        )).thenReturn(mockResponse);

        // Act
        List<UserOrderResponse> result = orderServiceClient.getOrderOfUser(userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Order1", result.get(0).getName());
        verify(restTemplate, Mockito.times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(headers)),
                Mockito.<ParameterizedTypeReference<List<UserOrderResponse>>>any()
        );
    }

    @Test
    void getOrderOfUser_RestTemplateThrowsException_ShouldThrowRuntimeException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int page = 1;
        int size = 5;

        String expectedUrl = "http://localhost:8080/orders?page=1&size=5";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("id", userId.toString());

        // Mock exception from RestTemplate
        Mockito.when(restTemplate.exchange(
                Mockito.eq(expectedUrl),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(new HttpEntity<>(headers)),
                Mockito.<ParameterizedTypeReference<List<UserOrderResponse>>>any()
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Orders not found"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                orderServiceClient.getOrderOfUser(userId, page, size)
        );

        assertTrue(exception.getMessage().contains("Orders not found"));
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.eq(expectedUrl),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(new HttpEntity<>(headers)),
                Mockito.<ParameterizedTypeReference<List<UserOrderResponse>>>any()
        );
    }
}
