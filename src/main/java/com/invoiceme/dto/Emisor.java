package com.invoiceme.dto;

public class Emisor {
	private String nombre;
	private String direccion;
	private int zipCode;
	private String ciudad;
	private String cif;
	private String iban;
	private String email;
	private String telefono;

	public Emisor() {
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Emisor(String nombre, String direccion, int zipCode, String ciudad, String cif, String iban, String email,
			String telefono) {
		this.nombre = nombre;
		this.direccion = direccion;
		this.zipCode = zipCode;
		this.ciudad = ciudad;
		this.cif = cif;
		this.iban = iban;
		this.email = email;
		this.telefono = telefono;
	}

	public String[] toArray() {
		String[] datos = new String[]{this.nombre, this.cif, this.getDireccionDocumento(), this.iban};
		return datos;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public String getDireccionDocumento() {
		return this.direccion + ", " + this.zipCode + " - " + this.ciudad;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	public String getCiudad() {
		return this.ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getCif() {
		return this.cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}
}