package es.gmbdesign.invoiceme.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.*;
import es.gmbdesign.invoiceme.bbdd.ConnectionHandler;
import es.gmbdesign.invoiceme.dao.IDeviceTokenDAO;
import es.gmbdesign.invoiceme.dao.impl.DeviceTokenDAOImpl;
import es.gmbdesign.invoiceme.dto.DeviceRegistered;
import es.gmbdesign.invoiceme.dto.Documento;
import es.gmbdesign.invoiceme.utiles.PropertyUtil;

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
		DeviceRegistered device = null;
		ConnectionHandler connectionHandler = null;
		Connection connection = null;
		IDeviceTokenDAO deviceTokenDAO = null;
		
		try{
			while((line = req.getReader().readLine()) != null){
				sb.append(line);
			}
			device = (DeviceRegistered) gson.fromJson(sb.toString(), DeviceRegistered.class);
			//TODO: almacenar el objeto en base de datos para auditoria.
			connectionHandler = new ConnectionHandler();
			deviceTokenDAO = new DeviceTokenDAOImpl();
			connection = connectionHandler.getConnection(ConnectionHandler.DB_SCHEME);
			deviceTokenDAO.almacenarTokenDevice(connection, device);
			resp.setHeader("Authorization", createJWT(device));
			resp.setStatus(HttpServletResponse.SC_OK);
		}catch(Exception e){
			System.out.println("exception: " + e.getMessage());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			//TODO: Cerrar recursos de acceso a base de datos.
			ConnectionHandler.closeConnection(connection);
		}
	}
	
	private String createJWT (DeviceRegistered device) {
		String JWT = null;
		JWT = Jwts.builder()
				.setSubject("invoiceMe-API")
				.claim("user", device.getUser())
				.claim("appVersion", device.getAppVersion())
				.claim("scope", "user")
				.signWith(SignatureAlgorithm.HS256, getApiKey())
				.compact();
		return JWT;
	}
	
	private byte[] getApiKey() {
		return PropertyUtil.getProperty("api.key").getBytes();
	}

}
