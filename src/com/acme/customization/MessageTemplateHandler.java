package com.acme.customization;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.acme.entity.entityParameter;
import com.acme.enums.Parameters;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;

public class MessageTemplateHandler extends JLbsCustomXUIEventListener {
  private Parameters parameter[]=Parameters.values();
	
	public MessageTemplateHandler() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event)
	{
		JLbsXUIPane container = event.getContainer();
		
		
		ArrayList parameterList=new ArrayList();
	
		
		for(Parameters param : parameter)
		{
			parameterList.add(new entityParameter(param.getParameterName()));
		}
		
		JLbsObjectListGrid objectGrid= (JLbsObjectListGrid) container.getComponentByTag(3000005);
		objectGrid.setObjectClass(parameterList.getClass());
		objectGrid.setObjects(parameterList);
	
	}

	public void parameterOnClick(JLbsXUIControlEvent event)
	{
		JOptionPane.showMessageDialog(null, "test");
//		JLbsXUIPane container = event.getContainer();
//		
//		JLbsEditorPane messageTemplate = (JLbsEditorPane) container.getComponentByTag(3000002);
//		JLbsObjectListGrid objectGrid= (JLbsObjectListGrid) container.getComponentByTag(3000005);
//		
//        ArrayList list = (ArrayList) objectGrid.getDisplayList();
//	
//        for(int i=0;i<list.size();i++)
//        {
//        if(objectGrid.getSelectedRow()==list.indexOf(i))
//           {
//        	entityParameter param = (entityParameter) list.get(i); 
//        	messageTemplate.setText(param.parameter);
//           }
//        }
	}
	

}
