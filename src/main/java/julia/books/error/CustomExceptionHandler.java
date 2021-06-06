package julia.books.error;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class,
            org.hibernate.exception.ConstraintViolationException.class,
            javax.validation.ConstraintViolationException.class})
    public ResponseEntity<ErrorRepresentation> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        final ErrorRepresentation error = new ErrorRepresentation(exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoTokenException.class)
    public ResponseEntity<?> handleNoToken(NoTokenException exception) {
        log.error("Token not found", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage(), ex);
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            final ErrorRepresentation error = new ErrorRepresentation("Unknown error");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}

