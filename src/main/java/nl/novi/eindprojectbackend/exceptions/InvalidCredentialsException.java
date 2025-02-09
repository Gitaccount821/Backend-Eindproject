package nl.novi.eindprojectbackend.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid username or password.";

    public InvalidCredentialsException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidCredentialsException(String message) {
        super(message != null ? message : DEFAULT_MESSAGE);
    }
}
