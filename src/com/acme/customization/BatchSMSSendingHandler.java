package com.acme.customization;

import javax.swing.JOptionPane;

import com.acme.enums.Parameters;
import com.acme.events.DoubleClickOnGridEvent;
import com.acme.events.OnClickButtonEvent;
import com.acme.events.OnInitializeEvent;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.util.StringUtil;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class BatchSMSSendingHandler {

	private OnInitializeEvent initialize;
	private Parameters parameter[] = Parameters.values();
	
	public void onInitialize(JLbsXUIControlEvent event)
	{
	initialize=new OnInitializeEvent();
    initialize.getterParameter(parameter, event, 200);
	
	}

	public void ParameterOnGridCellDblClick(JLbsXUIGridEvent event)
	{
		DoubleClickOnGridEvent doubleClick = new DoubleClickOnGridEvent();
		doubleClick.addDoubleClickOnText(event,3001,200);
	}

	public void ParameterOnClick(JLbsXUIControlEvent event)
	{
		OnClickButtonEvent click= new OnClickButtonEvent();
		click.addParameterOnGrid(event,3001,200);
	}

	public void SendSmsOnClick(JLbsXUIControlEvent event)
	{
		JLbsXUIPane container = event.getContainer();
	    String messageMain=	((JLbsEditorPane)((com.lbs.controls.JLbsScrollPane)container.getComponentByTag(3001)).getInnerComponent()).getText();
		if(!messageMain.isEmpty()){
	    MessageSplitControl control= new MessageSplitControl();
		String strlist[]=control.splitControl(messageMain);
		for(int i=0;i<strlist.length;i++)
		{
			if(StringUtil.equals(strlist[i],"adý"))
			{
				strlist[i]="ugur";
				JOptionPane.showMessageDialog(null,strlist[i]);
			}
			if(StringUtil.equals(strlist[i],"Soyadý"))
			{
				
			}
			if(StringUtil.equals(strlist[i],"Telfon Numarasý"))
			{
				
			}
			if(StringUtil.equals(strlist[i],"Tarih"))
			{
				
			}
			if(StringUtil.equals(strlist[i],"Saat"))
			{
				
			}
			if(StringUtil.equals(strlist[i],"Cari Hesap Kodu"))
			{
				
			}
			if(StringUtil.equals(strlist[i],"Cari Hesap Unvaný"))
			{
				
			}
			if(StringUtil.equals(strlist[i],"Cari Hesap Bakiyesi"))
			{
				
			}
		}
		
		}else 
		{
		  //TO DO UYAR MESAJI EKLENECEK
		}
	}

}
