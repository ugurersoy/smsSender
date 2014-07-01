package com.acme.customization.forms;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTabbedPane;

import com.acme.customization.client.DoubleClickOnGridEvent;
import com.acme.customization.client.MessageSizeCalculater;
import com.acme.customization.client.MessageSplitControl;
import com.acme.customization.client.OnClickButtonEvent;
import com.acme.customization.client.OnGridCellSelectedReceivers;
import com.acme.customization.client.OnInitializeEvent;
import com.acme.customization.client.OnKeyPressedMessages;
import com.acme.customization.client.ParameterName;
import com.acme.customization.client.Parameters;
import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.acme.customization.ws.maradit.Maradit;
import com.acme.customization.ws.maradit.Response;
import com.acme.customization.ws.maradit.Settings;
import com.acme.customization.ws.maradit.SettingsResponse;
import com.lbs.controls.JLbsCheckBox;
import com.lbs.controls.JLbsComboBox;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsLabel;
import com.lbs.controls.datedit.JLbsDateEditWithCalendar;
import com.lbs.controls.datedit.JLbsTimeEdit;
import com.lbs.controls.numericedit.JLbsNumericEdit;
import com.lbs.data.grids.MultiSelectionList;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.objects.IBusinessObjectStates;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.data.query.QueryParams;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.remoteclient.IClientContext;
import com.lbs.unity.UnityBatchHelper;
import com.lbs.unity.dialogs.IUODMessageConstants;
import com.lbs.util.JLbsStringListEx;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.util.StringUtilExtra;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class CXESMSAlert implements KeyListener{

	private UnityBatchHelper batchHelper = new UnityBatchHelper();
	
	private JLbsObjectListGrid usersGrid;
	private JLbsObjectListGrid senderInfoGrid;
	private JLbsComboBox cbxSenderInfo = null;
	
	private Parameters parameter[] = Parameters.values();
	private ParameterName parameterName[]=ParameterName.values();
	
	private OnInitializeEvent initialize;
	
	private ArrayList senderInfoList = new ArrayList();
	private ArrayList senderInfoDeleteList = new ArrayList();

	private String message = "";
	private String mainMassage ="";

	private CustomBusinessObject m_SMSAlert = null;

	private JLbsXUIControlEvent m_Event = null;
	private JLbsXUIPane m_Container = null;
	private IClientContext m_Context = null;
	
	private int selectedRow = -1;
	
	public void onInitialize(JLbsXUIControlEvent event) {
		
		m_Event = event;
		m_Container = event.getContainer();
		m_Context = event.getClientContext();
		m_SMSAlert = (CustomBusinessObject) event.getData();
		
		setPermanentStates();

		addNewUserLine();
		usersGrid = ((com.lbs.grids.JLbsObjectListGrid) m_Container.getComponentByTag(100));
		usersGrid.setObjects((CustomBusinessObjects)ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers"));
		initialize = new OnInitializeEvent();
		initialize.getterParameter(parameter, event, 200,parameterName);
		
		if (ProjectUtil.getBOIntFieldValue(m_SMSAlert, "LogicalReference") == 0)
			resetDates(event);
		else
			setAlertUsersInfo();

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
		
		checkSelectedSenderInfo();
		
	}
	
	private void setAlertUsersInfo() {
		
		CustomBusinessObjects users = (CustomBusinessObjects) ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers");
		for (int i = 0; i < users.size(); i++)
		{
			CustomBusinessObject user = (CustomBusinessObject) users.get(i);
			if (ProjectUtil.getBOIntFieldValue(user, "UserRef") > 0
					&& ProjectUtil.getMemberValue(user, "UserInfo") != null) {
				
				CustomBusinessObject userInfo =  (CustomBusinessObject) ProjectUtil.getMemberValue(user, "UserInfo");
				ProjectUtil.setMemberValue(user, "Name", ProjectUtil.getBOStringFieldValue(userInfo, "Name"));
				ProjectUtil.setMemberValue(user, "SurName", ProjectUtil.getBOStringFieldValue(userInfo, "SurName"));
				ProjectUtil.setMemberValue(user, "Title", ProjectUtil.getBOStringFieldValue(userInfo, "Title"));
				ProjectUtil.setMemberValue(user, "Phonenumber", ProjectUtil.getBOStringFieldValue(userInfo, "Phonenumber"));
				ProjectUtil.setMemberValue(user, "Tckno", ProjectUtil.getBOStringFieldValue(userInfo, "Tckno"));
				ProjectUtil.setMemberValue(user, "CardReference", ProjectUtil.getBOIntFieldValue(userInfo, "CardReference"));
				ProjectUtil.setMemberValue(user, "UserType", ProjectUtil.getBOIntFieldValue(userInfo, "UserType"));
				setUserInfo(user);
			}
		}
			
		
	}

	private void checkSelectedSenderInfo() {
		CustomBusinessObject selectedSenderInfo = getSelectedSenderInfo();
		Maradit maradit = new Maradit(ProjectUtil.getBOStringFieldValue(selectedSenderInfo, "UserName"),
				ProjectUtil.getBOStringFieldValue(selectedSenderInfo, "Password"));
    	maradit.validityPeriod = 120;
    	maradit.from =  ProjectUtil.getCompanyName(m_Context);
    	SettingsResponse settingsResponse = maradit.getSettings();
        printResponse(settingsResponse);
		if (settingsResponse.status) {

			if (settingsResponse.statusCode == 200) {
				System.out.println("User info:");
				System.out.println("Balance limit:"
						+ settingsResponse.settings.balance.limit + "\t"
						+ "Main balance:"
						+ settingsResponse.settings.balance.main);

				System.out.println("Senders:");
				for (Settings.Sender sender : settingsResponse.settings.senders) {
					System.out.println("Sender:" + sender.value
							+ "\t Is Default:" + sender.isDefault);
				}

				System.out.println("Keywords:");
				for (Settings.Keyword keyword : settingsResponse.settings.keywords) {
					System.out.println("Keyword:" + keyword.value
							+ "\t Service Number:" + keyword.serviceNumber
							+ "\t Timestamp:" + keyword.timestamp
							+ "\t Validity:" + keyword.validity);
				}
				m_Container.setPermanentStateByTag(10000023, JLbsXUITypes.XUISTATE_EXCLUDED);
				m_Container.setPermanentStateByTag(10000100, JLbsXUITypes.XUISTATE_ACTIVE);
				((JLbsLabel) m_Container.getComponentByTag(10000100)).setText(settingsResponse.settings.balance.main
								.toString());
			}
			else if(settingsResponse.statusCode == 401)
			{
				m_Container.setPermanentStateByTag(10000100, JLbsXUITypes.XUISTATE_EXCLUDED);
				m_Container.setPermanentStateByTag(10000023, JLbsXUITypes.XUISTATE_ACTIVE); 
			}
		}
		
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
		doubleClick.addDoubleClickOnText(event, 3001, 200,100,4001,m_SMSAlert);
	
				try {
					MessageSizeCalculater.messageFindSize(m_Container, 10000053, 10000054, 4001, 10000056);
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
			MessageSizeCalculater.messageFindSize(m_Container, 10000053, 10000054, 4001, 10000056);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageSizeCalculater.sizeControl(event.getContainer(), 4001, 3001,10000053, 10000054,10000056);
	}

	public boolean createNewLine(ILbsXUIPane container, Object data,
			IClientContext context) {
		if (selectedRow + 1 == usersGrid.getObjects().size())
			addNewUserLine();
		usersGrid.rowListChanged();
		return true;
	}
	
	public void setUserInfo(CustomBusinessObject user)
	{
		if (user != null)
		{
			if(ProjectUtil.getBOIntFieldValue(user, "UserType") == ProjectGlobals.USER_TYPE_ARP) 
			{
				int arpRef = ProjectUtil.getBOIntFieldValue(user, "CardReference");
				if (arpRef > 0)
				{
					ArrayList arpRefList = new ArrayList();
					arpRefList.add(arpRef);
					CustomBusinessObjects userList = ProjectUtil.getUserListWithArpInfo(m_Context, arpRefList);
					if (userList.size() > 0)
					{
						CustomBusinessObject listUser = (CustomBusinessObject) userList.get(0);
						ProjectUtil.setMemberValueUn(user, "ArpCode",  ProjectUtil.getBOStringFieldValue(listUser, "ArpCode"));
						ProjectUtil.setMemberValueUn(user, "ArpTitle",  ProjectUtil.getBOStringFieldValue(listUser, "ArpTitle"));
						ProjectUtil.setMemberValueUn(user, "ArpBalance", ProjectUtil.getBOBigDecimalFieldValue(listUser, "ArpBalance"));
					}
				}
	
			}
			else if(ProjectUtil.getBOIntFieldValue(user, "UserType") == ProjectGlobals.USER_TYPE_EMPLOYEE) 
			{
				int personRef = ProjectUtil.getBOIntFieldValue(user, "CardReference");
				if (personRef > 0)
				{
					ArrayList personRefList = new ArrayList();
					personRefList.add(personRef);
					CustomBusinessObjects userList = ProjectUtil.getUserListWithPersonInfo(m_Context, personRefList);
					if (userList.size() > 0)
					{
						CustomBusinessObject listUser = (CustomBusinessObject) userList.get(0);
						ProjectUtil.setMemberValueUn(user, "PersonCode",  ProjectUtil.getBOStringFieldValue(listUser, "PersonCode"));
						ProjectUtil.setMemberValueUn(user, "PersonName",  ProjectUtil.getBOStringFieldValue(listUser, "PersonName"));
						ProjectUtil.setMemberValueUn(user, "PersonSurName", ProjectUtil.getBOStringFieldValue(listUser, "PersonSurName"));
					}
				}
			}
		}
	}
	
	public void setUserInfo(ILbsXUIPane container, Object data, IClientContext context)
	{
		CustomBusinessObjects users = (CustomBusinessObjects) ProjectUtil.getMemberValue(m_SMSAlert, "AlertUsers");
		CustomBusinessObject user =  (CustomBusinessObject) users.get(selectedRow);
		setUserInfo(user);
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

			ProjectUtil.setMemberValue(user, "Name", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Name"));
			ProjectUtil.setMemberValue(user, "SurName", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "SurName"));
			ProjectUtil.setMemberValue(user, "Title", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Title"));
			ProjectUtil.setMemberValue(user, "Phonenumber", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Phonenumber"));
			ProjectUtil.setMemberValue(user, "Tckno", ProjectUtil.getBOStringFieldValue(mblInfoUserLink, "Tckno"));
			ProjectUtil.setMemberValue(user, "CardReference", ProjectUtil.getBOIntFieldValue(mblInfoUserLink, "CardReference"));
			ProjectUtil.setMemberValue(user, "UserType", ProjectUtil.getBOIntFieldValue(mblInfoUserLink, "UserType"));
			setUserInfo(user);
			
			if (!isPhoneNumberInList(
					ProjectUtil.getBOStringFieldValue(user, "Phonenumber"),
					ProjectUtil.getBOStringFieldValue(user, "Title"))) {
				usersGrid.getObjects().set(usersGrid.getObjects().size() - 1,
						user);
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
			ProjectUtil.setMemberValueUn(user, "Name", QueryUtil.getStringProp(qbo, "MBLINFUSER_NAME"));
			ProjectUtil.setMemberValueUn(user, "SurName", QueryUtil.getStringProp(qbo, "MBLINFUSER_SURNAME"));
			ProjectUtil.setMemberValueUn(user, "Title", QueryUtil.getStringProp(qbo, "MBLINFUSER_TITLE"));
			ProjectUtil.setMemberValueUn(user, "Tckno",  QueryUtil.getStringProp(qbo, "MBLINFUSER_TCKNO"));
			ProjectUtil.setMemberValueUn(user, "Phonenumber", QueryUtil.getStringProp(qbo, "MBLINFUSER_PHONENUMBER"));
			ProjectUtil.setMemberValueUn(user, "CardReference", QueryUtil.getIntProp(qbo, "MBLINFUSER_CARDREF"));
			ProjectUtil.setMemberValueUn(user, "LogicalReference", QueryUtil.getIntProp(qbo, "MBLINFUSER_REF"));
			ProjectUtil.setMemberValueUn(user, "UserType", QueryUtil.getIntProp(qbo, "MBLINFUSER_USERTYPE"));
			
			setUserInfo(user);
			if (!isPhoneNumberInList(
					ProjectUtil.getBOStringFieldValue(user, "Phonenumber"),
					ProjectUtil.getBOStringFieldValue(user, "Title"))) {
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
				String warningMsg = "\""+phoneNumber +"\""+ " "+m_Container.getMessage(500052,0)+" " +"\""+ name+"\""+ " "+m_Container.getMessage(500052,1)+".";
				m_Context.showMessage(null,warningMsg);
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
		String m_Message=null;
		if (!messageMain.isEmpty()) {
			
			if(messageReveiverGrid.getObjects().size()>0){
			for (int j = 0; j < messageReveiverGrid.getObjects().size(); j++) {
				CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid.getObjects().get(j);
				if (ProjectUtil.getBOStringFieldValue(obj, "Phonenumber")
						.length() == 0)
					continue;
				  m_Message=messageMain;
				  
				  if(m_Message.contains("P10"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"PersonName")!=null)
						m_Message=m_Message.replace("P10",(String) ProjectUtil.getMemberValue(obj,
								"PersonName"));
						else 
							m_Message= m_Message.replace("P10","");
					}
					
					if(m_Message.contains("P11"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"PersonSurName")!=null)
						m_Message=m_Message.replace("P11",(String) ProjectUtil.getMemberValue(obj,
								"PersonSurName"));
						else 
							m_Message= m_Message.replace("P11","");
					}
				
				 if(m_Message.contains("P1"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"Name")!=null)
		              m_Message= m_Message.replace("P1",(String) ProjectUtil.getMemberValue(obj,
									"Name"));	
						else 
							m_Message= m_Message.replace("P1","");
					}
					
					if(m_Message.contains("P2"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"SurName")!=null)
						m_Message=m_Message.replace("P2",(String) ProjectUtil.getMemberValue(obj,
									"SurName"));
						else 
							m_Message= m_Message.replace("P2","");
					}
					
					if(m_Message.contains("P3"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"Phonenumber")!=null)
						m_Message=m_Message.replace("P3",(String) ProjectUtil.getMemberValue(obj,
									"Phonenumber"));
						else 
							m_Message= m_Message.replace("P3","");
					}
					
					if(m_Message.contains("P4"))
					{
						m_Message=m_Message.replace("P4",MessageSplitControl.returnDate());
					}
					
					if(m_Message.contains("P5"))
					{
						m_Message=m_Message.replace("P5",MessageSplitControl.returnTime());
					}
					
					if(m_Message.contains("P6"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"ArpCode")!=null)
						m_Message=m_Message.replace("P6",(String) ProjectUtil.getMemberValue(obj,
								"ArpCode"));
						else 
							m_Message= m_Message.replace("P6","");
					}
					
					if(m_Message.contains("P7"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"ArpTitle")!=null)
						m_Message=m_Message.replace("P7",(String) ProjectUtil.getMemberValue(obj,
								"ArpTitle"));
						else 
							m_Message= m_Message.replace("P7","");
					}
			
					if(m_Message.contains("P8"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"ArpBalance")!=null)
						m_Message=m_Message.replace("P8",((BigDecimal) ProjectUtil.getMemberValue(obj,
								"ArpBalance")).toString());
						else 
							m_Message= m_Message.replace("P8","");
					}
					
					if(m_Message.contains("P9"))
					{
						if(ProjectUtil.getMemberValue(obj,
								"PersonCode")!=null)
						m_Message=m_Message.replace("P9",((String) ProjectUtil.getMemberValue(obj,
								"PersonCode")).toString());
						else 
							m_Message= m_Message.replace("P9","");
					}
					
					ProjectUtil.setMemberValueUn(obj, "Message", m_Message);
					smsObjectList.add(obj);
		     	}
			
				}

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
			m_Context.showMessage(null,m_Container.getMessage(500052,4));
			event.setReturnObject(false);
			return;
		}

		if(ProjectUtil.getBOStringFieldValue(m_SMSAlert, "MainMessage").length() == 0)
		{
			//JOptionPane.showMessageDialog(null, "Mesaj alaný boþ býrakýlamaz!");
			m_Context.showMessage("deneme", null);
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
			m_Container.saveDataAndClose();
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
		selectedRow = event.getRow();
		OnGridCellSelectedReceivers.OnCellSelected(event, 3001, 4001, 100,m_SMSAlert);
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
			 if (ProjectUtil.getBOStringFieldValue(cBO, "ShortDef").length() == 0)
				continue;
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
			checkSelectedSenderInfo();
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
		senderInfoGrid.getObjects().addAll(senderInfoList);
		senderInfoGrid.rowListChanged();
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
				m_Context.showMessage(null, m_Container.getMessage(500052,5));
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
				m_Context.showMessage(null,m_Container.getMessage(500052,4));
				event.setReturnObject(false);
				return;
			}
			
			if(ProjectUtil.getBOStringFieldValue(m_SMSAlert, "MainMessage").length() == 0)
			{
				m_Context.showMessage(null, m_Container.getMessage(500052,6));
				event.setReturnObject(false);
				return;
			}
		}
		
		if(ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Periodic") == 1 && ProjectUtil.getBOIntFieldValue(m_SMSAlert, "Period") == 0)
		{
			m_Context.showMessage(null, m_Container.getMessage(500052,7));
			event.setReturnObject(false);
			return;
		}
		
		setAlertInfoPropertiesToCBO();
	}
	
	private void setAlertInfoPropertiesToCBO() {
		
		Calendar batchBeginDate = null;
		Calendar batchEndDate = null;
		boolean isPeriodic =  ProjectUtil.getIntValueOfCheckBox(m_SMSAlert, "Periodic") == 1;
		if(m_Context.getVariable("ALERT") != null && m_Context.getVariable("ALERT") instanceof Integer)
		{
			batchBeginDate = ProjectUtil.concatDates(
					ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "StartDate"),
					ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "StartTime"));
			batchEndDate = ProjectUtil.concatDates(
					ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "EndDate"),
					ProjectUtil.getBOCalendarFieldValue(m_SMSAlert, "EndTime"));
			
			if (isPeriodic && batchEndDate.compareTo(batchBeginDate) < 0)
			{
				m_Context.showMessage(null, m_Container.getMessage(500052,7));
				m_Event.setReturnObject(false);
				return;
			}
		}
		
		CustomBusinessObject selectedSenderInfo = getSelectedSenderInfo();
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

	public void onGridCellDataChanged(JLbsXUIGridEvent event)
	{
		CustomBusinessObject user = (CustomBusinessObject) event.getData();
		QueryParams params = new QueryParams();
		switch (event.getColumnTag()) {
			case 10000013:
				params.getEnabledTerms().enable("T_PHONENUM");
				params.getParameters().put("P_PHONENUM", ProjectUtil.getBOStringFieldValue(user, "Phonenumber"));
				break;
			case 10000014:
				params.getEnabledTerms().enable("T_TCKNO");
				params.getParameters().put("P_TCKNO", ProjectUtil.getBOStringFieldValue(user, "Tckno"));
				break;
			case 10000015:
				params.getEnabledTerms().enable("T_TITLE");
				params.getParameters().put("P_TITLE", ProjectUtil.getBOStringFieldValue(user, "Title"));
				break;
			default:
				break;
			}
		
		try {
			
			params.setCustomization(ProjectGlobals.getM_ProjectGUID());
			QueryBusinessObjects results = new QueryBusinessObjects();
			IQueryFactory factory = (IQueryFactory) event.getClientContext().getQueryFactory();
			factory.select("CQOMobileSubscribersBrowser", params, results, -1);
			if (results != null && results.size() > 0) {
				QueryBusinessObject result = results.get(0);
				ProjectUtil.setMemberValueUn(user, "Name", QueryUtil.getStringProp(result, "MBLINFUSER_NAME"));
				ProjectUtil.setMemberValueUn(user, "SurName", QueryUtil.getStringProp(result, "MBLINFUSER_SURNAME"));
				ProjectUtil.setMemberValueUn(user, "Title", QueryUtil.getStringProp(result, "MBLINFUSER_TITLE"));
				ProjectUtil.setMemberValueUn(user, "Tckno",  QueryUtil.getStringProp(result, "MBLINFUSER_TCKNO"));
				ProjectUtil.setMemberValueUn(user, "Phonenumber", QueryUtil.getStringProp(result, "MBLINFUSER_PHONENUMBER"));
				ProjectUtil.setMemberValueUn(user, "CardReference", QueryUtil.getIntProp(result, "MBLINFUSER_CARDREF"));
				ProjectUtil.setMemberValueUn(user, "LogicalReference", QueryUtil.getIntProp(result, "MBLINFUSER_REF"));
				ProjectUtil.setMemberValueUn(user, "UserType", QueryUtil.getIntProp(result, "MBLINFUSER_USERTYPE"));
				setUserInfo(event.getContainer(), event.getContainer().getData(), event.getClientContext());
				createNewLine(event.getContainer(), event.getContainer().getData(), event.getClientContext());
				lookupSelected(event.getContainer(), event.getContainer().getData(), event.getClientContext());
			}
			else
			{
				String title = ProjectUtil.getBOStringFieldValue(user, "Title");
				ProjectUtil.setMemberValueUn(user, "UserRef", 0);
				if (event.getColumnTag() == 10000015) {
					String [] nameSurName = title.split(" ", 2);
					ProjectUtil.setMemberValueUn(user, "Name",
							nameSurName.length >= 1 ? nameSurName[0] : "");
					ProjectUtil.setMemberValueUn(user, "SurName",
							nameSurName.length == 2 ? nameSurName[1] : "");
				}
				
			}
			
		} 
		catch (Exception e) {
			event.getClientContext().getLogger()
					.error("CQOMobileSubscribersBrowser query could not be executed properly :",
							e);

		}
	}

	public void onItemChanged(JLbsXUIControlEvent event)
	{
		/** onItemChanged : This method is called when an item is selected in a combobox. Event parameter object (JLbsXUIControlEvent) contains form object in 'container', the combobox component in 'component' property, form data object in 'data' property, selected combo item's id in 'tag' property, and whether the item is selected or deselected in 'StringData' property. This method is called twice for each selection: once for the deselected item with 'stringData' equals to 'false, 'and once for the selected item with 'stringData' equals to 'true'. No return value is expected. */
		checkSelectedSenderInfo();
	}


}
