package tdelivry.mr_irmag.user_service.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tdelivry.mr_irmag.user_service.domain.dto.ErrorResponse;
import tdelivry.mr_irmag.user_service.exception.UserException.FieldAlreadyExistsException;
import tdelivry.mr_irmag.user_service.exception.UserException.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class UserExceptionHandlerTest {

    private UserExceptionHandler userExceptionHandler;

    @BeforeEach
    void setUp() {
        userExceptionHandler = new UserExceptionHandler();
    }

    @Test
    void handleUserNotFoundException_WhenUserNotFound_ShouldReturnNotFoundResponse() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("User not found");

        // Act
        ResponseEntity<ErrorResponse> response = userExceptionHandler.handleCustomerNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getErrorCode());
        assertEquals("User not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleFieldAlreadyExistsException_WhenFieldAlreadyExists_ShouldReturnConflictResponse() {
        // Arrange
        FieldAlreadyExistsException exception = new FieldAlreadyExistsException("Email already exists");

        // Act
        ResponseEntity<ErrorResponse> response = userExceptionHandler.handleEmailAlreadyExists(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getErrorCode());
        assertEquals("Email already exists", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
