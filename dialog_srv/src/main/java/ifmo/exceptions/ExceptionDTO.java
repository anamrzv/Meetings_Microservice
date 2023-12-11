package ifmo.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ExceptionDTO(
        String message,
        HttpStatus httpStatus,
        LocalDateTime timestamp
) {

}
