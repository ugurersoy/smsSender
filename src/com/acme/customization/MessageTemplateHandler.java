package com.acme.customization;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.databinding.types.soapencoding.Array;

import com.acme.enums.Parameters;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class MessageTemplateHandler extends JLbsCustomXUIEventListener {
  private Parameters parameter[];
	
	public MessageTemplateHandler() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event)
	{
		JLbsXUIPane container = event.getContainer();
		
		JLbsObjectListGrid objectGrid= (JLbsObjectListGrid) container.getComponentByTag(3000005);
	 
		ArrayList<String> parameterList=new ArrayList();
		
		for(Parameters param : parameter)
		{
			parameterList.add(param.getParameterName());
		}

		objectGrid.setObjectClass(parameterList.getClass());
	
	}
	

}
