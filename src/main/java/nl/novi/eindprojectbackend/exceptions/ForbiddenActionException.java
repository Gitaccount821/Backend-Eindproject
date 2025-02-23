package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class ForbiddenActionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenActionException(String role, String action) {
        super(role + " is not allowed to " + action + ".");
    }

    public ForbiddenActionException(String role, String restriction, boolean isRestricted) {
        super(role + " " + restriction + ".");
    }
}
