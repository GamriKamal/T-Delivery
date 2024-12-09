package tdelivery.mr_irmag.route_service.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tdelivery.mr_irmag.route_service.domain.dto.ErrorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrors = List.of(
                new FieldError("TestObject", "field1", "Field1 is invalid"),
                new FieldError("TestObject", "field2", "Field2 must not be blank")
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("fieldErrors"));

        ErrorResponse errorResponse = (ErrorResponse) responseBody.get("error");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getErrorCode());
        assertEquals("Validation failed", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());

        Map<String, String> fieldErrorsMap = (Map<String, String>) responseBody.get("fieldErrors");
        assertEquals(2, fieldErrorsMap.size());
        assertEquals("Field1 is invalid", fieldErrorsMap.get("field1"));
        assertEquals("Field2 must not be blank", fieldErrorsMap.get("field2"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new Exception("Something went wrong");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getErrorCode());
        assertEquals("An unexpected error occurred.", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }
}
