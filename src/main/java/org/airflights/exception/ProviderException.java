package org.airflights.exception;

public class ProviderException extends RuntimeException {
    public ProviderException(String message) {
        super(message);
    }

    public ProviderException(Throwable cause) {
        super(cause);
    }
}