package nl.novi.eindprojectbackend.utils;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.models.Part;

import java.util.Map;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName, true);
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

    public static void validatePart(Part part) {
        if (part == null) {
            throw new BadRequestException("Part cannot be null.");
        }

        validateNotEmpty(part.getName(), "Part name");
        validatePositiveNumber(part.getPrice(), "Part price");
        validateNonNegativeNumber(part.getStock(), "Part stock");
    }

    public static void validatePartPatch(Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            throw new BadRequestException("No data provided for update.");
        }

        if (updates.containsKey("name")) {
            Object nameObj = updates.get("name");
            if (!(nameObj instanceof String)) {
                throw new BadRequestException("Part name must be a string.");
            }
            validateNotEmpty((String) nameObj, "Part name");
        }

        if (updates.containsKey("price")) {
            Object priceObj = updates.get("price");
            validatePositiveNumber(priceObj, "Part price");
        }

        if (updates.containsKey("stock")) {
            Object stockObj = updates.get("stock");
            validateNonNegativeNumber(stockObj, "Part stock");
        }
    }
}
