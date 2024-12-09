package tdelivery.mr_irmag.route_service.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tdelivery.mr_irmag.route_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.route_service.exception.InvalidAddressException;
import tdelivery.mr_irmag.route_service.exception.RestaurantAlreadyExistsException;
import tdelivery.mr_irmag.route_service.exception.RestaurantNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestaurantExceptionHandlerTest {

    private RestaurantExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new RestaurantExceptionHandler();
    }

    @Test
    void handleRestaurantNotFound_ShouldReturnNotFoundResponse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        RestaurantNotFoundException exception = new RestaurantNotFoundException(uuid);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRestaurantNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getErrorCode());
        assertEquals("Restaurant not found with id " + uuid, errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void handleRestaurantAlreadyExists_ShouldReturnConflictResponse() {
        // Arrange
        RestaurantAlreadyExistsException exception = new RestaurantAlreadyExistsException("RestaurantName");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRestaurantAlreadyExists(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getErrorCode());
        assertEquals("Restaurant with name 'RestaurantName' already exists.", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void handleInvalidAddress_ShouldReturnBadRequestResponse() {
        // Arrange
        InvalidAddressException exception = new InvalidAddressException("Invalid address provided");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidAddress(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getErrorCode());
        assertEquals("Invalid address provided", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }
}
