package com.acme.customization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.acme.enums.Parameters;
import com.acme.events.DoubleClickOnGridEvent;
import com.acme.events.OnClickButtonEvent;
import com.acme.events.OnInitializeEvent;
import com.java.net.maradit.api.Response;
import com.lbs.controls.JLbsComboBox;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.data.grids.MultiSelectionList;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.remoteclient.IClientContext;
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
		CustomBusinessObject user = ProjectUtil.createNewCBO("CBOMaster");
		usersGrid = ((com.lbs.grids.JLbsObjectListGrid) event.getContainer().getComponentByTag(100));
		usersGrid.getObjects().add(user);
		usersGrid.rowListChanged();
		initialize = new OnInitializeEvent();
		initialize.getterParameter(parameter, event, 200);

		senderInfoGrid =  ((com.lbs.grids.JLbsObjectListGrid) event.getContainer().getComponentByTag(10000032));
		cbxSenderInfo = (JLbsComboBox) event.getContainer().getComponentByTag(10000021);
		updateSenderInfoGrid(event);
		fillSenderShortDefinition(event, senderInfoList);
		
	}

	public void ParameterOnGridCellDblClick(JLbsXUIGridEvent event) {
		DoubleClickOnGridEvent doubleClick = new DoubleClickOnGridEvent();
		doubleClick.addDoubleClickOnText(event, 3001, 200,100,4001);
	}

	public void ParameterOnClick(JLbsXUIControlEvent event) {
		OnClickButtonEvent click = new OnClickButtonEvent();
		click.addParameterOnGrid(event, 3001, 200,100,4001);
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

		List<String> phoneNumberList = new ArrayList<String>();
		List<String> messageList = new ArrayList<String>();
		JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
				.getComponentByTag(100);

		if (!messageMain.isEmpty()) {
			MessageSplitControl control = new MessageSplitControl();
			String strlist[] = control.splitControl(messageMain);
			String strlistMessage[]=null;

			if (control.controlParams(strlist)) {
				for (int j = 0; j < messageReveiverGrid.getColumnCount(); j++) {
					CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
							.getRowObject(j);
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
				
					messageList.add(message);
					message = "";
					
					phoneNumberList.add((String) ProjectUtil.getMemberValue(
							obj, "Phonenumber"));
				}

			} else {
				for (int i = 0; i < messageReveiverGrid.getColumnCount(); i++) {
					CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
							.getRowObject(i);

					String name = (String) ProjectUtil.getMemberValue(obj,
							"Phonenumber");

					phoneNumberList.add((String) ProjectUtil.getMemberValue(
							obj, "Phonenumber"));
				}

				JLbsEditorPane messageTemplate = ((JLbsEditorPane) ((JLbsScrollPane) container
						.getComponentByTag(3001)).getInnerComponent());

				message = messageTemplate.getText();

			}
		} else {
			// TO DO UYAR MESAJI EKLENECEK
		}

		//TO DO mesajýn gönderileceði yer

		//Numarayý listeleyen döngü
		for (String list : phoneNumberList) {
			System.out.println(list);
		}
		
		//mesajlarý listeliyen döngü
		   if (messageList.size() != 0)
			for (String list : messageList) {
				System.out.println("---"+list);
			}

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
	
	public void onKeyTypedMessage(JLbsXUIControlEvent event)
	{
		
		
	}

	public void onKeyPressedMessage(JLbsXUIControlEvent event)
	{
		JLbsXUIPane container = event.getContainer();
		 mainMassage=  ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(3001)).getInnerComponent()).getText();
		
		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(4001)).getInnerComponent());
		
		
		message.setText(mainMassage);
		container.resetValueByTag(4001);
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
		if (tabbedPane.getSelectedIndex() == 0) {
			Collections.sort(senderInfoList, new CompareToDefault());
			fillSenderShortDefinition(event, senderInfoList);
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
				ProjectUtil.setMemberValueUn(senderInfo, "Password", QueryUtil.getStringProp(result, "PassWord"));
				ProjectUtil.setMemberValueUn(senderInfo, "Subscriber",	QueryUtil.getStringProp(result, "Subscriber"));
				ProjectUtil.setMemberValueUn(senderInfo, "SenderReference",	QueryUtil.getStringProp(result, "SenderRef"));
				senderInfoList.add(senderInfo);
			}
		}
		senderInfoGrid.setObjects(senderInfoList);
	}
	
	private void fillSenderShortDefinition(JLbsXUIControlEvent event, ArrayList senderInfoList)
	{
		JLbsStringListEx senderInfoStringList = new JLbsStringListEx();
		for (int i = 0; i < senderInfoList.size(); i++) {
			CustomBusinessObject cBO = (CustomBusinessObject)senderInfoList.get(i);
			int logicalRef = (Integer)ProjectUtil.getMemberValue(cBO, "LogicalReference");
			String senderRef = (String)ProjectUtil.getMemberValue(cBO, "SenderReference");
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
			int default_0 = ((Boolean)ProjectUtil.getBOFieldValue(obj0, "Default_")).booleanValue() == true ? 1 : 0;   	
			int default_1 = ((Boolean) ProjectUtil.getBOFieldValue(obj1, "Default_")).booleanValue() == true ? 1 : 0; 	
			if (default_0 > default_1)
				return -1;
			else if (default_0 < default_1)
				return 1;
			return 0;
		}

	}
	

}
