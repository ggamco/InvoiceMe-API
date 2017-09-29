package es.gmbdesign.invoiceme.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import es.gmbdesign.invoiceme.dto.Documento;
import es.gmbdesign.invoiceme.exceptions.ListaProductosVacia;
import es.gmbdesign.invoiceme.utiles.FacturaTemplate;

public class PDF extends HttpServlet {

	private static final long serialVersionUID = 3060041056106176275L;
	private final Logger logger = Logger.getLogger(PDF.class);

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fecha = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		StringBuilder sb = new StringBuilder();
		Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
		Documento documento = null;
		try {
			req.setCharacterEncoding("UTF-8");
			String line = null;
			while ((line = req.getReader().readLine()) != null) {
				sb.append(line);
			}
			documento = (Documento) gson.fromJson(sb.toString(), Documento.class);
		} catch (Exception ex) {
			logger.error("Se ha producido un error al leer el json de la request.", ex);
		}
		String path = this.getServletContext().getRealPath("/template");
		FacturaTemplate factura = new FacturaTemplate(path);
		ByteArrayOutputStream baos = null;
		ServletOutputStream out = null;
		try {
			baos = factura.CrearDocumento(documento);
			out = resp.getOutputStream();
			StringBuilder ex = (new StringBuilder()).append(documento.getNumeroDocumento()).append("_").append(fecha)
					.append("_").append(documento.getTipoDocumento().tipo()).append(".pdf");
			resp.setHeader("Expires", "0");
			resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			resp.setHeader("Pragma", "public");
			resp.addHeader("Content-Disposition", "attachment; filename=" + ex);
			resp.setContentType("application/pdf");
			resp.setContentLength(baos.size());
			baos.writeTo(out);
			out.flush();
		} catch (ListaProductosVacia ex) {
			logger.error("El json no contiene productos que facturar.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getLocalizedMessage());;
		} finally {
			baos.close();
			out.close();
		}
	}
}
/*ESTRUCTURA JSON DATOS ENVIADOS
{
	"tipoDocumento": 1,
	"numeroDocumento": 18,
	"sufijo": "SEP2017",
	"fechaEmision": "30/09/2017",
	"fechaValidez": "06/10/2017",
	"nota": " Factura libre de IVA segun el Articulo 20 apartado 1 numero 9 de la LEY 37/92 del 28 de Diciembre de 1992",
	"logo": "base64",
	"emisor": {
		"nombre": "Gustavo Gamboa Cordero",
		"direccion": "Calle de la Aurora 29",
		"zipCode": 28760,
		"ciudad": "Tres Cantos",
		"cif": "75891423W",
		"iban": "ES50 2108 2838 4200 3343 8369"
	},
	"receptor": {
		"nombre": "CICE S.A.",
		"direccion": "Calle Povedilla 4",
		"zipCode": 28009,
		"ciudad": "Madrid",
		"cif": "A28683670",
		"email": "emma@ciceonline.com"
	},
	"listaProductos": [
		{
			"codigo": "HJAVA",
			"descripcion": "Hora de clase impartida al grupo T119.",
			"cantidad": 28.00,
			"precio": 30.00,
			"IVA": 0.00,
			"IRPF": 7.00,
			"exentoIVA": true,
			"exentoIRPF": false
		},
		{
			"codigo": "HSEM",
			"descripcion": "Hora de Seminario impartido el dia 22 de Septiembre",
			"cantidad": 3.00,
			"precio": 30.00,
			"IVA": 0.00,
			"IRPF": 7.00,
			"exentoIVA": true,
			"exentoIRPF": false
		}
	]	
}
*/