package com.invoiceme.dto;

import java.io.Serializable;
import java.util.Date;

public class DeviceRegistered implements Serializable {

	private int id_deviceRegistered;
	private String user;
	private String tokenDevice;
	private Date registeredDate;
	private String appVersion;
	
	public DeviceRegistered() {
		super();
	}
	public DeviceRegistered(int id_deviceRegistered, String user, String tokenDevice, Date registeredDate,
			String appVersion) {
		super();
		this.id_deviceRegistered = id_deviceRegistered;
		this.user = user;
		this.tokenDevice = tokenDevice;
		this.registeredDate = registeredDate;
		this.appVersion = appVersion;
	}
	public int getId_deviceRegistered() {
		return id_deviceRegistered;
	}
	public void setId_deviceRegistered(int id_deviceRegistered) {
		this.id_deviceRegistered = id_deviceRegistered;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getTokenDevice() {
		return tokenDevice;
	}
	public void setTokenDevice(String tokenDevice) {
		this.tokenDevice = tokenDevice;
	}
	public Date getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	
}
