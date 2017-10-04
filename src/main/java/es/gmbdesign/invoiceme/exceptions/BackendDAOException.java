package es.gmbdesign.invoiceme.exceptions;

public class BackendDAOException extends Exception {

	private static final long serialVersionUID = 6485885919099036520L;

	public BackendDAOException(String message, Throwable exception) {
        super(message, exception);
    }
}
