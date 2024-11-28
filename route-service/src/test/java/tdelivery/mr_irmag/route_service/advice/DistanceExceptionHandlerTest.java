package tdelivery.mr_irmag.route_service.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tdelivery.mr_irmag.route_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.route_service.exception.GeocodingServiceException;
import tdelivery.mr_irmag.route_service.exception.InvalidAddressException;
import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DistanceExceptionHandlerTest {

    private DistanceExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new DistanceExceptionHandler();
    }

    @Test
    void handleGeocodingServiceException_ShouldReturnServiceUnavailableResponse() {
        // Arrange
        GeocodingServiceException exception = new GeocodingServiceException("Geocoding service is down");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeocodingServiceException(exception);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.getBody().getErrorCode());
        assertEquals("Geocoding service is down", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleInvalidAddressException_ShouldReturnBadRequestResponse() {
        // Arrange
        InvalidAddressException exception = new InvalidAddressException("Invalid address provided");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidAddressException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getErrorCode());
        assertEquals("Invalid address provided", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleOptimalRouteNotFound_ShouldReturnNotFoundResponse() {
        // Arrange
        OptimalRouteNotFound exception = new OptimalRouteNotFound("Route not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOptimalRouteNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getErrorCode());
        assertEquals("Route not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
