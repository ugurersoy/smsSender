package com.acme.customization.client;

public enum ParameterName {
	
	P1("P1"),P2("P2"),P3("P3"),P4("P4"),P5("P5"),P6("P6"),P7("P7"),P8("P8"),P9("P9"),P10("P10"),P11("P11");
	
	private String name;

	private ParameterName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
