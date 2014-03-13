package com.test.main;

import com.lbs.admin.ADXEEntityRights;
import com.lbs.controls.treegrid.ILbsTreeGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class TestTreeHandler {

	private ILbsTreeGrid m_EntityGrid;
	
	public TestTreeHandler() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event)
	{
	   JLbsXUIPane container = event.getContainer();
	   
	   m_EntityGrid = (ILbsTreeGrid) container.getComponentByTag(2000001);
	   
	 
		
		
	}

}
