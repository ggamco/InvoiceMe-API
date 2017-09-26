package es.gmbdesign.invoiceme.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import es.gmbdesign.invoiceme.dto.Documento;

public class RegisterDeviceOnAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		StringBuilder sb = new StringBuilder();
		String line;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		RegisterDeviceOnAPI device = null;
		
		try{
			while((line = req.getReader().readLine()) != null){
				sb.append(line);
			}
			device = (RegisterDeviceOnAPI) gson.fromJson(sb.toString(), RegisterDeviceOnAPI.class);
			//TODO: almacenar el objeto en base de datos para auditoria.
			
			resp.setStatus(HttpServletResponse.SC_OK);
		}catch(Exception e){
			System.out.println("exception: " + e.getMessage());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			//TODO: Cerrar recursos de acceso a base de datos.
		}
	}

}
