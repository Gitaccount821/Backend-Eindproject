package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class RepairTypeNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RepairTypeNotFoundException(Long repairTypeId) {
        super("Repair Type with ID " + repairTypeId + " not found.");
    }
}