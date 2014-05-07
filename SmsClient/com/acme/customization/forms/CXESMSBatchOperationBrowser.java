package com.acme.customization.forms;

import com.lbs.appobjects.GOBOBatch;
import com.lbs.data.grids.JLbsQuerySelectionGrid;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.remoteclient.IClientContext;
import com.lbs.transport.TransportUtil;
import com.lbs.unity.UnityHelper;
import com.lbs.util.QueryUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXESMSBatchOperationBrowser{

	public void onInitialize(JLbsXUIControlEvent event)
	{
		/** onInitialize : This is the initialization method for XUI forms. The method is called when the form and its components are created and ready to display. Event parameter object (JLbsXUIControlEvent) contains the form object (JLbsXUIPane) in 'component' and 'container' properties, and form's data in 'data' property. This method is meant to be void (no return value is expected). */
		System.out.println("initiliazed..");
	}
	
	/*public boolean viewSMSSendingForm(ILbsXUIPane container, Object data, IClientContext context) {
		
		JLbsQuerySelectionGrid grid = (JLbsQuerySelectionGrid) container.getComponentByTag(100);
		QueryBusinessObject selected = (QueryBusinessObject)grid.getSelectedObject();
		GOBOBatch batch = (GOBOBatch)UnityHelper.getBOByReference(context, GOBOBatch.class, QueryUtil.getIntProp(selected, "BatchID"));
		byte[] startPars = batch.getStartParameters();
		Object[] obj = null;
		CustomBusinessObject smsSendingObj = null;
		if (startPars != null && startPars.length > 0)
		{
			
			try {
				obj = TransportUtil.deserializeObjects(startPars);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (obj != null)
				for (int i = 0; i < obj.length; i++) {
					if (obj[i] instanceof CustomBusinessObject) {
						smsSendingObj = (CustomBusinessObject) obj[i];
					}
				}
		}
		if(smsSendingObj!=null)
		{
			boolean ok = container.openChild("Forms/BatchSMSSending.lfrm", smsSendingObj, true, JLbsXUITypes.XUIMODE_VIEWONLY);
			if (!ok)
				return true;
		}
		return true;
	}
*/

}
