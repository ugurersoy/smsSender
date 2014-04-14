package com.acme.customization;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.acme.enums.Parameters;
import com.acme.events.DoubleClickOnGridEvent;
import com.acme.events.OnClickButtonEvent;
import com.acme.events.OnGridCellSelectedReceivers;
import com.acme.events.OnInitializeEvent;
import com.acme.events.OnKeyPressedMessages;
import com.java.net.maradit.api.Response;
import com.lbs.batch.ClientBatchService;
import com.lbs.controls.JLbsCheckBox;
import com.lbs.controls.JLbsComboBox;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.controls.numericedit.JLbsNumericEdit;
import com.lbs.data.grids.MultiSelectionList;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.remoteclient.IClientContext;
import com.lbs.unity.UnityBatchHelper;
import com.lbs.unity.UnityHelper;
import com.lbs.unity.dialogs.IUODMessageConstants;
import com.lbs.util.JLbsStringListEx;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class BatchSMSSendingHandler {

	UnityBatchHelper batchHelper = new UnityBatchHelper();
	private JLbsObjectListGrid usersGrid;
	private JLbsObjectListGrid senderInfoGrid;

	private Parameters parameter[] = Parameters.values();
	private OnInitializeEvent initialize;
	private String message = "";
	String mainMassage ="";
	
	ArrayList senderInfoList = new ArrayList();
	private ArrayList senderInfoDeleteList = new ArrayList();
	JLbsComboBox cbxSenderInfo = null;

	public void onInitialize(JLbsXUIControlEvent event) {
		batchHelper.reset(event.getContainer(), 400, 500, -1);
		CustomBusinessObject user = ProjectUtil.createNewCBO("CBOMaster");
		usersGrid = ((com.lbs.grids.JLbsObjectListGrid) event.getContainer().getComponentByTag(100));
		usersGrid.getObjects().add(user);
		usersGrid.rowListChanged();
		initialize = new OnInitializeEvent();
		initialize.getterParameter(parameter, event, 200);

		senderInfoGrid =  ((com.lbs.grids.JLbsObjectListGrid) event.getContainer().getComponentByTag(10000032));
		cbxSenderInfo = (JLbsComboBox) event.getContainer().getComponentByTag(10000021);
		updateSenderInfoGrid(event);
		fillSenderShortDefinition(senderInfoList);
		
		JLbsXUIPane container = event.getContainer();
		JLbsNumericEdit remainingText = (JLbsNumericEdit) container.getComponentByTag(10000054);
		remainingText.setNumber(612);
		JLbsNumericEdit sizeText = (JLbsNumericEdit) container.getComponentByTag(10000053);
		sizeText.setNumber(0);
		JLbsNumericEdit messageText = (JLbsNumericEdit) container.getComponentByTag(10000056);
		messageText.setNumber(0);
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(3001)).getInnerComponent());
		mainMessage.setText("");

		
	}

	public void ParameterOnGridCellDblClick(JLbsXUIGridEvent event) {
		DoubleClickOnGridEvent doubleClick = new DoubleClickOnGridEvent();
		doubleClick.addDoubleClickOnText(event, 3001, 200,100,4001);
	
				try {
					MessageSizeCalculater.messageFindSize(event.getContainer(), 10000041, 10000042, 4001, 10000044);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	public void ParameterOnClick(JLbsXUIControlEvent event) {
		OnClickButtonEvent click = new OnClickButtonEvent();
		click.addParameterOnGrid(event, 3001, 200,100,4001);
		try {
			MessageSizeCalculater.messageFindSize(event.getContainer(), 10000041, 10000042, 4001, 10000044);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean concatNameSurName(ILbsXUIPane container, Object data,
			IClientContext context) {
		CustomBusinessObject user = (CustomBusinessObject) data;
		String userName = ProjectUtil.getBOStringFieldValue(data, "Name");
		String surName = ProjectUtil.getBOStringFieldValue(data, "SurName");
		ProjectUtil.setMemberValueUn(user, "Name", userName + ' ' + surName);
		if (usersGrid.getSelectedRow() + 1 == usersGrid.getObjects().size())
			addNewUserLine();
		usersGrid.rowListChanged();
		return true;
	}

	public boolean createMsgWithTemplate(ILbsXUIPane container, Object data,
			IClientContext context) {
		CustomBusinessObject cBO = (CustomBusinessObject) data;
		String templateMsgText = ProjectUtil.getBOStringFieldValue(cBO,
				"TemplateMsgText");
		JLbsScrollPane message = (JLbsScrollPane) container
				.getComponentByTag(3001);
		((JLbsEditorPane) message.getInnerComponent()).setText(templateMsgText);
		container.resetValueByTag(3001);
		return true;
	}
	
	private void addNewUserLine() {
		CustomBusinessObject newLine = ProjectUtil.createNewCBO("CBOMaster");
		usersGrid.getObjects().add(newLine);
	}

	public void onGridCanInsertRow(JLbsXUIGridEvent event) {
		event.setReturnObject(false);
	}

	public void onGridCanDeleteRow(JLbsXUIGridEvent event) {
		int size = usersGrid.getObjects().size();
		if (size == 1) {
			CustomBusinessObject newLine = ProjectUtil
					.createNewCBO("CBOMaster");
			usersGrid.getObjects().set(0, newLine);
			usersGrid.rowListChanged();
			event.setReturnObject(false);
		} else
			event.setReturnObject(usersGrid.getObjects().size() > 1);
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
			CustomBusinessObject groupLine = (CustomBusinessObject) groupLines
					.get(i);
			CustomBusinessObject mblInfoUserLink = (CustomBusinessObject) ProjectUtil
					.getMemberValue(groupLine, "MblInfoUserLink");

			String name = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Name");
			String surName = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "SurName");
			String phoneNumber = ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Phonenumber");
			ProjectUtil.setMemberValueUn(user, "Name", name);
			ProjectUtil.setMemberValueUn(user, "SurName", surName);
			ProjectUtil.setMemberValueUn(user, "Phonenumber", phoneNumber);
			ProjectUtil.setMemberValueUn(user, "Tckno", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Tckno"));

			String title = name + ' ' + surName;
			ProjectUtil.setMemberValueUn(user, "Name", title);

			if (!isPhoneNumberInList(phoneNumber, title)) {
				usersGrid.getObjects().set(usersGrid.getObjects().size() - 1, user);
				addNewUserLine();
			}
		}
	}

	public void onClickSelectSubscriber(JLbsXUIControlEvent event) {

		JLbsXUIPane container = event.getContainer();
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		boolean ok = container.openChild("Forms/MobileSubscribersBrowser.lfrm",
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
			String phoneNumber = QueryUtil.getStringProp(qbo,
					"MBLINFUSER_PHONENUMBER");
			String tckNo = QueryUtil.getStringProp(qbo, "MBLINFUSER_TCKNO");
			ProjectUtil.setMemberValueUn(user, "Name", name + ' ' + surName);
			ProjectUtil.setMemberValueUn(user, "Phonenumber", phoneNumber);
			ProjectUtil.setMemberValueUn(user, "Tckno", tckNo);
			ProjectUtil.setMemberValueUn(user, "Name", name + ' ' + surName);
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

	public void sendSmsOnClick(JLbsXUIControlEvent event) {

		JLbsXUIPane container = event.getContainer();
		String messageMain = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(3001)).getInnerComponent()).getText();

		ArrayList<SMSObject> smsObjectList = new ArrayList<SMSObject>();
		JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
				.getComponentByTag(100);

		if (!messageMain.isEmpty()) {
			MessageSplitControl control = new MessageSplitControl();
			String strlist[] = control.splitControl(messageMain);
			String strlistMessage[]=null;

			if (control.controlParams(strlist)) {
				for (int j = 0; j < messageReveiverGrid.getObjects().size(); j++) {
					CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
							.getObjects().get(j);
					
					if (ProjectUtil.getBOStringFieldValue(obj, "Phonenumber")
							.length() == 0)
						continue;
					
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
					String title = (String) ProjectUtil.getMemberValue(obj, "Name")	+ " "+ (String) ProjectUtil.getMemberValue(obj,	"SurName");
					SMSObject smsObject = new SMSObject(message, (String) ProjectUtil.getMemberValue(obj, "Phonenumber"), title);
					smsObjectList.add(smsObject);
					message = "";
				}

			} else {
				JLbsEditorPane messageTemplate = ((JLbsEditorPane) ((JLbsScrollPane) container
						.getComponentByTag(3001)).getInnerComponent());
				message = messageTemplate.getText();
				for (int i = 0; i < messageReveiverGrid.getObjects().size(); i++) {
					CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
							.getObjects().get(i);
					if (ProjectUtil.getBOStringFieldValue(obj, "Phonenumber")
							.length() == 0)
						continue;
					String title = (String) ProjectUtil.getMemberValue(obj, "Name")	+ " "+ (String) ProjectUtil.getMemberValue(obj,	"SurName");
					SMSObject smsObject = new SMSObject(message, (String) ProjectUtil.getMemberValue(obj, "Phonenumber"), title);
					smsObjectList.add(smsObject);
				}
				message = "";

			}
			
			CustomBusinessObject selectedSenderInfo = getSelectedSenderInfo();
			String userName = ProjectUtil.getBOStringFieldValue(selectedSenderInfo, "UserName");
			String passWord = ProjectUtil.getBOStringFieldValue(selectedSenderInfo, "Password");
			JLbsCheckBox checkbox = (JLbsCheckBox) container.getComponentByTag(450);
			if (event.getClientContext() != null)
				try
				{
					ClientBatchService batchServ = new ClientBatchService(event.getClientContext());
					
					if (checkbox.isSelected())
						batchServ.scheduleBatchOperation("BatchSMSSending",  new Object[] {userName, passWord, smsObjectList}, UnityBatchHelper
								.getScheduleDate(container));
					else
						event.getClientContext().requestBatchOperation("BatchSMSSending", new Object[] {userName, passWord, smsObjectList });

					container.showMessage(IUODMessageConstants.TRANSACTION_COMPLETED, "", null);
				}
				catch (Exception e)
				{
					event.getClientContext().getLogger().error("BatchSMSSending operation batch exception :", e);
				}
		}
		
		
		else {
			// TO DO UYAR MESAJI EKLENECEK
		}


	}
	
	private CustomBusinessObject getSelectedSenderInfo()
	{
		String selected = cbxSenderInfo.getSelectedItemValue() != null ? (String) cbxSenderInfo.getSelectedItemValue() : "";
		for(int i=0; i<senderInfoGrid.getObjects().size();i++)
		{
			CustomBusinessObject cBO = (CustomBusinessObject)senderInfoGrid.getObjects().get(i);
			String senderRef = (String)ProjectUtil.getBOStringFieldValue(cBO, "SenderReference");
			if(senderRef.compareTo(selected)==0)
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
		JLbsXUIPane container = event.getContainer();
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		boolean ok = container.openChild("Forms/MobileSubscriberGroupsBrowser.lfrm", info, true, JLbsXUITypes.XUIMODE_DBSELECT);
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
			MessageSizeCalculater.messageFindSize(event.getContainer(), 10000053, 10000054, 4001, 10000056);
		} catch (ParseException e) {
		 	e.printStackTrace();
		}

	}
	
	public void onGridCellSelectedReceiver(JLbsXUIGridEvent event)
	{  
		OnGridCellSelectedReceivers.OnCellSelected(event, 3001, 4001, 100);
		 try {
				MessageSizeCalculater.messageFindSize(event.getContainer(), 10000053, 10000054, 4001, 10000056);
			} catch (ParseException e) {
			 	e.printStackTrace();
			}
	}
	
	public void lookupSelected(ILbsXUIPane container, Object data, IClientContext context)
	{
		MessageSplitControl.messageCalculaterLookUp(container, 3001, 4001, 100);
		
		 try {
				MessageSizeCalculater.messageFindSize((JLbsXUIPane) container, 10000053, 10000054, 4001, 10000056);
			} catch (ParseException e) {
			 	e.printStackTrace();
			}
		
	}



	public void onClickSaveSenderInfo(JLbsXUIControlEvent event)
	{
		 JLbsObjectListGrid senderInfoGrid = ((com.lbs.grids.JLbsObjectListGrid) event.getContainer().getComponentByTag(10000032));
		 for(int i=0; i<senderInfoGrid.getObjects().size();i++)
		 {
			 CustomBusinessObject cBO = (CustomBusinessObject)senderInfoGrid.getObjects().get(i);
			 cBO.setObjectName("CBOSenderInfo");
			 cBO.setCustomization(ProjectGlobals.getM_ProjectGUID());
			if (ProjectUtil.getBOIntFieldValue(cBO, "LogicalReference") == 0) {
				cBO._setState(CustomBusinessObject.STATE_NEW);
			} else
				cBO._setState(CustomBusinessObject.STATE_MODIFIED);
			 
			 ProjectUtil.setMemberValueUn(cBO, "Linenr", i+1);
			 ProjectUtil.setMemberValueUn(cBO, "UserNr", ProjectUtil.getUserNr(event.getClientContext()));
			 ProjectUtil.persistCBO(event, cBO);
		 }
		 
		 for(int i=0; i<senderInfoDeleteList.size();i++)
		 {
			 CustomBusinessObject cBO = (CustomBusinessObject)senderInfoDeleteList.get(i);
			 cBO.setCustomization(ProjectGlobals.getM_ProjectGUID());
			 cBO._setState(CustomBusinessObject.STATE_DELETED);
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
		 
		int userNr = ProjectUtil.getUserNr(event.getClientContext());
		String [] paramNames = {"P_USERNR"};
		String [] paramVals = {String.valueOf(userNr)};
		QueryBusinessObjects results = ProjectUtil.runQuery(event, "CQOGetSenderInfo", paramNames, paramVals);
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				QueryBusinessObject result = (QueryBusinessObject) results.get(i);
				CustomBusinessObject senderInfo = new CustomBusinessObject();
				senderInfo.setObjectName("CBOSenderInfo");
				senderInfo.setCustomization(ProjectGlobals.getM_ProjectGUID());
				ProjectUtil.setMemberValueUn(senderInfo, "LogicalReference", QueryUtil.getIntProp(result, "LogicalRef"));
				ProjectUtil.setMemberValueUn(senderInfo, "Default_", QueryUtil.getIntProp(result, "Default_"));
				ProjectUtil.setMemberValueUn(senderInfo, "UserName", QueryUtil.getStringProp(result, "UserName"));
				ProjectUtil.setMemberValueUn(senderInfo, "Password", QueryUtil.getStringProp(result, "Password"));
				ProjectUtil.setMemberValueUn(senderInfo, "Subscriber",	QueryUtil.getStringProp(result, "Subscriber"));
				ProjectUtil.setMemberValueUn(senderInfo, "SenderReference",	QueryUtil.getStringProp(result, "SenderRef"));
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
			int logicalRef = (Integer)ProjectUtil.getBOIntFieldValue(cBO, "LogicalReference");
			String senderRef = (String)ProjectUtil.getBOStringFieldValue(cBO, "SenderReference");
			senderInfoStringList.add(senderRef, logicalRef);
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
		public int compare(Object obj0, Object obj1)
		{
			int default_0 = getIntValueOfDefault(obj0); 	
			int default_1 = getIntValueOfDefault(obj1); 	
			if (default_0 > default_1)
				return -1;
			else if (default_0 < default_1)
				return 1;
			return 0;
			
		}
		
		private int getIntValueOfDefault(Object obj)
		{
			if(ProjectUtil.getBOFieldValue(obj, "Default_") instanceof Boolean)
				return ((Boolean)ProjectUtil.getBOFieldValue(obj, "Default_")).booleanValue() == true ? 1 : 0;
			else
				return ProjectUtil.getBOIntFieldValue(obj, "Default_"); 
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
			event.getContainer().setPermanentStateByTag(400, JLbsXUITypes.XUISTATE_ACTIVE);
			event.getContainer().setPermanentStateByTag(500, JLbsXUITypes.XUISTATE_ACTIVE);
		}
		else
		{
			event.getContainer().setPermanentStateByTag(400, JLbsXUITypes.XUISTATE_RESTRICTED);
			event.getContainer().setPermanentStateByTag(500, JLbsXUITypes.XUISTATE_RESTRICTED);
		}
	}
	
	public void onClickResetDate(JLbsXUIControlEvent event)
	{
		if (event.getContainer().getContext() != null)
		{
			try
			{
				batchHelper.reset(event.getContainer(), 400, 500, -1);
			}
			catch (Exception e)
			{
				event.getContainer().getContext().getLogger().error("resetDate() exception", e);
			}
		}
	} //END of resetDate()	
	
	

}
