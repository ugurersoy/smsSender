package com.acme.customization.forms;

import javax.swing.JOptionPane;

import com.acme.customization.client.DoubleClickOnGridEvent;
import com.acme.customization.client.MessageSplitControl;
import com.acme.customization.client.OnClickButtonEvent;
import com.acme.customization.client.OnInitializeEvent;
import com.acme.customization.client.Parameters;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;

public class CXEMessageTemplate extends JLbsCustomXUIEventListener {
	private Parameters parameter[] = Parameters.values();
	private OnInitializeEvent  initialize;
	public CXEMessageTemplate() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event) {
		initialize=new OnInitializeEvent();
		initialize.getterParameter(parameter, event, 3000005);
	}

	public void parameterOnClick(JLbsXUIControlEvent event) {
		OnClickButtonEvent click= new OnClickButtonEvent();
		click.addParameterOnGrid(event,3000002 , 3000005,null,null);

	}

	public void selectParameterDoubleClick(JLbsXUIGridEvent event)
	{
		DoubleClickOnGridEvent doubleClick = new DoubleClickOnGridEvent();
		doubleClick.addDoubleClickOnText(event, 3000002, 3000005,null,null);
	}

	public void onClick(JLbsXUIControlEvent event)
	{
		String mes="aaaaa sdf<adý> bbbbbb sdfdsf<soyadý> cccccc <tr> dddddd";
		
		MessageSplitControl control= new MessageSplitControl();
		String strlist[]=control.splitControl(mes);
		
		for(String str : strlist)
		{
			JOptionPane.showMessageDialog(null, str);
		}
		
	}

}
