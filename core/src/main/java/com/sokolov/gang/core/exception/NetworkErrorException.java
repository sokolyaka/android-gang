package com.sokolov.gang.core.exception;

public class NetworkErrorException extends RuntimeException {
    public NetworkErrorException() {
    }

    public NetworkErrorException(String message) {
        super(message);
    }

    public NetworkErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkErrorException(Throwable cause) {
        super(cause);
    }

    public NetworkErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
