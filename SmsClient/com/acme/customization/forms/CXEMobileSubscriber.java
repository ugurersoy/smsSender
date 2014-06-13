package com.acme.customization.forms;

import java.math.BigDecimal;
import java.util.ArrayList;







import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.appobjects.GOBOUser;
import com.lbs.controls.JLbsComboBox;
import com.lbs.controls.maskededit.JLbsTextEdit;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryFactoryException;
import com.lbs.data.query.QueryParams;
import com.lbs.hr.em.EMConstants;
import com.lbs.hr.em.bo.EMBOPerson;
import com.lbs.invoke.SessionReestablishedException;
import com.lbs.invoke.SessionTimeoutException;
import com.lbs.remoteclient.IClientContext;
import com.lbs.unity.UnityConstants;
import com.lbs.unity.UnityHelper;
import com.lbs.unity.fi.bo.FIBOARPCard;
import com.lbs.unity.fi.bo.FIBOARPCardLink;
import com.lbs.util.JLbsStringListEx;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXEMobileSubscriber{
	
	
	private ArrayList<GOBOUser> userList = new ArrayList<GOBOUser>();
	
	public CXEMobileSubscriber() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event) {
		
		CustomBusinessObject data = (CustomBusinessObject) event.getData();
		data.createLinkedObjects();
		if (event.getContainer().getMode() == JLbsXUITypes.XUIMODE_DBENTRY) {
			
			ProjectUtil.setMemberValueUn(data, "ArpCardLink",
					new FIBOARPCardLink());
			ProjectUtil.setMemberValueUn(data, "PersonCardLink",
					new EMBOPerson());
		}
		setPermanentStates(event,  ProjectUtil.getBOIntFieldValue(data, "UserType"));
	}
	
	private void setPermanentStates(JLbsXUIControlEvent event, int userType)
	{
		
		if(userType == ProjectGlobals.USER_TYPE_USER)
		{
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_EXCLUDED); // arpcode
			event.getContainer().setPermanentStateByTag(19, JLbsXUITypes.XUISTATE_EXCLUDED); // personCode
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_ACTIVE); // usercombo
			fillUserList(event, userType);
		}
		else if(userType == ProjectGlobals.USER_TYPE_ARP)
		{
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_EXCLUDED); // usercombo
			event.getContainer().setPermanentStateByTag(19, JLbsXUITypes.XUISTATE_EXCLUDED); // personCode
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_ACTIVE); // arpcode
		}
		else if(userType == ProjectGlobals.USER_TYPE_EMPLOYEE)
		{
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_EXCLUDED); // usercombo
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_EXCLUDED); // arpCode
			event.getContainer().setPermanentStateByTag(19, JLbsXUITypes.XUISTATE_ACTIVE); // personCode
		}
		else
		{
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_EXCLUDED); 
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_EXCLUDED);
			event.getContainer().setPermanentStateByTag(19, JLbsXUITypes.XUISTATE_EXCLUDED);
		}
		resetValues(null, event);
	}
	
	private void fillUserList(JLbsXUIControlEvent event, int userType)
	{
		JLbsComboBox cbxUserList = (JLbsComboBox) event.getContainer()
				.getComponentByTag(16);
		JLbsStringListEx list = new JLbsStringListEx();
		list.add("(Belirtilmemiþ)", -1);
		if (userType == ProjectGlobals.USER_TYPE_USER) {
			try {
				QueryBusinessObjects results = ProjectUtil.runQuery(event, "CQOGetUserInfo", new String[0] , new Object[0]);
				if (results!=null && results.size() > 0) {
					for (int i = 0; i < results.size(); i++) {
						QueryBusinessObject result = results
								.get(i);
						int logicalRef =QueryUtil.getIntProp(result,"LOGICALREF");
						String userName = QueryUtil.getStringProp(result,
								"USERNAME");
						String fullName = QueryUtil.getStringProp(result,
								"FULLNAME");
						String tckNo = QueryUtil.getStringProp(result,
								"TCKN");
						String phoneNumber = QueryUtil.getStringProp(result,
								"GSMNR");

						GOBOUser user = new GOBOUser();
						user.setLogicalRef(logicalRef);
						user.setName(userName);
						user.setFullName(fullName);
						user.setTcKimlikNo(tckNo);
						user.setGsmNr(phoneNumber);
						userList.add(user);
						
						list.add(userName, logicalRef);

					}
				}
				
			} 
			catch (Exception e) {
				event.getClientContext()
						.getLogger()
						.error("CQOGetArpInfo query could not be executed properly :",
								e);

			}
			cbxUserList.setItems(list);
			event.getContainer().resetValueByTag(16);
		}
	}

	public void onUserTypeChanged(JLbsXUIControlEvent event) {
		setPermanentStates(event,
				((JLbsComboBox) event.getComponent()).getSelectedItemTag());
		CustomBusinessObject cBO = (CustomBusinessObject) event.getData();
		ProjectUtil.setMemberValueUn(cBO, "ArpCardLink.Code", "");
		ProjectUtil.setMemberValueUn(cBO, "PersonCardLink.Code", "");
		ProjectUtil.setMemberValueUn(cBO, "CardReference", 0);
		GOBOUser user = new GOBOUser();
		setUserFields(event, user);
		event.getContainer().resetValueByTag(18);
	}

	public void onUserChanged(JLbsXUIControlEvent event)
	{
		JLbsComboBox cbxUserList = (JLbsComboBox) event.getContainer()
				.getComponentByTag(16);
		 
		int userRef = cbxUserList.getSelectedItemTag();
		GOBOUser user = getUser(event, userRef);
		setUserFields(event, user);
	}
	
	private void setUserFields(JLbsXUIControlEvent event, GOBOUser user)
	{
		if (user != null)
		{
			CustomBusinessObject data = (CustomBusinessObject) event.getData();
			ProjectUtil.setMemberValueUn(data, "Name", user.getName());
			ProjectUtil.setMemberValueUn(data, "SurName", user.getFullName());
			ProjectUtil.setMemberValueUn(data, "Tckno", user.getTcKimlikNo());
			ProjectUtil.setMemberValueUn(data, "Phonenumber", user.getGsmNr());
		}
		resetValues(null, event);
	}
	
	
	
	private void resetValues(ILbsXUIPane container, JLbsXUIControlEvent event)
	{
		if (container == null) {
			event.getContainer().resetValueByTag(2000);
			event.getContainer().resetValueByTag(2001);
			event.getContainer().resetValueByTag(2002);
			event.getContainer().resetValueByTag(2003);
			event.getContainer().resetValueByTag(2004);
			event.getContainer().resetValueByTag(2005);
		} else {
			container.resetValueByTag(2000);
			container.resetValueByTag(2001);
			container.resetValueByTag(2002);
			container.resetValueByTag(2003);
			container.resetValueByTag(2004);
			container.resetValueByTag(2005);

		}
		
	}

	private GOBOUser getUser(JLbsXUIControlEvent event, int userRef) {
		
		for (int i = 0; i < userList.size(); i++) {
			GOBOUser user = userList.get(i);
			if(user.getLogicalRef() == userRef)
				return user;
		}
		return new GOBOUser();
	}
	
	public boolean setPersonInfo(ILbsXUIPane container, Object data, IClientContext context)
	{
		int personRef =  ProjectUtil.getBOIntFieldValue(data, "CardReference");
		if(personRef > 0)
		{
			if (personRef != 0)
			{
				ArrayList personRefList = new ArrayList();
				personRefList.add(personRef);
				CustomBusinessObjects personList = ProjectUtil.getUserListWithPersonInfo(context, personRefList);
				if (personList.size() > 0)
				{
					CustomBusinessObject person = (CustomBusinessObject) personList.get(0);
					ProjectUtil.setMemberValueUn((CustomBusinessObject)data, "Phonenumber", ProjectUtil.getBOStringFieldValue(person, "PersonPhonenumber"));						
				}
				resetValues(container, null);
			}
			
		}
		return true;
	}
	
	public boolean checkMblUserName(ILbsXUIPane container, Object data, IClientContext context)
	{
		CustomBusinessObject cBO = (CustomBusinessObject) data; 
		String [] strArr = StringUtil.split(ProjectUtil.getBOStringFieldValue(data, "Name"), ' ');
		ProjectUtil.setMemberValueUn(cBO, "Name", strArr != null ? strArr[0] :" ");
		ProjectUtil.setMemberValueUn(cBO, "SurName", strArr != null ? strArr[1] :" ");
		resetValues(container, null);
		return true;
	}
	
	
	public void onSaveData(JLbsXUIControlEvent event)
	{
		/** onSaveData : This method is called before form data is saved to determine whether the form data can be saved or not. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' and 'component' properties, and form data object in 'data' property. A boolean ('true' means form data can be saved) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'true'. */
		CustomBusinessObject data = (CustomBusinessObject) event.getData();
		JLbsTextEdit phoneNumTxtEdit =  (JLbsTextEdit)event.getContainer().getComponentByTag(2002);
		JLbsTextEdit tcknoTxtEdit =  (JLbsTextEdit)event.getContainer().getComponentByTag(2003);
		if(phoneNumTxtEdit.getText().length() == 0 && tcknoTxtEdit.getText().length()==0)
		{
			event.getContainer().messageDialog(500005, 100, "", null); //"Telefon numarasý veya TC Kimlik No girilmelidir"
			event.setReturnObject(false);
		}
		createTitle(event.getData());
	}

	private void createTitle(Object data) {
		CustomBusinessObject user = (CustomBusinessObject) data;
		String userName = ProjectUtil.getBOStringFieldValue(data, "Name");
		String surName = ProjectUtil.getBOStringFieldValue(data, "SurName");
		ProjectUtil.setMemberValueUn(user, "Title", userName + ' ' + surName);
		
	}
	
}
