package jets.projects.exceptions;

public class ResourceUnavailableException extends Exception {
    public ResourceUnavailableException(String message) {
        super(message);
    }

    public ResourceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}