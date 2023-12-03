package ifmo.exceptions;

import ifmo.exceptions.custom.TokenExpired;
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

}
