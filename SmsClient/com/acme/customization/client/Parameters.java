package com.acme.customization.client;

public enum Parameters {
	NAME("<.Abone Adý.>"),SURNAME("<.Abone Soyadý.>"),PHONENUMBER("<.Abone Telefonu.>"),DATE("<.Tarih.>"),
	TIME("<.Saat.>"),ACCOUNTCODE("<.Cari Hesap Kodu.>"),ACCOUNTTITLE("<.Cari Hesap Ünvaný.>"),
	ACCOUNTBALANCE("<.Cari Hesap Bakiyesi.>"), PERSONCODE("<.Personel Sicil No.>"),PERSONNAME("<.Personel Adý.>"),
	PERSONSURNAME("<.Personel Soyadý.>");
	
	private String parameterName;

	
	private Parameters(String parameterName) {
		this.parameterName=parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
