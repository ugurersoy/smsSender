package com.acme.customization.client;

import java.util.ArrayList;

import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class OnInitializeEvent {
	

  	/**
  	 *  Bu metod Parametreleri  "Parameters" enumundan çaðýrýp messageTemplate ve BatshSmsSending 
  	 *ekranlarýnda bullunan gridin üzerine yerleþtirmek için oluþturulmuþtur.  
  	 **/
	public void getterParameter(Parameters parameter[],JLbsXUIControlEvent event,int tagNumber,ParameterName[] parameterName)
	{
		JLbsXUIPane container = event.getContainer();

		ArrayList parameterList = new ArrayList();
		ArrayList<String> paramList =new ArrayList();
		
		int size=0;
		for(ParameterName param:parameterName)
		{  
			parameterList.add(new EntityParameter(event.getContainer().getMessage(500051,size),param.getName()));
			size++;
		}
		
//		for (Parameters param : parameter) {
//			parameterList.add(new EntityParameter(param.getParameterName()));
//		}
		
		JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
				.getComponentByTag(tagNumber);
		objectGrid.setObjectClass(parameterList.getClass());
		objectGrid.setObjects(parameterList);
	}
    


}
