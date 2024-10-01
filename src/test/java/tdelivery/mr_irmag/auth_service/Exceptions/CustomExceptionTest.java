package tdelivery.mr_irmag.auth_service.Exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionTest {

    @Test
    void constructor_EmailAlreadyExistsExceptionPositiveCase_ShouldReturnExpectedMessage() {
        // Arrange
        String expectedMessage = "Email already exists";

        // Act
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void constructor_UsernameAlreadyExistsExceptionPositiveCase_ShouldReturnExpectedMessage() {
        // Arrange
        String expectedMessage = "Username already exists";

        // Act
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}

