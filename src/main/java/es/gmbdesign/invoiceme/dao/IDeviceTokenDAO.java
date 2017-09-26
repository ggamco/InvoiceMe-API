package es.gmbdesign.invoiceme.dao;

import java.io.Serializable;

public interface IDeviceTokenDAO extends Serializable{

	/**
	 * M�todo para almacenar los tokenDevice en la bbdd mysql
	 * 
	 * @param tokenDevice
	 * @return
	 */
	boolean almacenarTokenDevice(String tokenDevice);
	
}
