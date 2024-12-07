package tdelivry.mr_irmag.user_service.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tdelivry.mr_irmag.user_service.domain.dto.ErrorResponse;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHadlerTest {

    private GlobalExceptionHadler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHadler();
    }

    @Test
    void handleValidationExceptions_WhenInvalidArgument_ShouldReturnBadRequest() {
        // Arrange
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "Invalid value");
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, ErrorResponse>> response = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("field"));
        ErrorResponse errorResponse = response.getBody().get("field");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getErrorCode());
        assertEquals("Invalid value", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void handleGenericException_WhenExceptionOccurs_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new Exception("An unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getErrorCode());
        assertEquals("An error occurred: An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}