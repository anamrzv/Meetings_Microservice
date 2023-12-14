package ifmo.exceptions;

public class CustomExistsException extends RuntimeException {
    public CustomExistsException(String message) {
        super(message);
    }
}
