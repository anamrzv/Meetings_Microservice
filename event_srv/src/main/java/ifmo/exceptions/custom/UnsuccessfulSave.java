package ifmo.exceptions.custom;


import ifmo.exceptions.CustomBadRequestException;

public class UnsuccessfulSave extends CustomBadRequestException {
    public UnsuccessfulSave(String message) {
        super(message);
    }
}
