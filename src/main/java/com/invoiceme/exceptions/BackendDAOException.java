package com.invoiceme.exceptions;

public class BackendDAOException extends Exception {

    public BackendDAOException(String message, Throwable exception) {
        super(message, exception);
    }
}
