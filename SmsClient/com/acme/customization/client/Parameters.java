package com.acme.customization.client;

public enum Parameters {
	NAME("<.ad�.>"),SURNAME("<.Soyad�.>"),PHONENUMBER("<.Telfon Numaras�.>"),DATE("<.Tarih.>"),
	TIME("<.Saat.>"),ACCOUNTCODE("<.Cari Hesap Kodu.>"),ACCOUNTTITLE("<.Cari Hesap Unvan�.>"),
	ACCOUNTBALANCE("<.Cari Hesap Bakiyesi.>");
	
	private String parameterName;

	
	private Parameters(String parameterName) {
		this.parameterName=parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
