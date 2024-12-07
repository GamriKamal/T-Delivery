package tdelivery.mr_irmag.menu_service.advise;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tdelivery.mr_irmag.menu_service.exception.ProductAlreadyExistsException;
import tdelivery.mr_irmag.menu_service.exception.ProductNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GlobalExceptionHandlerTest {
    @Test
    void handleProductNotFound_ProductNotFoundExceptionThrown_ShouldReturnNotFoundResponse() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String errorMessage = "Product not found";
        ProductNotFoundException exception = new ProductNotFoundException(errorMessage);

        // Act
        ResponseEntity<String> response = handler.handleProductNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleProductAlreadyExists_ProductAlreadyExistsExceptionThrown_ShouldReturnBadRequestResponse() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String errorMessage = "Product already exists";
        ProductAlreadyExistsException exception = new ProductAlreadyExistsException(errorMessage);

        // Act
        ResponseEntity<String> response = handler.handleProductAlreadyExists(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleDatabaseErrors_DataAccessExceptionThrown_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String databaseErrorMessage = "Unable to connect to the database";
        DataAccessException exception = new DataAccessException(databaseErrorMessage) {
        };

        // Act
        ResponseEntity<String> response = handler.handleDatabaseErrors(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error occurred: " + databaseErrorMessage, response.getBody());
    }

    @Test
    void handleGeneralErrors_GenericExceptionThrown_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String unexpectedErrorMessage = "Something went wrong";
        Exception exception = new Exception(unexpectedErrorMessage);

        // Act
        ResponseEntity<String> response = handler.handleGeneralErrors(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: " + unexpectedErrorMessage, response.getBody());
    }

}