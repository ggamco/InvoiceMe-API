package com.invoiceme.utiles;

import com.invoiceme.dto.Documento;
import com.invoiceme.dto.Producto;
import com.invoiceme.exceptions.ListaProductosVacia;
import com.invoiceme.logica.CalculosFacturas;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Base64;

public class FacturaTemplate {
	private static PdfWriter writer;
	private DecimalFormat df = new DecimalFormat("0.00");
	private float SUBTOTAL;
	private float IVA;
	private float IRPF;
	private float TOTAL;
	private ByteArrayOutputStream baos;
	private Document document;
	private Font fuenteRegular;
	private Font fuenteSemiBold;

	public FacturaTemplate(String path) {
		FontFactory.register(path + "/font/Open_Sans/OpenSans-Light.ttf", "OpenSans_light");
		FontFactory.register(path + "/font/Open_Sans/OpenSans-Regular.ttf", "OpenSans_regular");
		FontFactory.register(path + "/font/Open_Sans/OpenSans-Semibold.ttf", "OpenSans_semibold");
		FontFactory.register(path + "/font/Open_Sans/OpenSans-Bold.ttf", "OpenSans_bold");
		this.fuenteRegular = FontFactory.getFont("OpenSans_regular", "Identity-H", true, 10.0F);
		this.fuenteSemiBold = FontFactory.getFont("OpenSans_semibold", "Identity-H", true, 10.0F);
	}

	public ByteArrayOutputStream CrearDocumento(Documento documento) throws ListaProductosVacia {
		this.baos = new ByteArrayOutputStream();
		this.document = new Document();

		try {
			writer = PdfWriter.getInstance(this.document, this.baos);
		} catch (DocumentException arg4) {
			System.out.println("Error: " + arg4.getLocalizedMessage());
		}

		this.document.open();

		try {
			this.addTitulo(documento.getTipoDocumento().tipo());
			this.cargarLogo(documento);
		} catch (Exception arg3) {
			arg3.printStackTrace();
		}

		this.crearGraficos();
		this.crearTablaProductos();

		try {
			this.crearTablaDesglose(documento);
			this.cargarTextosBase(documento.getTipoDocumento().tipo());
			this.cargarTextosFactura(documento);
			this.addProductos(documento);
		} catch (Exception arg2) {
			arg2.printStackTrace();
		}

		this.document.close();
		return this.baos;
	}

	private void addTitulo(String titulo) {
		this.document.addTitle(titulo);
	}

	private void cargarLogo(Documento documento) {
		try {
			Image ex = Image.getInstance(Base64.getDecoder().decode(documento.getLogo()));
			float aspectRatio = ex.getWidth() * 60.0F / ex.getHeight();
			ex.scaleAbsolute(aspectRatio, 60.0F);
			ex.setAbsolutePosition(36.0F, 750.0F);
			this.document.add(ex);
		} catch (BadElementException arg3) {
			System.out.println("Error: " + arg3.getLocalizedMessage());
		} catch (IOException arg4) {
			System.out.println("Error: " + arg4.getLocalizedMessage());
		} catch (DocumentException arg5) {
			System.out.println("Error: " + arg5.getLocalizedMessage());
		}

	}

	private void crearGraficos() {
		PdfContentByte cb = writer.getDirectContent();
		Rectangle rec = new Rectangle(0.0F, 540.0F, 596.0F, 720.0F);
		rec.setBackgroundColor(new GrayColor(0.9F));
		rec.setBorderColorTop(new GrayColor(0.8F));
		rec.setBorderWidthTop(4.0F);
		cb.rectangle(rec);
		float rotate = 45.0F;
		float width = 22.0F;
		float height = 22.0F;
		float x = 298.0F;
		float y = 528.0F;
		float angle = (float) ((double) (-rotate) * 0.017453292519943295D);
		float fxScale = (float) Math.cos((double) angle);
		float fyScale = (float) Math.cos((double) angle);
		float fxRote = (float) (-Math.sin((double) angle));
		float fyRote = (float) Math.sin((double) angle);
		PdfTemplate template = writer.getDirectContent().createTemplate(width, height);
		Rectangle rombo = new Rectangle(0.0F, 0.0F, width, height);
		template.rectangle(0.0F, 0.0F, width, height);
		rombo.setBackgroundColor(new GrayColor(0.9F));
		template.rectangle(rombo);
		template.fill();
		writer.getDirectContent().addTemplate(template, fxScale, fxRote, fyRote, fyScale, x, y);
	}

