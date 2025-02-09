package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
        super("Bad request: Invalid input provided.");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
