package com.acme.customization.handlers;

import java.util.ArrayList;
import java.util.Calendar;

import com.acme.customization.cbo.CEOAlertInfo;
import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.batch.ServerBatchServiceImpl;
import com.lbs.data.factory.BasicBusinessLogicHandler;
import com.lbs.data.factory.FactoryParams;
import com.lbs.data.factory.IBusinessLogicFactory;
import com.lbs.data.objects.BasicBusinessObject;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryParams;
import com.lbs.platform.interfaces.IServerContext;
import com.lbs.util.QueryUtil;

public class CLHAlert extends BasicBusinessLogicHandler {
	
	@Override
	public boolean afterDelete(IBusinessLogicFactory factory, FactoryParams params, BasicBusinessObject parentObj, BasicBusinessObject obj) {
		deleteBatchesAndExceptions(factory, obj);
		return super.afterDelete(factory, params, parentObj, obj);
	}
	
	@Override
	public boolean afterInsert(IBusinessLogicFactory factory, FactoryParams params, BasicBusinessObject parentObj,	BasicBusinessObject obj) {
		executeAlert(factory, obj);
		return super.afterInsert(factory, params, parentObj, obj);
	}
	
	
	@Override
	public boolean afterUpdate(IBusinessLogicFactory factory, FactoryParams params, BasicBusinessObject parentObj, BasicBusinessObject obj) {
		deleteBatchesAndExceptions(factory, obj);
		executeAlert(factory, obj);
		return super.afterUpdate(factory, params, parentObj, obj);
	}
	
	private void deleteBatchesAndExceptions(IBusinessLogicFactory factory, BasicBusinessObject obj) {
				
			//first select batch references..
			QueryBusinessObjects qObjs = new QueryBusinessObjects();
			IQueryFactory  serverFactory = (IQueryFactory)factory.getQueryFactory();
			QueryParams params = new QueryParams();
			params.setCustomization(ProjectGlobals.getM_ProjectGUID());
			params.getMainTableParams().getEnabledColumns().disableAll();
			params.getEnabledTableLinks().disableAll();
			params.getEnabledTerms().disableAll();
			params.getEnabledTerms().enable("T77");
			params.getParameters().put("P_OPERATIONID", ProjectGlobals.OPERTYPE_SMSALERT);
			params.getMainTableParams().getEnabledColumns().enable("BatchID");
			try {
				serverFactory.select("CQOSMSBatchBrowser", params, qObjs, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(qObjs.size() > 0 )
			{
				ArrayList batchRefList = new ArrayList();
				for (int i = 0; i < qObjs.size(); i++) {
					batchRefList.add(QueryUtil.getIntProp(qObjs.get(i), "BatchID"));
				}
				
				//then you should delete batch exceptions..
				params = new QueryParams();
				params.setCustomization(ProjectGlobals.getM_ProjectGUID());
				params.getVariables().put("V_BATCHREFS", batchRefList);
				try {
					serverFactory.executeServiceQuery("CQODeleteSMSBatchExceptions", params);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//then delete batches..
				params = new QueryParams();
				params.setCustomization(ProjectGlobals.getM_ProjectGUID());
				params.getParameters().put("P_OPERATIONID", ProjectGlobals.OPERTYPE_SMSALERT);
				try {
					serverFactory.executeServiceQuery("CQODeleteSMSBatches", params);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//delete alert batch table..
				params = new QueryParams();
				params.setCustomization(ProjectGlobals.getM_ProjectGUID());
				params.getParameters().put("P_ALERTEF", ProjectUtil.getBOIntFieldValue(obj, "LogicalReference"));
				try {
					serverFactory.executeServiceQuery("CQODeleteSMSAlertBatch", params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
	}
	
	private void executeAlert(IBusinessLogicFactory factory, BasicBusinessObject obj)
	{
		CustomBusinessObject alert = (CustomBusinessObject)obj;
		if (ProjectUtil.getIntValueOfCheckBox(alert, "Active") == 0)
			return;
		CEOAlertInfo alertInfo = new CEOAlertInfo();
		ProjectUtil.setAlertInfoPropFromCBO(alertInfo, alert);
		ServerBatchServiceImpl batchServ = new ServerBatchServiceImpl((IServerContext)factory.getContext());
		if (alertInfo.isSchedule())
		{
		
			if (alertInfo.isPeriodic())
			{
				
				while(alertInfo.getBeginDate().compareTo(alertInfo.getEndDate())<=0)
				{
					try {
						batchServ.scheduleBatchOperation("BatchSMSAlert",  new Object[] {alert}, alertInfo.getBeginDate());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					alertInfo.getBeginDate().add(Calendar.MONTH, alertInfo.getPeriod());
				}
			} else
				try {
					batchServ.scheduleBatchOperation("BatchSMSAlert",  new Object[] { alert}, alertInfo.getScheduleDate());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else
			try {
				batchServ.requestImmediateBatchOperation("BatchSMSAlert", new Object[] { alert }, false) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}

}
