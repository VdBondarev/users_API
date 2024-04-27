package api.exception;

import static api.constant.OtherConstantsHolder.ERRORS;
import static api.constant.OtherConstantsHolder.MESSAGE;
import static api.constant.OtherConstantsHolder.STATUS;
import static api.constant.OtherConstantsHolder.TIME_STAMP;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, status);
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        body.put(ERRORS, errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> body = getBody(ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = getBody(ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(
            RegistrationException ex, WebRequest request) {
        Map<String, Object> body = getBody(ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }

    private Map<String, Object> getBody(Exception ex, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, status);
        body.put(MESSAGE, ex.getMessage());
        return body;
    }
}
