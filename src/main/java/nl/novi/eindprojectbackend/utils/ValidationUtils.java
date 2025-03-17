package nl.novi.eindprojectbackend.utils;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;

public class ValidationUtils {

    private ValidationUtils() {

    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " cannot be empty.");
        }
    }

    public static void validatePositiveNumber(Object numberObj, String fieldName) {
        if (numberObj == null) {
            throw new BadRequestException(fieldName + " is required.");
        }

        if (!(numberObj instanceof Number)) {
            throw new BadRequestException(fieldName + " must be a number.");
        }

        double value = ((Number) numberObj).doubleValue();

        if (value <= 0.0) {
            throw new BadRequestException(fieldName + " must be greater than zero.");
        }
    }

    public static void validateNonNegativeNumber(Object numberObj, String fieldName) {
        if (numberObj == null) {
            throw new BadRequestException(fieldName + " is required.");
        }

        if (!(numberObj instanceof Number)) {
            throw new BadRequestException(fieldName + " must be a number.");
        }

        int value = ((Number) numberObj).intValue();

        if (value < 0) {
            throw new BadRequestException(fieldName + " must be a non-negative value.");
        }
    }

}
