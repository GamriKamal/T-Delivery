package tdelivery.mr_irmag.order_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.order_service.exception.UserServiceClientException;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceClientTest {

    @InjectMocks
    private UserServiceClient userServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field routeServiceUrlField = UserServiceClient.class.getDeclaredField("userServiceUrl");
        routeServiceUrlField.setAccessible(true);
        routeServiceUrlField.set(userServiceClient, "http://some-test-url/getUserByID");
    }

    @Test
    void getUserByID_WithValidId_ReturnsUserDetails() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String jsonResponse = "{\"id\":\"" + userId + "\", \"name\":\"John Doe\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("id", userId.toString());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);

        when(restTemplate.exchange("http://some-test-url/getUserByID", HttpMethod.GET, requestEntity, String.class))
                .thenReturn(responseEntity);

        // Act
        String actualResponse = userServiceClient.getUserByID(userId);

        // Assert
        assertEquals(jsonResponse, actualResponse);
        verify(restTemplate).exchange("http://some-test-url/getUserByID", HttpMethod.GET, requestEntity, String.class);
    }

    @Test
    void getUserByID_WithServerError_ThrowsUserServiceClientException() {
        // Arrange
        UUID userId = UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("id", userId.toString());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Мокаем ошибку на уровне REST запроса (например, 500 ошибка)
        when(restTemplate.exchange("http://some-test-url/getUserByID", HttpMethod.GET, requestEntity, String.class))
                .thenThrow(new RuntimeException("Server error"));

        // Act & Assert
        UserServiceClientException exception = assertThrows(UserServiceClientException.class, () -> {
            userServiceClient.getUserByID(userId);
        });

        assertTrue(exception.getMessage().contains("Failed to retrieve user details"));
        assertEquals(userId.toString(), exception.getUserId());
        verify(restTemplate).exchange("http://some-test-url/getUserByID", HttpMethod.GET, requestEntity, String.class);
    }

}
