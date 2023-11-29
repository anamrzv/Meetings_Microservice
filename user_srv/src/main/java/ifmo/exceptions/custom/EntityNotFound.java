package ifmo.exceptions.custom;

import ifmo.exceptions.CustomNotFoundException;

public class EntityNotFound extends CustomNotFoundException {
    public EntityNotFound(String message) {
        super(message);
    }
}
