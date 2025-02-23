package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class RecordNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RecordNotFoundException(String entity, Long id) {
        super(entity + " with ID '" + id + "' not found.");
    }
}
