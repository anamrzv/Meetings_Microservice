package ifmo.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(value = CustomExistsException.class)
    public ResponseEntity<Object> handleCustomExistsExceptions(CustomExistsException customException) {
        HttpStatus badRequest = HttpStatus.CONFLICT;
        ExceptionDTO exceptionDTO = new ExceptionDTO(customException.getMessage(), badRequest, LocalDateTime.now());
        return new ResponseEntity<>(exceptionDTO, badRequest);
    }

    @ExceptionHandler(value = CustomBadRequestException.class)
    public ResponseEntity<Object> handleCustomBadRequestException(CustomExistsException customException) {
        HttpStatus badRequest = HttpStatus.FORBIDDEN;
        ExceptionDTO exceptionDTO = new ExceptionDTO(customException.getMessage(), badRequest, LocalDateTime.now());
        return new ResponseEntity<>(exceptionDTO, badRequest);
    }

    @ExceptionHandler(value = CustomInternalException.class)
    public ResponseEntity<Object> handleCustomInternalExceptions(CustomInternalException customException) {
        HttpStatus badRequest = HttpStatus.INTERNAL_SERVER_ERROR;
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
