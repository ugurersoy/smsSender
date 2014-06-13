package com.acme.customization.client;

public enum Parameters {
	NAME("<.Abone Ad�.>"),SURNAME("<.Abone Soyad�.>"),PHONENUMBER("<.Abone Telefonu.>"),DATE("<.Tarih.>"),
	TIME("<.Saat.>"),ACCOUNTCODE("<.Cari Hesap Kodu.>"),ACCOUNTTITLE("<.Cari Hesap �nvan�.>"),
	ACCOUNTBALANCE("<.Cari Hesap Bakiyesi.>"), PERSONCODE("<.Personel Sicil No.>"),PERSONNAME("<.Personel Ad�.>"),
	PERSONSURNAME("<.Personel Soyad�.>");
	
	private String parameterName;

	
	private Parameters(String parameterName) {
		this.parameterName=parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
