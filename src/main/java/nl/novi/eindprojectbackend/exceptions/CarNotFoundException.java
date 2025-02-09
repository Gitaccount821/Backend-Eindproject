package nl.novi.eindprojectbackend.exceptions;

import java.io.Serial;

public class CarNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CarNotFoundException(String carId) {
        super("Car with ID " + carId + " not found.");
    }
}