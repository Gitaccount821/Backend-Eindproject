package nl.novi.eindprojectbackend.exceptions;


public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String resourceName, Object id) {
        super(resourceName + " with identifier '" + id + "' not found.");
    }
}