	private void cargarTextosBase(String tipo) {
		System.out.println(tipo);
		this.cargarTituloDocumento(tipo);
		this.cargarTextosReferencias(tipo);
	}

	private void cargarTituloDocumento(String tipo) {
		PdfContentByte title = writer.getDirectContent();
		Font fontTitulo = FontFactory.getFont("OpenSans_light", "Identity-H", true, 0.0F);
		BaseFont bfTitulo = fontTitulo.getBaseFont();
		title.saveState();
		title.beginText();
		title.setFontAndSize(bfTitulo, 22.0F);
		title.showTextAligned(2, tipo, 570.0F, 690.0F, 0.0F);
		title.endText();
		title.restoreState();
	}

	private void cargarTextosReferencias(String tipo) {
		PdfContentByte ref = writer.getDirectContent();
		System.out.println(tipo);
		BaseFont bfTitulo = this.fuenteSemiBold.getBaseFont();
		ref.saveState();
		ref.beginText();
		ref.setFontAndSize(bfTitulo, 10.0F);
		ref.setColorFill(new GrayColor(0.2F));
		ref.showTextAligned(0, tipo + " Nº:", 36.0F, 700.0F, 0.0F);
		ref.showTextAligned(0, "Fecha:", 36.0F, 685.0F, 0.0F);
		ref.showTextAligned(0, "Nombre:", 36.0F, 670.0F, 0.0F);
		ref.showTextAligned(0, "NIF:", 36.0F, 655.0F, 0.0F);
		ref.showTextAligned(0, "Dirección:", 36.0F, 640.0F, 0.0F);
		ref.showTextAligned(0, "Cuenta IBAN:", 36.0F, 625.0F, 0.0F);
		ref.showTextAligned(0, "Cliente:", 36.0F, 600.0F, 0.0F);
		ref.endText();
		ref.restoreState();
	}

	public void cargarTextosFactura(Documento documento) {
		String[] datos = documento.getEmisor().toArray();
		String[] cliente = documento.getReceptor().toArray();
		String numeroDocumento = String.valueOf(documento.getNumeroDocumento());
		String fechaEmision = documento.getFechaEmision();
		PdfContentByte ref = writer.getDirectContent();
		BaseFont bf = this.fuenteRegular.getBaseFont();
		ref.saveState();
		ref.beginText();
		ref.setFontAndSize(bf, 10.0F);
		ref.setColorFill(new GrayColor(0.35F));
		ref.showTextAligned(0, numeroDocumento, 136.0F, 700.0F, 0.0F);
		ref.showTextAligned(0, fechaEmision, 136.0F, 685.0F, 0.0F);

		int i;
		for (i = 0; i < datos.length; ++i) {
			if (datos[i] != null) {
				ref.showTextAligned(0, datos[i], 136.0F, (float) (670 - i * 15), 0.0F);
			}
		}

		for (i = 0; i < cliente.length; ++i) {
			if (cliente[i] != null) {
				ref.showTextAligned(0, cliente[i], 136.0F, (float) (600 - i * 15), 0.0F);
			}
		}

		ref.endText();
		ref.restoreState();
	}

	private void crearTablaProductos() {
		PdfContentByte cb = writer.getDirectContent();
		Rectangle rec = new Rectangle(0.0F, 490.0F, 596.0F, 520.0F);
		rec.setBackgroundColor(BaseColor.WHITE);
		rec.setBorderColorTop(new GrayColor(0.8F));
		rec.setBorderColorBottom(new GrayColor(0.9F));
		rec.setBorderWidthTop(1.25F);
		rec.setBorderWidthBottom(0.5F);
		cb.rectangle(rec);
		PdfContentByte ref = writer.getDirectContent();
		BaseFont bfTitulo = this.fuenteRegular.getBaseFont();
		ref.setColorFill(new GrayColor(0.2F));
		ref.saveState();
		ref.beginText();
		ref.setFontAndSize(bfTitulo, 8.0F);
		ref.showTextAligned(0, "Código", 36.0F, 502.0F, 0.0F);
		ref.showTextAligned(0, "Descripción", 100.0F, 502.0F, 0.0F);
		ref.showTextAligned(2, "Cantidad", 430.0F, 502.0F, 0.0F);
		ref.showTextAligned(2, "Precio", 500.0F, 502.0F, 0.0F);
		ref.showTextAligned(2, "Importe", 570.0F, 502.0F, 0.0F);
		ref.endText();
		ref.restoreState();
	}

