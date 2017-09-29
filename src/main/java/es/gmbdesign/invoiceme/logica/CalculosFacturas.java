package es.gmbdesign.invoiceme.logica;

import es.gmbdesign.invoiceme.dto.Producto;
import es.gmbdesign.invoiceme.exceptions.ListaProductosVacia;

import java.util.Iterator;
import java.util.List;

public class CalculosFacturas {
	public static float calcularIVA(List<Producto> productos) throws ListaProductosVacia {
		float iva = 0.0F;
		if (productos != null) {
			Iterator<Producto> arg1 = productos.iterator();

			while (arg1.hasNext()) {
				Producto p = (Producto) arg1.next();
				if (!p.isExentoIVA()) {
					iva += p.getIVA() * p.getImporte() / 100.0F;
				}
			}

			return iva;
		} else {
			throw new ListaProductosVacia();
		}
	}

	public static float calcularIRPF(List<Producto> productos) throws ListaProductosVacia {
		float irpf = 0.0F;
		if (productos != null) {
			Iterator<Producto> arg1 = productos.iterator();

			while (arg1.hasNext()) {
				Producto p = (Producto) arg1.next();
				if (!p.isExentoIRPF()) {
					irpf += p.getIRPF() * p.getImporte() / 100.0F;
				}
			}

			return irpf;
		} else {
			throw new ListaProductosVacia();
		}
	}

	public static float calcularSUBTOTAL(List<Producto> productos) throws ListaProductosVacia {
		float total = 0.0F;
		if (productos == null) {
			throw new ListaProductosVacia();
		} else {
			Producto p;
			for (Iterator<Producto> arg1 = productos.iterator(); arg1.hasNext(); total += p.getImporte()) {
				p = (Producto) arg1.next();
			}

			return total;
		}
	}
}