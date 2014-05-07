package com.acme.customization.handlers;

import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.appobjects.GOBOBatch;
import com.lbs.data.factory.BasicBusinessLogicHandler;
import com.lbs.data.factory.FactoryParams;
import com.lbs.data.factory.IBusinessLogicFactory;
import com.lbs.data.objects.BasicBusinessObject;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.platform.interfaces.IServerContext;
import com.lbs.transport.TransportUtil;

public class CLHBatch extends BasicBusinessLogicHandler {
	
	@Override
	public boolean afterInsert(IBusinessLogicFactory factory,
			FactoryParams params, BasicBusinessObject parentObj,
			BasicBusinessObject obj) {
		
		GOBOBatch batch = (GOBOBatch) obj;
		if(batch.getOperationID() == ProjectGlobals.OPERTYPE_SMSALERT)
		{
			byte[] startPars = batch.getStartParameters();
			Object[] startParamObjs = null;
			if (startPars != null && startPars.length > 0)
			{
				try {
					startParamObjs = TransportUtil.deserializeObjects(startPars);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (startParamObjs != null)
				{
						CustomBusinessObject smsAlert = (CustomBusinessObject) startParamObjs[0];
						insertAlertBatchRecord(factory, batch.getLogicalRef(), ProjectUtil.getBOIntFieldValue(smsAlert, "LogicalReference"));
				}
			}
		}
		return super.afterInsert(factory, params, parentObj, obj);
	}
	
	private void insertAlertBatchRecord(IBusinessLogicFactory factory, int batchID, int alertRef)
	{
		CustomBusinessObject smsAlertBatch = ProjectUtil.createNewCBO("CBOSMSAlertBatch");
		smsAlertBatch._setState(CustomBusinessObject.STATE_NEW);
		ProjectUtil.setMemberValueUn(smsAlertBatch, "Batchid", batchID);
		ProjectUtil.setMemberValueUn(smsAlertBatch, "AlertReference", alertRef);
				
		try {
			ProjectUtil.persistCBO((IServerContext)factory.getContext(), smsAlertBatch);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean afterUpdate(IBusinessLogicFactory factory,
			FactoryParams params, BasicBusinessObject parentObj,
			BasicBusinessObject obj) {
		// TODO Auto-generated method stub
		return super.afterUpdate(factory, params, parentObj, obj);
	}

}