	private void crearTablaDesglose(Documento documento) {
		PdfContentByte cb = writer.getDirectContent();
		Rectangle rec = new Rectangle(0.0F, 60.0F, 596.0F, 200.0F);
		rec.setBackgroundColor(BaseColor.WHITE);
		rec.setBorderColorTop(new GrayColor(0.8F));
		rec.setBorderWidthTop(1.25F);
		cb.rectangle(rec);
		Rectangle rec2 = new Rectangle(370.0F, 60.0F, 580.0F, 95.0F);
		rec2.setBackgroundColor(new GrayColor(0.9F));
		cb.rectangle(rec2);
		PdfContentByte ref = writer.getDirectContent();
		BaseFont bfTitulo = this.fuenteRegular.getBaseFont();
		ref.setColorFill(new GrayColor(0.2F));
		ref.saveState();
		ref.beginText();
		ref.setFontAndSize(bfTitulo, 8.0F);
		ref.showTextAligned(0,
				"* Indica un concepto exento de impuestos. ** Indica un concepto parcialmente sujeto a impuestos",
				36.0F, 185.0F, 0.0F);
		ref.showTextAligned(0, "NOTA: " + documento.getNota(), 36.0F, 175.0F, 0.0F);
		ref.showTextAligned(2, "Subtotal", 430.0F, 150.0F, 0.0F);
		ref.showTextAligned(2, "IVA (21.00%)", 430.0F, 130.0F, 0.0F);
		ref.showTextAligned(2, "IRPF (7.00%)", 430.0F, 110.0F, 0.0F);
		bfTitulo = this.fuenteSemiBold.getBaseFont();
		ref.setFontAndSize(bfTitulo, 20.0F);
		ref.showTextAligned(2, "Total", 430.0F, 70.0F, 0.0F);
		ref.setFontAndSize(bfTitulo, 10.0F);
		ref.showTextAligned(0, "Firmado: " + documento.getEmisor().getNombre(), 36.0F, 70.0F, 0.0F);
		ref.endText();
		ref.restoreState();
	}

	private void addProductos(Documento documento) throws ListaProductosVacia {
		PdfContentByte ref = writer.getDirectContent();
		BaseFont bfTitulo = this.fuenteRegular.getBaseFont();
		ref.setColorFill(new GrayColor(0.2F));
		ref.saveState();
		ref.beginText();
		ref.setFontAndSize(bfTitulo, 8.0F);
		if (documento.getListaProductos() == null) {
			throw new ListaProductosVacia("Lista vacia");
		} else {
			for (int i = 0; i < documento.getListaProductos().size(); ++i) {
				Producto p = (Producto) documento.getListaProductos().get(i);
				ref.showTextAligned(0, p.getCodigo(), 36.0F, (float) (470 - i * 15), 0.0F);
				ref.showTextAligned(0, p.getDescripcion(), 100.0F, (float) (470 - i * 15), 0.0F);
				ref.showTextAligned(2, this.df.format((double) p.getCantidad()), 430.0F, (float) (470 - i * 15), 0.0F);
				ref.showTextAligned(2, this.df.format((double) p.getPrecio()).concat(" €"), 500.0F,
						(float) (470 - i * 15), 0.0F);
				String importe = this.df.format((double) p.getImporte()).concat(" €");
				if (p.isExentoIVA() | p.isExentoIRPF()) {
					if (p.isExentoIRPF() && p.isExentoIVA()) {
						importe = importe.concat(" **");
					} else {
						importe = importe.concat(" *");
					}
				}

				ref.showTextAligned(2, importe, 570.0F, (float) (470 - i * 15), 0.0F);
			}

			this.SUBTOTAL = CalculosFacturas.calcularSUBTOTAL(documento.getListaProductos());
			this.IVA = CalculosFacturas.calcularIVA(documento.getListaProductos());
			this.IRPF = CalculosFacturas.calcularIRPF(documento.getListaProductos());
			this.TOTAL = this.SUBTOTAL + this.IVA - this.IRPF;
			ref.showTextAligned(2, this.df.format((double) this.SUBTOTAL).concat(" €"), 570.0F, 150.0F, 0.0F);
			ref.showTextAligned(2, this.df.format((double) this.IVA).concat(" €"), 570.0F, 130.0F, 0.0F);
			ref.showTextAligned(2, this.df.format((double) this.IRPF).concat(" €"), 570.0F, 110.0F, 0.0F);
			bfTitulo = this.fuenteSemiBold.getBaseFont();
			ref.setFontAndSize(bfTitulo, 20.0F);
			ref.showTextAligned(2, this.df.format((double) this.TOTAL).concat(" €"), 570.0F, 70.0F, 0.0F);
			ref.endText();
			ref.restoreState();
		}
	}
}