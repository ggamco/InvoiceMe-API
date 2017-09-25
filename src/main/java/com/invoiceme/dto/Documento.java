package com.invoiceme.dto;

import com.invoiceme.dto.Emisor;
import com.invoiceme.dto.Producto;
import com.invoiceme.dto.Receptor;
import com.invoiceme.utiles.TipoDocumento;

import java.util.List;

public class Documento {
	private int tipoDocumento;
	private int numeroDocumento;
	private String fechaEmision;
	private String fechaValidez;
	private String logo;
	private String nota;
	private Emisor emisor;
	private Receptor receptor;
	private List<Producto> listaProductos;

	public Documento() {
	}

	public Documento(int tipoDocumento, int numeroDocumento, String fechaEmision, String fechaValidez, String logo,
			String nota, Emisor emisor, Receptor receptor, List<Producto> listaProductos) {
		this.tipoDocumento = tipoDocumento;
		this.numeroDocumento = numeroDocumento;
		this.fechaEmision = fechaEmision;
		this.fechaValidez = fechaValidez;
		this.logo = logo;
		this.nota = nota;
		this.emisor = emisor;
		this.receptor = receptor;
		this.listaProductos = listaProductos;
	}

	public String getNota() {
		return this.nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public String getFechaEmision() {
		return this.fechaEmision;
	}

	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public String getFechaValidez() {
		return this.fechaValidez;
	}

	public void setFechaValidez(String fechaValidez) {
		this.fechaValidez = fechaValidez;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getNumeroDocumento() {
		return this.numeroDocumento;
	}

	public void setNumeroDocumento(int numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public TipoDocumento getTipoDocumento() {
		TipoDocumento type = TipoDocumento.UNKWOWN;
		switch (this.tipoDocumento) {
			case 0 :
				type = TipoDocumento.PRESUPUESTO;
				break;
			case 1 :
				type = TipoDocumento.FACTURA;
		}

		return type;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Emisor getEmisor() {
		return this.emisor;
	}

	public void setEmisor(Emisor emisor) {
		this.emisor = emisor;
	}

	public Receptor getReceptor() {
		return this.receptor;
	}

	public void setReceptor(Receptor receptor) {
		this.receptor = receptor;
	}

	public List<Producto> getListaProductos() {
		return this.listaProductos;
	}

	public void setListaProductos(List<Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}
}