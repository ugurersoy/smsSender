package com.acme.customization.client;

public class EntityParameter {
	
	public String parameter;
	public String paramName;
	
	

	public EntityParameter(String parameter, String paramName) {
		this.parameter = parameter;
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	
	
}
