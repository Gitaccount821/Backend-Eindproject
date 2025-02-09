package nl.novi.eindprojectbackend.exceptions;

public class PdfNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "No PDF found for this car";

    public PdfNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public PdfNotFoundException(String message) {
        super(message != null ? message : DEFAULT_MESSAGE);
    }
}
