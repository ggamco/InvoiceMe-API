package com.invoiceme.dto;

public class Receptor {
	private String nombre;
	private String direccion;
	private int zipCode;
	private String ciudad;
	private String cif;
	private String email;

	public Receptor() {
	}

	public Receptor(String nombre, String direccion, int zipCode, String ciudad, String cif, String email) {
		this.nombre = nombre;
		this.direccion = direccion;
		this.zipCode = zipCode;
		this.ciudad = ciudad;
		this.cif = cif;
		this.email = email;
	}

	public String[] toArray() {
		String[] cliente = new String[]{this.nombre, this.direccion, this.getDireccionLineaDos(), this.cif};
		return cliente;
	}

	private String getDireccionLineaDos() {
		return this.zipCode + " - " + this.ciudad;
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}