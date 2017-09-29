package es.gmbdesign.invoiceme.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.gmbdesign.invoiceme.bbdd.ConnectionHandler;
import es.gmbdesign.invoiceme.dao.IDeviceTokenDAO;
import es.gmbdesign.invoiceme.dao.impl.DeviceTokenDAOImpl;
import es.gmbdesign.invoiceme.dto.DeviceRegistered;
import es.gmbdesign.invoiceme.exceptions.BackendDAOException;
import es.gmbdesign.invoiceme.utiles.JWTUtil;

public class RegisterDeviceOnAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(RegisterDeviceOnAPI.class);
       
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		StringBuilder sb = new StringBuilder();
		String line;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DeviceRegistered device = null;
		
		try{
			while((line = req.getReader().readLine()) != null){
				sb.append(line);
			}
			device = (DeviceRegistered) gson.fromJson(sb.toString(), DeviceRegistered.class);
			if (registarDispositivoEnAPI(device)) {
				resp.setHeader("Authorization", JWTUtil.createJWT(device, "user"));
				resp.setStatus(HttpServletResponse.SC_CREATED);
			} else {
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}	
		}catch(Exception e){
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	private boolean registarDispositivoEnAPI(DeviceRegistered device) {
		boolean resultado = false;
		ConnectionHandler connectionHandler = new ConnectionHandler();
		Connection connection = null;
		IDeviceTokenDAO deviceTokenDAO = new DeviceTokenDAOImpl();
		try {
			connection = connectionHandler.getConnection(ConnectionHandler.DB_SCHEME);
			if (deviceTokenDAO.almacenarTokenDevice(connection, device)) {
				resultado = true;
			}
		} catch (BackendDAOException ex) {
			logger.error(ex.getLocalizedMessage());
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		} finally {
			ConnectionHandler.closeConnection(connection);
		}
		return resultado;
	}
}
