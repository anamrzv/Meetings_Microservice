package ifmo.exceptions;

public class UnsuccessfulSave extends CustomBadRequestException {
    public UnsuccessfulSave(String message) {
        super(message);
    }
}