package exceptions;

import org.springframework.http.HttpStatus;

public class RegistrationException extends RuntimeException {

    private final HttpStatus statusCode;

    public RegistrationException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
