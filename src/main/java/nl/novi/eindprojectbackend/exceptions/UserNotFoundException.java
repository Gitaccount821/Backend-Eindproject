package nl.novi.eindprojectbackend.exceptions;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username) {
        super("User with username '" + username + "' not found.");
    }
}
