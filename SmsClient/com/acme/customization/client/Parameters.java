package com.acme.customization.client;

public enum Parameters {
	NAME("<.Abone Ad�.>"),SURNAME("<.Abone Soyad�.>"),PHONENUMBER("<.Abone Telefonu.>"),DATE("<.Tarih.>"),
	TIME("<.Saat.>"),ACCOUNTCODE("<.Cari Hesap Kodu.>"),ACCOUNTTITLE("<.Cari Hesap �nvan�.>"),
	ACCOUNTBALANCE("<.Cari Hesap Bakiyesi.>");
	
	private String parameterName;

	
	private Parameters(String parameterName) {
		this.parameterName=parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
