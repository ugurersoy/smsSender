package com.acme.customization;

public class MessageSplitControl {
	
	private String[] par;
	
	public String[] splitControl(String message)
	{
		par = message.split("([\'<'\'>'])");
	
		return par;
	}

}
