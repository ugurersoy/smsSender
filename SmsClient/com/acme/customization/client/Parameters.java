package com.acme.customization.client;

public enum Parameters {
	NAME("<.adý.>"),SURNAME("<.Soyadý.>"),PHONENUMBER("<.Telfon Numarasý.>"),DATE("<.Tarih.>"),
	TIME("<.Saat.>"),ACCOUNTCODE("<.Cari Hesap Kodu.>"),ACCOUNTTITLE("<.Cari Hesap Unvaný.>"),
	ACCOUNTBALANCE("<.Cari Hesap Bakiyesi.>");
	
	private String parameterName;

	
	private Parameters(String parameterName) {
		this.parameterName=parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
