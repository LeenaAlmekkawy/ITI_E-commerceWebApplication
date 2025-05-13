package jets.projects.exceptions;

public class GeneralErrorException extends Exception {
    public GeneralErrorException(String message) {
        super(message);
    }

    public GeneralErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}