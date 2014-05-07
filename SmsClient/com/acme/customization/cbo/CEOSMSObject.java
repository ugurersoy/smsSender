package com.acme.customization.cbo;

import java.io.Serializable;

public class CEOSMSObject implements Serializable {

	private static final long serialVersionUID = 1L; 

	private String message = "";
	private String phoneNumber = "";
	private String title = "";
	private String error = "";
	private String statusDesc = "";
	
	public CEOSMSObject() {
		super();
	}
	
	public CEOSMSObject(String message, String phoneNumber, String title) {
		super();
		this.message = message;
		this.phoneNumber = phoneNumber;
		this.title = title;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
}
