package ifmo.exceptions;

import ifmo.exceptions.custom.TokenExpired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(value = CustomBadRequestException.class)
    public ResponseEntity<Object> handleCustomBadExceptions(CustomBadRequestException customException) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionDTO exceptionDTO = new ExceptionDTO(customException.getMessage(), badRequest, LocalDateTime.now());
        return new ResponseEntity<>(exceptionDTO, badRequest);
    }

    @ExceptionHandler(value = TokenExpired.class)
    public ResponseEntity<Object> handleTokenExpiredExceptions(TokenExpired customException) {
        HttpStatus badRequest = HttpStatus.UNAUTHORIZED;
        ExceptionDTO exceptionDTO = new ExceptionDTO(customException.getMessage(), badRequest, LocalDateTime.now());
        return new ResponseEntity<>(exceptionDTO, badRequest);
    }

    @ExceptionHandler(value = CustomExistsException.class)
    public ResponseEntity<Object> handleCustomExistsExceptions(CustomExistsException customException) {
        HttpStatus badRequest = HttpStatus.CONFLICT;
        ExceptionDTO exceptionDTO = new ExceptionDTO(customException.getMessage(), badRequest, LocalDateTime.now());
        return new ResponseEntity<>(exceptionDTO, badRequest);
    }

    @ExceptionHandler(value = CustomNotFoundException.class)
    public ResponseEntity<Object> handleCustomNotFoundExceptions(CustomNotFoundException customException) {
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ExceptionDTO exceptionDTO = new ExceptionDTO(customException.getMessage(), badRequest, LocalDateTime.now());
        return new ResponseEntity<>(exceptionDTO, badRequest);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }
}
