package tdelivery.mr_irmag.order_service.advise;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tdelivery.mr_irmag.order_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.order_service.exception.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceExceptionHandlerTest {

    private final OrderServiceExceptionHandler exceptionHandler = new OrderServiceExceptionHandler();

    @Test
    void handleOrderNotFoundException_WhenOrderNotFound_ShouldReturnNotFoundResponse() {
        // Arrange
        OrderNotFoundException exception = new OrderNotFoundException("Order 123 not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderNotFoundException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getMessage()).isEqualTo("Order 123 not found");
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void handleInvalidOrderStatus_WhenInvalidStatus_ShouldReturnBadRequestResponse() {
        // Arrange
        InvalidOrderStatusException exception = new InvalidOrderStatusException("Invalid status transition");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidOrderStatus(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid order status: Invalid status transition");
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void handleOrderProcessingException_WhenProcessingError_ShouldReturnBadRequestResponse() {
        // Arrange
        OrderProcessingException exception = new OrderProcessingException("Failed to process order");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderProcessingException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getMessage()).isEqualTo("Failed to process order");
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void handleUserServiceCommunicationException_WhenCommunicationFails_ShouldReturnServiceUnavailableResponse() {
        // Arrange
        UserServiceCommunicationException exception = new UserServiceCommunicationException("User service unavailable");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserServiceCommunicationException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(response.getBody().getMessage()).isEqualTo("User service unavailable");
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
