package com.acme.customization;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.acme.enums.Parameters;
import com.acme.events.OnInitializeEvent;
import com.java.net.maradit.api.Maradit;
import com.java.net.maradit.api.Response;
import com.java.net.maradit.api.SubmitResponse;
import com.lbs.controls.maskededit.JLbsTextEdit;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class BatchSMSSendingHandler {

	private OnInitializeEvent initialize;
	private Parameters parameter[] = Parameters.values();
	
	public void onInitialize(JLbsXUIControlEvent event)
	{
	initialize=new OnInitializeEvent();
    initialize.getterParameter(parameter, event, 200);
	
	}

}
