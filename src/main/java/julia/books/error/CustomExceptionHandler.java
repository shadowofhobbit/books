package julia.books.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class,
            org.hibernate.exception.ConstraintViolationException.class,
            javax.validation.ConstraintViolationException.class})
    public ResponseEntity<ErrorRepresentation> handleException(Exception exception) {
        final ErrorRepresentation error = new ErrorRepresentation(exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoTokenException.class)
    public ResponseEntity<?> handleNoToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

