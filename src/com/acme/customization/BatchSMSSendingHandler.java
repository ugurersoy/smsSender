package com.acme.customization;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.java.net.maradit.api.Maradit;
import com.java.net.maradit.api.Response;
import com.java.net.maradit.api.SubmitResponse;
import com.lbs.appobjects.GOBOUser;
import com.lbs.controls.JLbsComboTextEdit;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.controls.maskededit.JLbsTextEdit;
import com.lbs.data.grids.MultiSelectionList;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.remoteclient.IClientContext;
import com.lbs.unity.UnityHelper;
import com.lbs.unity.fi.bo.FIBOARPCardLink;
import com.lbs.util.QueryUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class BatchSMSSendingHandler {

	private JLbsObjectListGrid usersGrid;
	
	public void SendSmsOnClick(JLbsXUIControlEvent event)
	{
		 Maradit maradit = new Maradit("devtest", "devtest");
	        maradit.validityPeriod = 120;
	        
	        List<String> to = new ArrayList<String>();
	        to.add("905367107577");
	    
	        SubmitResponse response = maradit.submit(to, "test mesajý");
	        printResponse(response);
	        System.out.println("Message Id (if status = true and statusCode = 200):" + response.messageId);
	}
	
	public static void printResponse(Response response) {
        System.out.println("Data post status:" + response.status);
        System.out.println("Data post error (if status false):" + response.error);
        System.out.println("Gateway status code:" + response.statusCode);
        System.out.println("Gateway status description:" + response.statusDescription);
        System.out.println("Raw response xml:" + response.xml);
    }

	public void onInitialize(JLbsXUIControlEvent event)
	{
		CustomBusinessObject user = ProjectUtil.createNewCBO("CBOMaster");
		usersGrid = ((com.lbs.grids.JLbsObjectListGrid) event.getContainer().getComponentByTag(100));
		usersGrid.getObjects().add(user);
		usersGrid.rowListChanged();
	}
	
	public boolean concatNameSurName(ILbsXUIPane container, Object data, IClientContext context)
	{
		CustomBusinessObject user = (CustomBusinessObject) data;
		String userName = ProjectUtil.getBOStringFieldValue(data, "Name");
		String surName = ProjectUtil.getBOStringFieldValue(data, "SurName");
		ProjectUtil.setMemberValueUn(user, "Name", userName + ' ' + surName);
		if (usersGrid.getSelectedRow() + 1 == usersGrid.getObjects().size())
			addNewUserLine();
		return true;
	}
	
	public boolean createMsgWithTemplate(ILbsXUIPane container, Object data, IClientContext context)
	{
		CustomBusinessObject cBO = (CustomBusinessObject) data;
		String templateMsgText = ProjectUtil.getBOStringFieldValue(cBO, "TemplateMsgText");
		JLbsScrollPane message = (JLbsScrollPane)container.getComponentByTag(3001);
		((JLbsEditorPane) message.getInnerComponent()).setText(templateMsgText);
		container.resetValueByTag(3001);
		return true;
	}
	
	public boolean addUserToGrid(CustomBusinessObject cBO, int rowIndex)
	{
		String userName = ProjectUtil.getBOStringFieldValue(cBO, "Name");
		String surName = ProjectUtil.getBOStringFieldValue(cBO, "SurName");
		ProjectUtil.setMemberValueUn(cBO, "Name", userName + ' ' + surName);
		usersGrid.getObjects().set(rowIndex, cBO);
		addNewUserLine();
		return true;
	}
	
	
	private void addNewUserLine()
	{
		CustomBusinessObject newLine = ProjectUtil.createNewCBO("CBOMaster");
		usersGrid.getObjects().add(newLine);
		usersGrid.rowListChanged();
	}

	public void onGridCanInsertRow(JLbsXUIGridEvent event)
	{
		event.setReturnObject(false);
	}

	public void onGridCanDeleteRow(JLbsXUIGridEvent event)
	{
		int size =  usersGrid.getObjects().size();
		if(size == 1)
		{
			CustomBusinessObject newLine = ProjectUtil.createNewCBO("CBOMaster");
			usersGrid.getObjects().set(0, newLine);
			usersGrid.rowListChanged();
			event.setReturnObject(false);
		}
		else
			event.setReturnObject(usersGrid.getObjects().size() > 1);
	}

	public void onClickDeleteUsers(JLbsXUIControlEvent event)
	{
		usersGrid.getObjects().clear();
		addNewUserLine();
		usersGrid.rowListChanged();
	}
	
	public boolean addGroupLinesToGrid(ILbsXUIPane container, Object data, IClientContext context)
	{
		
		CustomBusinessObject group = ProjectUtil.readObject(context, "CBOMblInfoGroup", ProjectUtil.getBOIntFieldValue(data, "GroupRef"));
		CustomBusinessObjects<CustomBusinessObject> groupLines = (CustomBusinessObjects<CustomBusinessObject>) ProjectUtil
				.getMemberValue(group, "MblInfoUsrGrpLnsLink");
		for(int i=0; i<groupLines.size();i++)
		{
			CustomBusinessObject user = new CustomBusinessObject();
			CustomBusinessObject groupLine = (CustomBusinessObject) groupLines.get(i);
			CustomBusinessObject mblInfoUserLink = (CustomBusinessObject) ProjectUtil.getMemberValue(groupLine, "MblInfoUserLink");
			ProjectUtil.setMemberValueUn(user, "Name", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Name"));
			ProjectUtil.setMemberValueUn(user, "SurName", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "SurName"));
			ProjectUtil.setMemberValueUn(user, "Phonenumber", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Phonenumber"));
			ProjectUtil.setMemberValueUn(user, "Tckno", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Tckno"));
				
			addUserToGrid(user, usersGrid.getSelectedRow() + i);
		}
		
		return true;
	}

	public void onGridLookup(JLbsXUIGridEvent event)
	{
		/** onGridLookup : This method is called when a lookup is initiated from a grid cell. Event parameter object (JLbsXUIGridEvent) contains form object in 'container' property, grid row data object in 'data' property, grid component in 'grid' property, row number in 'row' property (starts from 0), column number in 'column' property (starts from 0), column's tag value in 'columnTag' property, and the editor component that belongs to the cell that is subject to the lookup in 'editor' property. A boolean ('true' if the lookup is successful) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'false'. */
		return ;
	}

	public void onClickSelectSubscriber(JLbsXUIControlEvent event)
	{
	
        CustomBusinessObject data = (CustomBusinessObject) event.getData();
        JLbsXUIPane container = event.getContainer();

        JLbsXUILookupInfo info = new JLbsXUILookupInfo();
        boolean ok = container.openChild("Forms/MobileSubscribersBrowser.lfrm", info, true, JLbsXUITypes.XUIMODE_DBSELECT);
        if ((!ok) || (info.getResult() <= 0))
                        return;

        MultiSelectionList list = (MultiSelectionList)info.getParameter("MultiSelectionList");
        for (int i = 0; i < list.size(); i++)
		{
			QueryObjectIdentifier qId = (QueryObjectIdentifier) list.get(i);				
			QueryBusinessObject qbo = (QueryBusinessObject) qId.getAssociatedData();
			CustomBusinessObject user = ProjectUtil.createNewCBO("CBOMaster");
			String name = QueryUtil.getStringProp(qbo, "MBLINFUSER_NAME");//info.getStringData("MBLINFUSER_NAME");
	        String surName = QueryUtil.getStringProp(qbo, "MBLINFUSER_SURNAME");//info.getStringData("MBLINFUSER_SURNAME");
	        String phoneNumber = QueryUtil.getStringProp(qbo, "MBLINFUSER_PHONENUMBER");// info.getStringData("MBLINFUSER_PHONENUMBER");
	        String tckNo =QueryUtil.getStringProp(qbo, "MBLINFUSER_TCKNO"); // info.getStringData("MBLINFUSER_TCKNO");
	        ProjectUtil.setMemberValueUn(user, "Name", name + ' ' + surName);
			ProjectUtil.setMemberValueUn(user, "Phonenumber", phoneNumber);
			ProjectUtil.setMemberValueUn(user, "Tckno", tckNo);
			addUserToGrid(user, usersGrid.getSelectedRow() + i);
		}
        
		usersGrid.rowListChanged();
	}
	

}
