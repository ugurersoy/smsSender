package com.acme.events;

import java.util.ArrayList;

import com.acme.entity.entityParameter;
import com.acme.enums.Parameters;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class OnInitializeEvent {
	

  	/**
  	 *  Bu metod Parametreleri  "Parameters" enumundan çaðýrýp messageTemplate ve BatshSmsSending 
  	 *ekranlarýnda bullunan gridin üzerine yerleþtirmek için oluþturulmuþtur.  
  	 **/
	public void getterParameter(Parameters parameter[],JLbsXUIControlEvent event,int tagNumber)
	{
		JLbsXUIPane container = event.getContainer();

		ArrayList parameterList = new ArrayList();

		for (Parameters param : parameter) {
			parameterList.add(new entityParameter(param.getParameterName()));
		}

		JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
				.getComponentByTag(tagNumber);
		objectGrid.setObjectClass(parameterList.getClass());
		objectGrid.setObjects(parameterList);
	}
    


}
