package es.gmbdesign.invoiceme.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import es.gmbdesign.invoiceme.dao.IDeviceTokenDAO;
import es.gmbdesign.invoiceme.dto.DeviceRegistered;
import es.gmbdesign.invoiceme.exceptions.BackendDAOException;

public class DeviceTokenDAOImpl implements IDeviceTokenDAO {
	
	private static final long serialVersionUID = -6552678204368822826L;
	private static final String SQL_INSERT_TOKENDEVICE = "INSERT INTO deviceRegistered (user, tokenDevice, appVersion) VALUES (?, ?, ?)";
	private final Logger logger = Logger.getLogger(DeviceTokenDAOImpl.class);
	
	@Override
	public boolean almacenarTokenDevice(Connection connection, DeviceRegistered device) throws BackendDAOException, SQLException {
		boolean respuesta = false;
		if (device != null) {
			logger.debug("Preparando la llamada para almacenar en el dispositivo en base de datos para el usuario: " + device.getUser());
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(SQL_INSERT_TOKENDEVICE);
				ps.setString(1, device.getUser());
				ps.setString(2, device.getTokenDevice());
				ps.setString(3, device.getAppVersion());
				respuesta = ps.executeUpdate() > 0;
			} catch(SQLException ex) {
				logger.warn("Se ha producido un error intentando registrar un dispositivo en la base de datos.");
				throw new BackendDAOException("Imposible registar dispositivo en el sistema", ex);
			} finally {
				ps.close();
			}
		}
		return respuesta;
	}

}
