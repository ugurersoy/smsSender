package com.acme.customization.forms;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.acme.customization.cbo.CEOAlertInfo;
import com.acme.customization.client.DoubleClickOnGridEvent;
import com.acme.customization.client.MessageSizeCalculater;
import com.acme.customization.client.MessageSplitControl;
import com.acme.customization.client.OnClickButtonEvent;
import com.acme.customization.client.OnGridCellSelectedReceivers;
import com.acme.customization.client.OnInitializeEvent;
import com.acme.customization.client.OnKeyPressedMessages;
import com.acme.customization.client.Parameters;
import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.acme.customization.ws.maradit.Response;
import com.lbs.batch.ClientBatchService;
import com.lbs.controls.JLbsCheckBox;
import com.lbs.controls.JLbsComboBox;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.controls.datedit.JLbsDateEditWithCalendar;
import com.lbs.controls.datedit.JLbsTimeEdit;
import com.lbs.controls.numericedit.JLbsNumericEdit;
import com.lbs.data.grids.MultiSelectionList;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.objects.IBusinessObjectStates;
import com.lbs.data.objects.ObjectValueManager;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.remoteclient.IClientContext;
import com.lbs.unity.UnityBatchHelper;
import com.lbs.unity.dialogs.IUODMessageConstants;
import com.lbs.unity.pj.PJHelper;
import com.lbs.util.JLbsStringListEx;
import com.lbs.util.ObjectUtil;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

import java.awt.event.KeyListener;

public class CXESMSAlert implements KeyListener{

	private UnityBatchHelper batchHelper = new UnityBatchHelper();
	
	private JLbsObjectListGrid usersGrid;
	private JLbsObjectListGrid senderInfoGrid;
	private JLbsComboBox cbxSenderInfo = null;
	
	private Parameters parameter[] = Parameters.values();
	
	private OnInitializeEvent initialize;
	
	private ArrayList senderInfoList = new ArrayList();
	private ArrayList senderInfoDeleteList = new ArrayList();

	private String message = "";
	private String mainMassage ="";

	private CustomBusinessObject m_SMSAlert = null;

	private JLbsXUIControlEvent m_Event = null;
	private JLbsXUIPane m_Container = null;
	private IClientContext m_Context = null;
	
