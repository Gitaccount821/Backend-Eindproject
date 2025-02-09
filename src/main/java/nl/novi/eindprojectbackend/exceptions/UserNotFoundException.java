package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username) {
        super("User with username '" + username + "' not found.");
    }
}
