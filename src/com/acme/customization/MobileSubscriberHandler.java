package com.acme.customization;

import java.util.ArrayList;

import com.lbs.appobjects.GOBOUser;
import com.lbs.control.interfaces.ILbsComboBox;
import com.lbs.control.interfaces.ILbsComponent;
import com.lbs.controls.JLbsComboBox;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryParams;
import com.lbs.remoteclient.IClientContext;
import com.lbs.unity.UnityHelper;
import com.lbs.unity.fi.bo.FIBOARPCard;
import com.lbs.unity.fi.bo.FIBOARPCardLink;
import com.lbs.util.JLbsStringList;
import com.lbs.util.JLbsStringListEx;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUIAdapter;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class MobileSubscriberHandler{
	
	public static int USER_TYPE_USER = 0;
	public static int USER_TYPE_ARP = 1;
	public static int USER_TYPE_OTHER = 2;
	
	private ArrayList<GOBOUser> userList = new ArrayList<GOBOUser>();
	
	public MobileSubscriberHandler() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event) {
		
		CustomBusinessObject data = (CustomBusinessObject) event.getData();
		data.createLinkedObjects();
		if (event.getContainer().getMode() == JLbsXUITypes.XUIMODE_DBENTRY) {
			ProjectUtil.setMemberValueUn(data, "ArpCardLink",
					new FIBOARPCardLink());
		}
		setPermanentStates(event,  ProjectUtil.getBOIntFieldValue(data, "UserType"));
	}
	
	private void setPermanentStates(JLbsXUIControlEvent event, int userType)
	{
		
		if(userType == USER_TYPE_USER)
		{
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_EXCLUDED); // usercombo
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_ACTIVE); // arpcode
			fillUserList(event, userType);
		}
		else if(userType == USER_TYPE_ARP)
		{
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_EXCLUDED); // arpcode
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_ACTIVE); // usercombo
		}
		else
		{
			event.getContainer().setPermanentStateByTag(16, JLbsXUITypes.XUISTATE_EXCLUDED); 
			event.getContainer().setPermanentStateByTag(18, JLbsXUITypes.XUISTATE_EXCLUDED);
		}
		resetValues(null, event);
	}
	
	private void fillUserList(JLbsXUIControlEvent event, int userType)
	{
		JLbsComboBox cbxUserList = (JLbsComboBox) event.getContainer()
				.getComponentByTag(16);
		JLbsStringListEx list = new JLbsStringListEx();
		list.add("(Belirtilmemiþ)", -1);
		if (userType == USER_TYPE_USER) {
			try {
				QueryBusinessObjects results = ProjectUtil.runQuery(event, "CQOGetUserInfo", new String[0] , new Object[0]);
				if (results!=null && results.size() > 0) {
					for (int i = 0; i < results.size(); i++) {
						QueryBusinessObject result = (QueryBusinessObject) results
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
		ProjectUtil.setMemberValueUn(cBO, "CardReference", 0);
		GOBOUser user = new GOBOUser();
		setUserFields(event, user);
		event.getContainer().resetValueByTag(18);
	}

	public void onArpCodeLookup(JLbsXUIControlEvent event) {
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		String[] terms = { "T1" };
		info.setOrderName("byActiveCode");
		info.setQueryTerms(terms);
		info.setQueryParamValue("P_STATUS", new Integer(0));
		info.setQueryVariableValue("V_LKPCOL", new String("CODE"));
		event.getContainer().openChild("FIXFARPCardBrowser.jfm", info, true,
				JLbsXUITypes.XUIMODE_DBSELECT);
		if (info.getResult() == JLbsXUILookupInfo.XUILU_OK) {
			CustomBusinessObject data = (CustomBusinessObject) event.getData();
			ProjectUtil.setMemberValueUn(data, "CardReference", info.getIntegerData("Reference"));
			ProjectUtil.setMemberValueUn(data, "ArpCardLink.Code", info.getStringData("Code"));
			setArpInfoFields(null, null, event, info.getStringData("Title"),info.getIntegerData("Reference"));
		}
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
	
	public boolean verifyArpCode(ILbsXUIPane container, Object data, IClientContext context)
	{
		setArpInfoFields(container, data, null,
				ProjectUtil.getBOStringFieldValue(data, "Name"),
				ProjectUtil.getBOIntFieldValue(data, "CardReference"));
		return true;
	}
	
	
	public void setArpInfoFields(ILbsXUIPane container, Object cBO, JLbsXUIControlEvent event, String name, int arpRef) {
		
		CustomBusinessObject data = cBO == null ? (CustomBusinessObject) event.getData() : (CustomBusinessObject)cBO;
		FIBOARPCard arpCard = new FIBOARPCard();
		String[] strArr = null;
		if (arpRef != 0)
		{
			strArr = StringUtil.split(name, ' ');
			IClientContext context = cBO == null ? event.getContainer().getContext():container.getContext();
			arpCard = (FIBOARPCard) UnityHelper.getBOFieldsByRef(
					context, FIBOARPCard.class, arpRef, new String[] {
							"Internal_Reference", "MobilePhone", "IDTCNo" });
		}
		
		ProjectUtil.setMemberValueUn(data, "Name", strArr != null ? strArr[0] :" ");
		ProjectUtil.setMemberValueUn(data, "SurName", strArr != null ? strArr[1] :" ");
		ProjectUtil.setMemberValueUn(data, "Phonenumber", arpCard.getMobilePhone());
		ProjectUtil.setMemberValueUn(data, "Tckno", arpCard.getIDTCNo());
		
		resetValues(container, event);
	}
	
}
