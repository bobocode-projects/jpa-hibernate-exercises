package com.bobocode.exception;

public class DaoException extends RuntimeException {
    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
