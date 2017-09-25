package com.invoiceme.exceptions;

public class ListaProductosVacia extends Exception {
	private static final long serialVersionUID = -7093395127661232747L;
	private String mensaje;

	public ListaProductosVacia() {
		this.mensaje = "La lista de productos introducida está vacía. Por favor revisa la carga de la colección.";
	}

	public ListaProductosVacia(String message) {
		super(message);
		this.mensaje = message;
	}

	public String getMessage() {
		return this.mensaje;
	}
}