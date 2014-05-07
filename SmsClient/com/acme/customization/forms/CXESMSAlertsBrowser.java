package com.acme.customization.forms;

import com.lbs.data.query.QueryBusinessObject;
import com.lbs.remoteclient.IClientContext;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIDataGridEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class CXESMSAlertsBrowser{

	public void onInitialize(JLbsXUIControlEvent event)
	{
		/** onInitialize : This is the initialization method for XUI forms. The method is called when the form and its components are created and ready to display. Event parameter object (JLbsXUIControlEvent) contains the form object (JLbsXUIPane) in 'component' and 'container' properties, and form's data in 'data' property. This method is meant to be void (no return value is expected). */
		System.out.println("initiliazed..");
	}

	public void onGridCanCreateObject(JLbsXUIDataGridEvent event)
	{
		event.getClientContext().setVariable("ALERT", 1);
	}


	public void onGridCanDuplicateObject(JLbsXUIDataGridEvent event)
	{
		event.getClientContext().setVariable("ALERT", 1);
	}

	public void onGridCanUpdateObject(JLbsXUIDataGridEvent event)
	{
		event.getClientContext().setVariable("ALERT", 1);
	}
	
	public boolean openSMSBatches(ILbsXUIPane container, Object data,
			IClientContext context) {
		
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		QueryBusinessObject alert = container.getSelectedGridData(100);
		if (alert != null)
		{
			info.setQueryParamValue("P_ALERTREF", alert.getProperties().getValue("LOGICALREF"));
			container.openChild("Forms/CXFSMSBatchOperationBrowser.lfrm", info, true, JLbsXUITypes.XUIMODE_DEFAULT);
		}
		return true;	
	}

	public void onGridGetCellValue(JLbsXUIGridEvent event)
	{
		if(event.getColumnTag() == 1006)
		{
			QueryBusinessObject qbo = (QueryBusinessObject) event.getData();
			Integer period = (Integer) qbo.getProperties().getValue("PERIOD");
			switch (period) {
			case 0:
				event.setReturnObject("");
				return ;
			case 1:
				event.setReturnObject("1 Ay'da 1");
				return;
			case 3:
				event.setReturnObject("3 Ay'da 1");
				return;
			case 6:
				event.setReturnObject("6 Ay'da 1");
				return;
			case 12:
				event.setReturnObject("12 Ay'da 1");
				return;
			default:
				break;
			}
		}
	}


}
