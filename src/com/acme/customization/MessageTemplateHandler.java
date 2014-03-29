package com.acme.customization;

import javax.swing.JOptionPane;

import com.acme.enums.Parameters;
import com.acme.events.DoubleClickOnGridEvent;
import com.acme.events.OnClickButtonEvent;
import com.acme.events.OnInitializeEvent;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.controls.JLbsImageButton;

public class MessageTemplateHandler extends JLbsCustomXUIEventListener {
	private Parameters parameter[] = Parameters.values();
	private OnInitializeEvent  initialize;
	public MessageTemplateHandler() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event) {
		initialize=new OnInitializeEvent();
		initialize.getterParameter(parameter, event, 3000005);
	}

	public void parameterOnClick(JLbsXUIControlEvent event) {
		OnClickButtonEvent click= new OnClickButtonEvent();
		click.addParameterOnGrid(event,3000002 , 3000005);

	}

	public void selectParameterDoubleClick(JLbsXUIGridEvent event)
	{
		DoubleClickOnGridEvent doubleClick = new DoubleClickOnGridEvent();
		doubleClick.addDoubleClickOnText(event, 3000002, 3000005);
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
