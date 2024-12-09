package tdelivery.mr_irmag.order_service.advise;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tdelivery.mr_irmag.order_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.order_service.exception.UserServiceClientException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleUserServiceException_WhenUserServiceFails_ShouldReturnServiceUnavailableResponse() {
        // Arrange
        String userId = "12345";
        String errorMessage = "User service is unavailable";
        Throwable cause = new RuntimeException("Underlying service error");
        UserServiceClientException exception = new UserServiceClientException(errorMessage, userId, cause);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserServiceException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
