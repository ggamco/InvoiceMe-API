package es.gmbdesign.invoiceme.dao;

import java.io.Serializable;
import java.sql.Connection;

import es.gmbdesign.invoiceme.dto.DeviceRegistered;

public interface IDeviceTokenDAO extends Serializable{

	/**
	 * M�todo para almacenar los tokenDevice en la bbdd mysql
	 * 
	 * @param connection conexi�n del pool
	 * @param tokenDevice token del dispositivo registrado
	 * @return
	 */
	boolean almacenarTokenDevice(Connection connection, DeviceRegistered device) throws Exception;
	
}