	public void onInitialize(JLbsXUIControlEvent event) {
		
		m_Event = event;
		m_Container = event.getContainer();
		m_Context = event.getClientContext();
		m_SMSAlert = (CustomBusinessObject) event.getData();
		
		setPermanentStates();

		if (ProjectUtil.getBOIntFieldValue(m_SMSAlert, "LogicalReference") == 0)
			resetDates(event);
		
		addNewUserLine();
		usersGrid = ((com.lbs.grids.JLbsObjectListGrid) m_Container.getComponentByTag(100));
		usersGrid.setObjects((CustomBusinessObjects)ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers"));
		initialize = new OnInitializeEvent();
		initialize.getterParameter(parameter, event, 200);

		senderInfoGrid =  ((com.lbs.grids.JLbsObjectListGrid) m_Container.getComponentByTag(10000032));
		cbxSenderInfo = (JLbsComboBox) m_Container.getComponentByTag(10000021);
		updateSenderInfoGrid(event);
		fillSenderShortDefinition(senderInfoList);
		
		JLbsXUIPane container = m_Container;
		JLbsNumericEdit remainingText = (JLbsNumericEdit) container.getComponentByTag(10000054);
		remainingText.setNumber(612);
		JLbsNumericEdit sizeText = (JLbsNumericEdit) container.getComponentByTag(10000053);
		sizeText.setNumber(0);
		JLbsNumericEdit messageText = (JLbsNumericEdit) container.getComponentByTag(10000056);
		messageText.setNumber(0);
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(3001)).getInnerComponent());
		mainMessage.addKeyListener(this);
		
	}
	
	private void setPermanentStates()
	{
		if (m_Context.getVariable("ALERT") != null && m_Context.getVariable("ALERT") instanceof Integer)
		{
			m_Container.setPermanentStateByTag(10000030, JLbsXUITypes.XUISTATE_EXCLUDED); //send sms button
			m_Container.setPermanentStateByTag(10000033, JLbsXUITypes.XUISTATE_ACTIVE); // save alert button
			if (ProjectUtil.getBOIntFieldValue(m_SMSAlert, "Schedule") > 0)
			{
				m_Container.setPermanentStateByTag(400, JLbsXUITypes.XUISTATE_ACTIVE); //start date
				m_Container.setPermanentStateByTag(500, JLbsXUITypes.XUISTATE_ACTIVE); //start time
			}
			if (ProjectUtil.getBOIntFieldValue(m_SMSAlert, "Periodic") > 0)
			{
				m_Container.setPermanentStateByTag(10000060, JLbsXUITypes.XUISTATE_ACTIVE); //end date
				m_Container.setPermanentStateByTag(10000065, JLbsXUITypes.XUISTATE_ACTIVE); //end time
				m_Container.setPermanentStateByTag(10000058, JLbsXUITypes.XUISTATE_ACTIVE); //period
			}
			m_Context.setVariable("ALERT", null);
		}
		else
		{
			m_Container.setPermanentStateByTag(10000066, JLbsXUITypes.XUISTATE_EXCLUDED); // active 
			m_Container.setPermanentStateByTag(5000, JLbsXUITypes.XUISTATE_EXCLUDED); // period group
		}
	}
	
	private void resetDates(JLbsXUIControlEvent event)
	{
		batchHelper.reset(m_Container, 400, 500, -1);
		//batchHelper.reset(m_Container, 10000060, 10000065, -1);
			
		JLbsDateEditWithCalendar startDate = (JLbsDateEditWithCalendar) m_Container.getComponentByTag(400);
		JLbsTimeEdit startTime = (JLbsTimeEdit) m_Container.getComponentByTag(500);
		ProjectUtil.setMemberValue(m_SMSAlert, "StartDate", startDate.getCalendar());
		ProjectUtil.setMemberValue(m_SMSAlert, "StartTime", startTime.getTime());
			
		//JLbsDateEditWithCalendar endDate = (JLbsDateEditWithCalendar) m_Container.getComponentByTag(10000060);
		//JLbsTimeEdit endTime = (JLbsTimeEdit) m_Container.getComponentByTag(10000065);
		//ProjectUtil.setMemberValue(m_SMSAlert, "EndDate", endDate.getCalendar());
		//ProjectUtil.setMemberValue(m_SMSAlert, "EndTime", endTime.getTime());
		
	}

	public void ParameterOnGridCellDblClick(JLbsXUIGridEvent event) {
		DoubleClickOnGridEvent doubleClick = new DoubleClickOnGridEvent();
		doubleClick.addDoubleClickOnText(event, 3001, 200,100,4001);
	
				try {
					MessageSizeCalculater.messageFindSize(m_Container, 10000041, 10000042, 4001, 10000044);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		MessageSizeCalculater.sizeControl(event.getContainer(), 4001, 3001,10000053, 10000054,10000056);
	}

	public void ParameterOnClick(JLbsXUIControlEvent event) {
		OnClickButtonEvent click = new OnClickButtonEvent();
		click.addParameterOnGrid(event, 3001, 200,100,4001);
		try {
			MessageSizeCalculater.messageFindSize(m_Container, 10000041, 10000042, 4001, 10000044);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageSizeCalculater.sizeControl(event.getContainer(), 4001, 3001,10000053, 10000054,10000056);
	}

	public boolean createNewLine(ILbsXUIPane container, Object data,
			IClientContext context) {
		if (usersGrid.getSelectedRow() + 1 == usersGrid.getObjects().size())
			addNewUserLine();
		usersGrid.rowListChanged();
		return true;
	}

	public boolean createMsgWithTemplate(ILbsXUIPane container, Object data,
			IClientContext context) {
		CustomBusinessObject cBO = (CustomBusinessObject) data;
		//String templateMsgText = ProjectUtil.getBOStringFieldValue(cBO,
			//	"TemplateMsgText");
		//JLbsScrollPane message = (JLbsScrollPane) container
			//	.getComponentByTag(3001);
		//((JLbsEditorPane) message.getInnerComponent()).setText(templateMsgText);
		//container.resetValueByTag(3001);
		return true;
	}
	
	private void addNewUserLine() {
		CustomBusinessObject user = ProjectUtil.createNewCBO("CBOSMSAlertUser");
		CustomBusinessObjects users = (CustomBusinessObjects)ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers");
		users.add(user);
	}

	public void onGridCanInsertRow(JLbsXUIGridEvent event) {
		event.setReturnObject(false);
	}

	public void onClickDeleteUsers(JLbsXUIControlEvent event) {
		usersGrid.getObjects().clear();
		addNewUserLine();
		usersGrid.rowListChanged();
	}

	public void addGroupLinesToGrid(JLbsXUIControlEvent event, int groupRef) {

		CustomBusinessObject group = ProjectUtil.readObject(event,
				"CBOMblInfoGroup", groupRef);
		if (group == null)
			return;
		CustomBusinessObjects<CustomBusinessObject> groupLines = (CustomBusinessObjects<CustomBusinessObject>) ProjectUtil
				.getMemberValue(group, "MblInfoUsrGrpLnsLink");
		for (int i = 0; i < groupLines.size(); i++) {
			CustomBusinessObject user = new CustomBusinessObject();
			CustomBusinessObject groupLine = groupLines
					.get(i);
			CustomBusinessObject mblInfoUserLink = (CustomBusinessObject) ProjectUtil
					.getMemberValue(groupLine, "MblInfoUserLink");

			String name = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Name");
			String surName = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "SurName");
			String phoneNumber = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Phonenumber");
			String title = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Title");
			ProjectUtil.setMemberValueUn(user, "Name", name);
			ProjectUtil.setMemberValueUn(user, "SurName", surName);
			ProjectUtil.setMemberValueUn(user, "Phonenumber", phoneNumber);
			ProjectUtil.setMemberValueUn(user, "Tckno", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Tckno"));
			ProjectUtil.setMemberValueUn(user, "Title", title);

			if (!isPhoneNumberInList(phoneNumber, title)) {
				usersGrid.getObjects().set(usersGrid.getObjects().size() - 1, user);
				addNewUserLine();
			}
		}
	}

	public void onClickSelectSubscriber(JLbsXUIControlEvent event) {

		JLbsXUIPane container = m_Container;
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		boolean ok = container.openChild("Forms/CXFMobileSubscribersBrowser.lfrm",
				info, true, JLbsXUITypes.XUIMODE_DBSELECT);
		if ((!ok) || (info.getResult() <= 0))
			return;

		MultiSelectionList list = (MultiSelectionList) info
				.getParameter("MultiSelectionList");
		for (int i = 0; i < list.size(); i++) {
			QueryObjectIdentifier qId = (QueryObjectIdentifier) list.get(i);
			QueryBusinessObject qbo = (QueryBusinessObject) qId
					.getAssociatedData();
			CustomBusinessObject user = ProjectUtil.createNewCBO("CBOMaster");
			String name = QueryUtil.getStringProp(qbo, "MBLINFUSER_NAME");
			String surName = QueryUtil.getStringProp(qbo, "MBLINFUSER_SURNAME");
			String phoneNumber = QueryUtil.getStringProp(qbo,"MBLINFUSER_PHONENUMBER");
			String title = QueryUtil.getStringProp(qbo,"MBLINFUSER_TITLE");
			String tckNo = QueryUtil.getStringProp(qbo, "MBLINFUSER_TCKNO");
			ProjectUtil.setMemberValueUn(user, "Name", name + ' ' + surName);
			ProjectUtil.setMemberValueUn(user, "Phonenumber", phoneNumber);
			ProjectUtil.setMemberValueUn(user, "Tckno", tckNo);
			ProjectUtil.setMemberValueUn(user, "Title", title);
			if (!isPhoneNumberInList(phoneNumber, name + ' ' + surName)) {
				usersGrid.getObjects().set(usersGrid.getObjects().size() - 1,
						user);
				addNewUserLine();
			}
		}
		usersGrid.rowListChanged();
	}

	private boolean isPhoneNumberInList(String phoneNumber, String name)
	{
		for(int i=0; i<usersGrid.getObjects().size();i++)
		{
			CustomBusinessObject user = (CustomBusinessObject) usersGrid.getObjects().get(i);
			String number = ProjectUtil.getBOStringFieldValue(user, "Phonenumber");
			if (number != null && number.compareTo(phoneNumber) == 0)
			{
				String warningMsg = "\""+phoneNumber +"\""+ " numaralý telefon " +"\""+ name+"\""+ " alýcý ünvanýyla eklenmiþ.";
				JOptionPane.showMessageDialog(null, warningMsg);
				return true;
			}
		}
		return false;
	}
	
	private ArrayList prepareSMSObjList(JLbsXUIControlEvent event)
	{
		JLbsXUIPane container = m_Container;
		String messageMain = ProjectUtil.getBOStringFieldValue(m_SMSAlert,"MainMessage");

		ArrayList smsObjectList = new ArrayList();
		JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
				.getComponentByTag(100);

		if (!messageMain.isEmpty()) {
			MessageSplitControl control = new MessageSplitControl();
			String strlist[] = control.splitControl(messageMain);
			String strlistMessage[]=null;
			for (int j = 0; j < messageReveiverGrid.getObjects().size(); j++) {
				CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid.getObjects().get(j);
				if (ProjectUtil.getBOStringFieldValue(obj, "Phonenumber")
						.length() == 0)
					continue;
				if (control.controlParams(strlist)) {
				
					strlistMessage=control.splitControl(messageMain);
					for (int i = 0; i < strlistMessage.length; i++) {
					
						if (control.controlParamsText(strlistMessage[i])) {
							if(strlistMessage[i]!=null)
							message += strlistMessage[i];
							continue;
						}
						if (StringUtil.equals(strlistMessage[i], ".adý.")) {
							strlistMessage[i] = (String) ProjectUtil.getMemberValue(
									obj, "Name");
							if(strlistMessage[i]!=null)
							message += strlistMessage[i];
							continue;
						}
						if (StringUtil.equals(strlistMessage[i], ".Soyadý.")) {

							strlistMessage[i] = (String) ProjectUtil.getMemberValue(
									obj, "SurName");
							if(strlistMessage[i]!=null)
							message += strlistMessage[i];
							continue;
						}
						if (StringUtil.equals(strlistMessage[i], ".Telfon Numarasý.")) {

							strlistMessage[i] = (String) ProjectUtil.getMemberValue(
									obj, "Phonenumber");
							if(strlistMessage[i]!=null)
							message += strlistMessage[i];
							continue;
						}
						if (StringUtil.equals(strlistMessage[i], ".Tarih.")) {

							continue;
						}
						if (StringUtil.equals(strlistMessage[i], ".Saat.")) {
							continue;
						}
						if (StringUtil.equals(strlistMessage[i], ".Cari Hesap Kodu.")) {
							continue;
						}
						if (StringUtil
								.equals(strlistMessage[i], ".Cari Hesap Unvaný.")) {
							continue;
						}
						if (StringUtil.equals(strlistMessage[i],
								".Cari Hesap Bakiyesi.")) {
							continue;
						}

					}
				}
				else 
					message = messageMain;
				
					ProjectUtil.setMemberValueUn(obj, "Message", message);
					smsObjectList.add(obj);
				}
				message = "";

			}
			 else {
						// TO DO UYAR MESAJI EKLENECEK
					}
		return smsObjectList;
	}

	public void sendSmsOnClick(JLbsXUIControlEvent event) {
			
		CustomBusinessObjects users = (CustomBusinessObjects)ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers");
		for (int i = users.size() - 1; i >= 0; i--)
		{
			CustomBusinessObject user = (CustomBusinessObject) users.get(i);
			if (ProjectUtil.getBOStringFieldValue(user, "Phonenumber").length() == 0)
				users.remove(i);
		}
		if(users.size() == 0)
		{
			JOptionPane.showMessageDialog(null, "Alýcý bilgisi girilmelidir!");
			event.setReturnObject(false);
			return;
		}

		if(ProjectUtil.getBOStringFieldValue(m_SMSAlert, "MainMessage").length() == 0)
		{
			JOptionPane.showMessageDialog(null, "Mesaj alaný boþ býrakýlamaz!");
			event.setReturnObject(false);
			return;
		}
		ArrayList smsObjectList = prepareSMSObjList(event);
		if (smsObjectList.size() > 0)
		{
			setAlertInfoPropertiesToCBO();
			if (m_Context != null)
				try
				{
					m_Context.requestBatchOperation("BatchSMSAlert", new Object[] { m_SMSAlert });
					m_Container.showMessage(IUODMessageConstants.TRANSACTION_STARTED, "", null);
				}
				catch (Exception e)
				{
					m_Context.getLogger().error("BatchSMSAlert operation batch exception :", e);
				}
		}

	}
	
	private CustomBusinessObject getSelectedSenderInfo()
	{
		String selected = cbxSenderInfo.getSelectedItemValue() != null ? (String) cbxSenderInfo.getSelectedItemValue() : "";
		for(int i=0; i<senderInfoGrid.getObjects().size();i++)
		{
			CustomBusinessObject cBO = (CustomBusinessObject)senderInfoGrid.getObjects().get(i);
			String shortDef = ProjectUtil.getBOStringFieldValue(cBO, "ShortDef");
			if(shortDef.compareTo(selected)==0)
			{
				return cBO;
			}
		}
		return null;
	}

	public static void printResponse(Response response) {
		System.out.println("Data post status:" + response.status);
		System.out.println("Data post error (if status false):"
				+ response.error);
		System.out.println("Gateway status code:" + response.statusCode);
		System.out.println("Gateway status description:"
				+ response.statusDescription);
		System.out.println("Raw response xml:" + response.xml);

		// Maradit maradit = new Maradit("devtest", "devtest");
		// maradit.validityPeriod = 120;
		//
		// List<String> to = new ArrayList<String>();
		// to.add("905367107577");
		//
		// SubmitResponse response = maradit.submit(to, "test mesaj");
		// printResponse(response);
		// System.out.println("Message Id (if status = true and statusCode = 200):"
		// + response.messageId);

	}

	public void onClickSelectGroup(JLbsXUIControlEvent event) {
		JLbsXUIPane container = m_Container;
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		boolean ok = container.openChild("Forms/CXFMobileSubscriberGroupsBrowser.lfrm", info, true, JLbsXUITypes.XUIMODE_DBSELECT);
		if ((!ok) || (info.getResult() <= 0))
			return;

		MultiSelectionList list = (MultiSelectionList) info.getParameter("MultiSelectionList");
		for (int i = 0; i < list.size(); i++) {
			QueryObjectIdentifier qId = (QueryObjectIdentifier) list.get(i);
			QueryBusinessObject qbo = (QueryBusinessObject) qId.getAssociatedData();
			addGroupLinesToGrid(event, QueryUtil.getIntProp(qbo, "MBLINFOGRP_REF"));
		}
		usersGrid.rowListChanged();
	}
	
	public void onKeyPressedMessage(JLbsXUIControlEvent event)
	{
		 OnKeyPressedMessages.OnKeyPress(event, 3001, 4001,100);
		 try {
			MessageSizeCalculater.messageFindSize(m_Container, 10000053, 10000054, 4001, 10000056);
		} catch (ParseException e) {
		 	e.printStackTrace();
		}
		 MessageSizeCalculater.sizeControl(event.getContainer(), 4001, 3001,10000053, 10000054,10000056);
	}
	
	public void onGridCellSelectedReceiver(JLbsXUIGridEvent event)
	{  
		OnGridCellSelectedReceivers.OnCellSelected(event, 3001, 4001, 100);
		 try {
				MessageSizeCalculater.messageFindSize(m_Container, 10000053, 10000054, 4001, 10000056);
			} catch (ParseException e) {
			 	e.printStackTrace();
			}
		 MessageSizeCalculater.sizeControl(event.getContainer(), 4001, 3001,10000053, 10000054,10000056);
	}
	
	public void lookupSelected(ILbsXUIPane container, Object data, IClientContext context)
	{
		MessageSplitControl.messageCalculaterLookUp(container, m_SMSAlert, 3001, 4001, 100);
		
		 try {
				MessageSizeCalculater.messageFindSize((JLbsXUIPane) container, 10000053, 10000054, 4001, 10000056);
			} catch (ParseException e) {
			 	e.printStackTrace();
			}
		 MessageSizeCalculater.sizeControl((JLbsXUIPane)container, 4001, 3001,10000053, 10000054,10000056);
	}



	public void onClickSaveSenderInfo(JLbsXUIControlEvent event)
	{
		 JLbsObjectListGrid senderInfoGrid = ((com.lbs.grids.JLbsObjectListGrid) m_Container.getComponentByTag(10000032));
		 for(int i=0; i<senderInfoGrid.getObjects().size();i++)
		 {
			 CustomBusinessObject cBO = (CustomBusinessObject)senderInfoGrid.getObjects().get(i);
			 cBO.setObjectName("CBOSenderInfo");
			 cBO.setCustomization(ProjectGlobals.getM_ProjectGUID());
			if (ProjectUtil.getBOIntFieldValue(cBO, "LogicalReference") == 0) {
				cBO._setState(IBusinessObjectStates.STATE_NEW);
			} else
				cBO._setState(IBusinessObjectStates.STATE_MODIFIED);
			 
			 ProjectUtil.setMemberValueUn(cBO, "Linenr", i+1);
			 ProjectUtil.setMemberValueUn(cBO, "UserNr", ProjectUtil.getUserNr(m_Context));
			 ProjectUtil.persistCBO(event, cBO);
		 }
		 
		 for(int i=0; i<senderInfoDeleteList.size();i++)
		 {
			 CustomBusinessObject cBO = (CustomBusinessObject)senderInfoDeleteList.get(i);
			 cBO.setCustomization(ProjectGlobals.getM_ProjectGUID());
			 cBO._setState(IBusinessObjectStates.STATE_DELETED);
			 ProjectUtil.persistCBO(event, cBO);
		 }			

		
	}

	public void onPageChange(JLbsXUIControlEvent event) {
		JTabbedPane tabbedPane = (JTabbedPane) event.getComponent();
		if (tabbedPane.getSelectedIndex() == 0 && senderInfoGrid != null) {
			fillSenderShortDefinition(senderInfoList);
		}
	}
	
	private void updateSenderInfoGrid(JLbsXUIControlEvent event)
	{
		senderInfoList.clear();
		 
		int userNr = ProjectUtil.getUserNr(m_Context);
		String [] paramNames = {"P_USERNR"};
		String [] paramVals = {String.valueOf(userNr)};
		QueryBusinessObjects results = ProjectUtil.runQuery(event, "CQOGetSenderInfo", paramNames, paramVals);
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				QueryBusinessObject result = results.get(i);
				CustomBusinessObject senderInfo = new CustomBusinessObject();
				senderInfo.setObjectName("CBOSenderInfo");
				senderInfo.setCustomization(ProjectGlobals.getM_ProjectGUID());
				ProjectUtil.setMemberValueUn(senderInfo, "LogicalReference", QueryUtil.getIntProp(result, "LogicalRef"));
				ProjectUtil.setMemberValueUn(senderInfo, "Default_", QueryUtil.getIntProp(result, "Default_"));
				ProjectUtil.setMemberValueUn(senderInfo, "UserName", QueryUtil.getStringProp(result, "UserName"));
				ProjectUtil.setMemberValueUn(senderInfo, "Password", QueryUtil.getStringProp(result, "Password"));
				ProjectUtil.setMemberValueUn(senderInfo, "Subscriber",	QueryUtil.getStringProp(result, "Subscriber"));
				ProjectUtil.setMemberValueUn(senderInfo, "ShortDef",	QueryUtil.getStringProp(result, "ShortDef"));
				senderInfoList.add(senderInfo);
			}
		}
		senderInfoGrid.setObjects(senderInfoList);
	}
	
	private void fillSenderShortDefinition(List senderInfoList)
	{
		List clonnedSenderInfoList = new ArrayList();
		clonnedSenderInfoList.addAll(senderInfoList);
		Collections.sort(clonnedSenderInfoList, new CompareToDefault());
		JLbsStringListEx senderInfoStringList = new JLbsStringListEx();
		for (int i = 0; i < clonnedSenderInfoList.size(); i++) {
			CustomBusinessObject cBO = (CustomBusinessObject)clonnedSenderInfoList.get(i);
			int logicalRef = ProjectUtil.getBOIntFieldValue(cBO, "LogicalReference");
			String shortDef = ProjectUtil.getBOStringFieldValue(cBO, "ShortDef");
			senderInfoStringList.add(shortDef, logicalRef);
		}
		if (cbxSenderInfo != null) {
			cbxSenderInfo.setItems(senderInfoStringList);
		}
	}
	
	public void onGridRowDeletedSenderInfo(JLbsXUIGridEvent event)
	{
		senderInfoDeleteList.add(event.getData());
	}
	
	private  class CompareToDefault implements Comparator
	{
		@Override
		public int compare(Object obj0, Object obj1)
		{
			int default_0 = ProjectUtil.getIntValueOfCheckBox(obj0, "Default_"); 	
			int default_1 = ProjectUtil.getIntValueOfCheckBox(obj1, "Default_"); 
			if (default_0 > default_1)
				return -1;
			else if (default_0 < default_1)
				return 1;
			return 0;
			
		}
		
	}
	
	public void onGridCellCheckStateChange(JLbsXUIGridEvent event)
	{
		/** onGridCellCheckStateChange : This method is called when the user changes the selection state of a checkbox grid cell. Event parameter object (JLbsXUIGridEvent) contains form object in 'container' property, grid row data object in 'data' property, grid component in 'grid' property, row number in 'row' property (starts from 0), column number in 'column' property (starts from 0), column's tag value in 'columnTag' property, and the selection state (0 if deselected, 1 if selected) in 'index' and 'tag' properties. No return value is expected. */
		if(event.getIndex() == 1)
		{
			for (int i = 0; i < senderInfoList.size(); i++) {
				CustomBusinessObject cBO = (CustomBusinessObject) senderInfoList.get(i);
				if (i == event.getRow())
					ProjectUtil.setMemberValueUn(cBO, "Default_", new Integer(1));
				else
					ProjectUtil.setMemberValueUn(cBO, "Default_", new Integer(0));
			}
		}
	}

	public void onGridRowInsertedSenderInfo(JLbsXUIGridEvent event)
	{
		/** onGridRowInserted : This method is called right after a new row is added to an edit grid. Event parameter object (JLbsXUIGridEvent) contains form object in 'container' property, grid row data object in 'data' property, grid component in 'grid' property, and row number in 'row' property (starts from 0). A boolean ('true' if the row data object is changed in this method) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'false'. */
		CustomBusinessObject senderInfo = (CustomBusinessObject) event.getData();
		senderInfo.setObjectName("CBOSenderInfo");
		senderInfo.setCustomization(ProjectGlobals.getM_ProjectGUID());
	}

	public void onCheckSchedule(JLbsXUIControlEvent event)
	{
		JLbsCheckBox checkbox = (JLbsCheckBox) event.getComponent();
		if (checkbox.isSelected())
		{
			m_Container.setPermanentStateByTag(400, JLbsXUITypes.XUISTATE_ACTIVE);
			m_Container.setPermanentStateByTag(500, JLbsXUITypes.XUISTATE_ACTIVE);
		}
		else
		{
			if(ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Periodic") == 1)
			{
				JOptionPane.showMessageDialog(null, "Period seçimi kaldýrýlmalýdýr!");
				checkbox.setSelected(true);
			}
			else
			{
				m_Container.setPermanentStateByTag(400, JLbsXUITypes.XUISTATE_RESTRICTED);
				m_Container.setPermanentStateByTag(500, JLbsXUITypes.XUISTATE_RESTRICTED);
			}
		}
	}
	
	public void onClickResetDate(JLbsXUIControlEvent event)
	{
		if (m_Container.getContext() != null)
		{
			try
			{
				resetDates(event);
			}
			catch (Exception e)
			{
				m_Container.getContext().getLogger().error("resetDate() exception", e);
			}
		}
	} //END of resetDate()	

	public void onCheckSelectPeriod(JLbsXUIControlEvent event)
	{
		JLbsCheckBox checkbox = (JLbsCheckBox) event.getComponent();
		if (checkbox.isSelected())
		{
		/*	if(ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Schedule") == 0)
			{
				JOptionPane.showMessageDialog(null, "Period seçilmeden önce baþlangýç tarihi belirlenmelidir!");
				checkbox.setSelected(false);
				
			}
			else
			{*/
				m_Container.setPermanentStateByTag(10000058, JLbsXUITypes.XUISTATE_ACTIVE);
				m_Container.setPermanentStateByTag(10000060, JLbsXUITypes.XUISTATE_ACTIVE);
				m_Container.setPermanentStateByTag(10000065, JLbsXUITypes.XUISTATE_ACTIVE);
			//}
		}
		else
		{
			m_Container.setPermanentStateByTag(10000058, JLbsXUITypes.XUISTATE_RESTRICTED);
			m_Container.setPermanentStateByTag(10000060, JLbsXUITypes.XUISTATE_RESTRICTED);
			m_Container.setPermanentStateByTag(10000065, JLbsXUITypes.XUISTATE_RESTRICTED);
			ProjectUtil.setMemberValueUn(m_SMSAlert, "EndDate", null);
			ProjectUtil.setMemberValueUn(m_SMSAlert, "EndTime", null);
			m_Container.resetValueByTag(10000060);
			m_Container.resetValueByTag(10000065);
		}
	}

	public void onSaveData(JLbsXUIControlEvent event)
	{
		CustomBusinessObjects users = (CustomBusinessObjects)ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers");
		for (int i = users.size() - 1; i >= 0; i--)
		{
			CustomBusinessObject user = (CustomBusinessObject) users.get(i);
			if (ProjectUtil.getBOStringFieldValue(user, "Phonenumber").length() == 0)
				users.remove(i);
		}
		
		if(ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Active") == 1)
		{
			if(users.size() == 0 )
			{
				JOptionPane.showMessageDialog(null, "Alýcý bilgisi girilmelidir!");
				event.setReturnObject(false);
				return;
			}
			
			if(ProjectUtil.getBOStringFieldValue(m_SMSAlert, "MainMessage").length() == 0)
			{
				JOptionPane.showMessageDialog(null, "Mesaj alaný boþ býralýlamaz!");
				event.setReturnObject(false);
				return;
			}
		}
		
		if(ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Periodic") == 1 && ProjectUtil.getBOIntFieldValue(m_SMSAlert, "Period") == 0)
		{
			JOptionPane.showMessageDialog(null, "Period seçilmelidir!");
			event.setReturnObject(false);
			return;
		}
		
		setAlertInfoPropertiesToCBO();
	}
	
	private void setAlertInfoPropertiesToCBO() {
		
		Calendar batchBeginDate = ProjectUtil.concatDates(
				ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "StartDate"),
				ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "StartTime"));
		Calendar batchEndDate = ProjectUtil.concatDates(
				ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "EndDate"),
				ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "EndTime"));
		
		CustomBusinessObject selectedSenderInfo = getSelectedSenderInfo();
		
		boolean isPeriodic =  ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Periodic") == 1;
		if (isPeriodic && batchEndDate.compareTo(batchBeginDate) < 0)
		{
			JOptionPane.showMessageDialog(null, "Bitiþ tarihi baþlangýç tarihinden önce olamaz!");
			m_Event.setReturnObject(false);
			return;
		}
	
		ProjectUtil.setMemberValueUn(m_SMSAlert, "AlertRef", 0);
		ProjectUtil.setMemberValueUn(m_SMSAlert, "BeginDate", batchBeginDate);
		ProjectUtil.setMemberValueUn(m_SMSAlert, "EndDate", batchEndDate);
		ProjectUtil.setMemberValueUn(m_SMSAlert, "UserName", ProjectUtil.getBOStringFieldValue(selectedSenderInfo, "UserName"));
		ProjectUtil.setMemberValueUn(m_SMSAlert, "Password", ProjectUtil.getBOStringFieldValue(selectedSenderInfo, "Password"));
		ProjectUtil.setMemberValueUn(m_SMSAlert, "Schedule", true);
		ProjectUtil.setMemberValueUn(m_SMSAlert, "ScheduleDate", UnityBatchHelper.getScheduleDate(m_Container));
		ProjectUtil.setMemberValueUn(m_SMSAlert, "Period", ProjectUtil.getBOIntFieldValue(m_SMSAlert, "Period"));
		ProjectUtil.setMemberValueUn(m_SMSAlert, "Periodic", isPeriodic);
		ProjectUtil.setMemberValueUn(m_SMSAlert, "SmsObjectList", prepareSMSObjList(m_Event));
		
	}

	public void onGridRowDeleted(JLbsXUIGridEvent event)
	{
		if (usersGrid.getObjects().size() == 0)
			addNewUserLine();
		usersGrid.rowListChanged();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {		
		OnKeyPressedMessages.OnKeyPress(m_Event, 3001, 4001,100);
		 try {
			MessageSizeCalculater.messageFindSize(m_Container, 10000053, 10000054, 4001, 10000056);
		} catch (ParseException e1) {
		 	e1.printStackTrace();
		}
		 MessageSizeCalculater.sizeControl(m_Container, 4001, 3001,10000053, 10000054,10000056);
	}


}
