package com.acme.customization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


import com.java.net.maradit.api.Maradit;
import com.java.net.maradit.api.SubmitResponse;
import com.lbs.batch.BatchOperationBase;
import com.lbs.batch.BatchSuspensionResult;
import com.lbs.batch.IBatchSuspendable;
import com.lbs.batch.IBatchTerminatable;
import com.lbs.batch.classes.BatchSuspensionDataBase;
import com.lbs.batch.classes.ServerBatchUtil;
import com.lbs.data.database.cache.DBPreparedStatementCache;
import com.lbs.data.objects.CustomBusinessObject;
import com.acme.customization.SMSObject;

public class BatchSMSSending extends BatchOperationBase implements IBatchTerminatable, IBatchSuspendable, Serializable{
	
	private ArrayList m_SMSObjectList = new ArrayList();
	private Vector m_LogList = new Vector();
	private String m_UserName = "";
	private String m_Password = "";
	private int operationID = ProjectGlobals.OPERTYPE_SMSSENDING;
	private int recStartCount = 0;
	private int totalCount = 0;
	private static final long serialVersionUID = 1L;
	private ServerBatchUtil batchUtil = new ServerBatchUtil();
	boolean okay = true;
	
	public int setParams(String userName, String password, ArrayList smsObjectList)
	{
		m_UserName = userName;
		m_Password = password;
		m_SMSObjectList = smsObjectList ;
		return STATUS_COMPLETED;
	}
	
	public int run()
	{
		try
		{
			DBPreparedStatementCache.STATEMENT_CACHE = false;

			//insert into batch tables
			setBatchOperationID(operationID);


			okay =  m_SMSObjectList.size() > 0;

			if (okay)
				sendSMSRoutine(batchUtil);
		}
		catch (Exception e)
		{
			m_ServerContext.getLogger().error("Exception: ", e);
			updateStatus(STATUS_ERROR, "Exception :" + e);
			return STATUS_ERROR;
		}

		finalizeBatch();

		return STATUS_COMPLETED;
	} //END of run()
	
	private void sendSMSRoutine(ServerBatchUtil util) throws Exception
	{
			Maradit maradit = new Maradit(m_UserName, m_Password);
        	maradit.validityPeriod = 120;
        	maradit.from =  ProjectUtil.getCompanyName(m_ServerContext);
			for (int i = recStartCount; i < m_SMSObjectList.size(); i++)
			{
				totalCount++;
				recStartCount++;
				CustomBusinessObject smsObj = (CustomBusinessObject) m_SMSObjectList.get(i);

				String phoneNumber = ProjectUtil.getBOStringFieldValue(smsObj, "Phonenumber");
				String title = ProjectUtil.getBOStringFieldValue(smsObj, "Title");
				String message = ProjectUtil.getBOStringFieldValue(smsObj, "Message");
				ArrayList ToList = new ArrayList<String>();
				ToList.add(phoneNumber);
				SubmitResponse response = maradit.submit(ToList, message);
				if (response.statusCode != 200 || !response.status) {
					SMSObject logRec = new SMSObject();
					logRec.setPhoneNumber(phoneNumber);
					logRec.setTitle(title);
					logRec.setError(response.error);
					logRec.setStatusDesc(response.statusDescription);
					m_LogList.add(logRec);
				}
			}


	} //END of sendSMSRoutine()
	
	public Vector getBatchLog(Vector logList, Object[] paramList)
	{
		return getBatchLog(logList, paramList, 0, 0, 0);
	}

	
	@Override
	public int resume(Object suspensionData) {
		// TODO Auto-generated method stub
		if (suspensionData instanceof BatchSMSSendingSuspData)
		{
			try
			{
				copyDataToBatch(suspensionData);
				batchUtil = new ServerBatchUtil();
				okay = true;
				sendSMSRoutine(batchUtil);
			}
			catch (Exception e)
			{
				m_ServerContext.getLogger().error("Resume to the batch failed: ", e);
			}
		}
		return finalizeBatch();
	}
	
	public int finalizeBatch()
	{

		Vector logList = getBatchLog(m_LogList, new Object[] { "PhoneNumber", "Title", "Error", "StatusDesc" });
		insertBatchExceptions(logList, 0, 1);
		updateBatchRecCounts(totalCount, recStartCount);

		return STATUS_COMPLETED;
	}
	
	private HashMap copyDataToSuspension()
	{
		HashMap values = new HashMap();
		/*Main Vars*/
		values.put("operationID", Integer.valueOf(operationID));
		values.put("m_SMSObjectList", m_SMSObjectList);
		values.put("m_UserName", m_UserName);
		values.put("m_Password", m_Password);
		values.put("m_LogList", m_LogList);
		values.put("totalCount", Integer.valueOf(totalCount));
		values.put("recStartCount", Integer.valueOf(recStartCount));
		values.put("okay", new Boolean(okay));
		return values;
	}

	private void copyDataToBatch(Object suspensionData)
	{
		BatchSMSSendingSuspData batchData = ((BatchSMSSendingSuspData) suspensionData);
		HashMap values = batchData.suspHashMap;
		this.operationID = ((Integer) values.get("operationID")).intValue();
		this.totalCount = ((Integer) values.get("totalCount")).intValue();
		this.recStartCount = ((Integer) values.get("recStartCount")).intValue();
		this.m_UserName = (String)values.get("m_UserName");
		this.m_Password = (String)values.get("m_Password");
		this.m_SMSObjectList = (ArrayList) values.get("m_SMSObjectList");
		this.m_LogList = (Vector) values.get("m_LogList");
		this.okay = ((Boolean) values.get("okay")).booleanValue();
	}


	@Override
	public BatchSuspensionResult suspend() {
		return new BatchSuspensionResult(true, new BatchSMSSendingSuspData(copyDataToSuspension()));
	}

	@Override
	public boolean terminate() {
		return true;
	}

}

class BatchSMSSendingSuspData extends BatchSuspensionDataBase
{
	private static final long serialVersionUID = 1L;
	protected HashMap suspHashMap = new HashMap();

	public BatchSMSSendingSuspData(HashMap values)
	{
		super();
		suspHashMap = values;
	}
}
