package com.acme.customization;

import java.util.ArrayList;
import java.util.List;

import com.java.net.maradit.api.Maradit;
import com.java.net.maradit.api.Response;
import com.java.net.maradit.api.SubmitResponse;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class FormHandler {

	public FormHandler() {
		
	}

	public void testSendSmsOnClick(JLbsXUIControlEvent event)
	{
		 Maradit maradit = new Maradit("test", "test");
	        maradit.validityPeriod = 120;
	        
	        List<String> to = new ArrayList<String>();
	        to.add("905367107577");
	    
	        SubmitResponse response = maradit.submit(to, "test mesajý");
	        printResponse(response);
	}
	
	public static void printResponse(Response response) {
        System.out.println("Data post status:" + response.status);
        System.out.println("Data post error (if status false):" + response.error);
        System.out.println("Gateway status code:" + response.statusCode);
        System.out.println("Gateway status description:" + response.statusDescription);
        System.out.println("Raw response xml:" + response.xml);
    }

}
