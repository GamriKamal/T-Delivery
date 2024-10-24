package tdelivry.mr_irmag.user_service.advice;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tdelivry.mr_irmag.user_service.domain.dto.ErrorResponse;
import tdelivry.mr_irmag.user_service.exception.UserException.UserNotFoundException;
import tdelivry.mr_irmag.user_service.exception.UserException.FieldAlreadyExistsException;

import java.time.LocalDateTime;

@ControllerAdvice
@Order(1)
public class UserExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(FieldAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(FieldAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
