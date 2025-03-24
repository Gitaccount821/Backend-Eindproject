package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class ForbiddenActionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenActionException() {
        super("You do not have permission to access this resource.");
    }

}
