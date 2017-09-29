package es.gmbdesign.invoiceme.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import es.gmbdesign.invoiceme.utiles.JWTUtil;

public class LoginDeviceOnAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(LoginDeviceOnAPI.class);
       
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.warn("Intento de acceso al api a un metodo no permitido desde IP: " + req.getRemoteHost());
    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String authTokenRequest = req.getHeader("Authorization");
		if (authTokenRequest != null) {
			if (JWTUtil.validateJWT(authTokenRequest)) {
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "El token proporcionado no es valido");
			}
		} else {
			//TODO: recuperar tokendevice y consultar el usuario vinculado. Si se encuentra se crea un nuevo token para la respuesta
			//TODO: Si no se encuentra, el usuario hace login desde otro dispositivo por lo que hay que registrarlo.
		}
	}

}
