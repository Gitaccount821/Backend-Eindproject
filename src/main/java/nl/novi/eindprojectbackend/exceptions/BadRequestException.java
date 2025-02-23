package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String field, boolean isEmptyError) {
        super(isEmptyError ? field + " cannot be empty." : field);
    }
}
