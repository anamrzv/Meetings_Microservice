package ifmo.exceptions.custom;

import ifmo.exceptions.CustomBadRequestException;

public class TokenExpired extends CustomBadRequestException {
    public TokenExpired(String message) {
        super(message);
    }
}

