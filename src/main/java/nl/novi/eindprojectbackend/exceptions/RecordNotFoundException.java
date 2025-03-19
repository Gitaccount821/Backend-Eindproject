package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String resourceName, Object id) {
        super(resourceName + " with identifier '" + id + "' not found.");
    }
}
