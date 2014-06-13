package com.acme.customization.client;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.search.SentDateTerm;

import com.acme.customization.shared.ProjectUtil;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.util.StringUtil;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class MessageSplitControl {

	private String[] par;

	public String[] splitControl(String message) {
		par = message.split("([\'<'\'>'])");

		return par;
	}

	public boolean controlParams(String[] strlist) {
		boolean status = true;
		for (int i = 0; i < strlist.length; i++) {
			if (!StringUtil.equals(strlist[i], ".Abone Adý.")
					&& !StringUtil.equals(strlist[i], ".Abone Soyadý.")
					&& !StringUtil.equals(strlist[i], ".Abone Telefonu.")
					&& !StringUtil.equals(strlist[i], ".Tarih.")
					&& !StringUtil.equals(strlist[i], ".Saat.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Ünvaný.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")
					&& !StringUtil.equals(strlist[i], ".Personel Sicil No.")
					&& !StringUtil.equals(strlist[i], ".Personel Adý.")
					&& !StringUtil.equals(strlist[i], ".Personel Soyadý.")) {
				status = false;
			} else {
				status = true;
				break;
			}
		}
		return status;
	}

	public boolean controlParamsText(String text) {
		boolean status = true;

		if (text.equals(".Abone Adý.")) {
			status = false;
		} else if (text.equals(".Abone Soyadý.")) {
			status = false;
		} else if (text.equals(".Abone Telefonu.")) {
			status = false;
		} else if (text.equals(".Tarih.")) {
			status = false;
		} else if (text.equals(".Saat.")) {
			status = false;
		} else if (text.equals(".Cari Hesap Kodu.")) {
			status = false;
		} else if (text.equals(".Cari Hesap Ünvaný.")) {
			status = false; 
		}
		else if (text.equals(".Cari Hesap Bakiyesi.")) {
			status = false; 
		}
		else if (text.equals(".Personel Sicil No.")) {
			status = false; 
		}
		else if (text.equals(".Personel Adý.")) {
			status = false; 
		}
		else if (text.equals(".Personel Soyadý.")) {
			status = false; 
		}
		else 
		{
			status = true;
		}

		return status;
	}
	
   public static String returnDate()
   {
	   String date=null;
	   SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
	  
	    Calendar cal = Calendar.getInstance();
	    date= dateFormat.format(cal.getTime());  
	   
	   return date;
   }
   
   public static String returnTime()
   {
	   String time=null;
	   SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");  
	  
	    Calendar cal = Calendar.getInstance();
	    time= dateFormat.format(cal.getTime());  
	   
	   return time;
   }

   /**
    *Bu metod Gönderilecek mesaja yazýlan alaný parametre olup olmadýðýný kontrol 
    *edip ana mesaj alanýna eklenmesini saðlar. 
    *
    * @author Ugur.Ersoy
    **/
	public static void messageCalculaterControlEvent(JLbsXUIControlEvent event,
			int mainMessageTag, int messageTag, int gridTag)
	{
		String sendToVisualMessage = "";
		JLbsXUIPane container = event.getContainer();
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageTag)).getInnerComponent());

		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageTag)).getInnerComponent());

		if (!mainMessage.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			MessageSplitControl control = new MessageSplitControl();

			String strlist[] = control.splitControl(mainMessage.getText());

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());
			
			if(ProjectUtil.getBOStringFieldValue(obj, "Phonenumber").length() == 0)
			{
				message.setText("");
				ProjectUtil.setMemberValue((CustomBusinessObject) event.getContainer().getData(), "Message", message.getText());
				return ;
			}
			for (int i = 0; i < strlist.length; i++) {
				if (control.controlParamsText(strlist[i])) {
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}

				if (control.controlParamsText(strlist[i])) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}

				if (StringUtil.equals(strlist[i], ".Abone Adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Abone Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"SurName");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Abone Telefonu.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Phonenumber");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Tarih.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnDate();
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Saat.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnTime();
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"ArpCode");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Ünvaný.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"ArpTitle");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
					strlist[i] = ((BigDecimal) ProjectUtil.getMemberValue(obj,
							"ArpBalance")).toString();
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				
				if (StringUtil.equals(strlist[i], ".Personel Sicil No.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"PersonCode");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"PersonName");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Soyadý.")) {
					strlist[i] = ((String) ProjectUtil.getMemberValue(obj,
							"PersonSurName")).toString();
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
			}

		}else{
			message.setText(mainMessage.getText());
		}
		
		ProjectUtil.setMemberValue((CustomBusinessObject)event.getData(), "Message", message.getText());
	}
	
	
	
	public static void messageCalculaterGridEvent(JLbsXUIGridEvent event,
			int mainMessageTag, int messageTag, int gridTag,CustomBusinessObject m_SMSAlert)
	{
		String sendToVisualMessage = "";
		JLbsXUIPane container = event.getContainer();
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageTag)).getInnerComponent());

		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageTag)).getInnerComponent());

		if (!mainMessage.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			MessageSplitControl control = new MessageSplitControl();

			String strlist[] = control.splitControl(mainMessage.getText());

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());
			
			if(ProjectUtil.getBOStringFieldValue(obj, "Phonenumber").length() == 0)
			{
				message.setText("");
				ProjectUtil.setMemberValue(m_SMSAlert, "Message", message.getText());
				return ;
			}
			
			for (int i = 0; i < strlist.length; i++) {
				if (control.controlParamsText(strlist[i])) {
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				
				if (control.controlParamsText(strlist[i])) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}

				if (StringUtil.equals(strlist[i], ".Abone Adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Abone Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"SurName");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Abone Telefonu.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Phonenumber");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Tarih.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnDate();
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Saat.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnTime();
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"ArpCode");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Ünvaný.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"ArpTitle");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
					strlist[i] = ((BigDecimal) ProjectUtil.getMemberValue(obj,
							"ArpBalance")).toString();
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				
				if (StringUtil.equals(strlist[i], ".Personel Sicil No.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"PersonCode");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"PersonName");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Soyadý.")) {
					strlist[i] = ((String) ProjectUtil.getMemberValue(obj,
							"PersonSurName")).toString();
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
			}
		}else{
			message.setText(mainMessage.getText());
		}
		
		ProjectUtil.setMemberValue(m_SMSAlert, "Message", message.getText());
	}
	
	public static void messageCalculaterLookUp(ILbsXUIPane container, CustomBusinessObject smsAlert,
			int mainMessageTag, int messageTag, int gridTag)
	{
		String sendToVisualMessage = "";
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageTag)).getInnerComponent());
		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageTag)).getInnerComponent());
		if (!mainMessage.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			MessageSplitControl control = new MessageSplitControl();

			String strlist[] = control.splitControl(mainMessage.getText());

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());
			

			for (int i = 0; i < strlist.length; i++) {
				if (control.controlParamsText(strlist[i])) {
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}

				if (control.controlParamsText(strlist[i])) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}

				if (StringUtil.equals(strlist[i], ".Abone Adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Abone Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"SurName");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Abone Telefonu.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Phonenumber");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Tarih.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnDate();
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Saat.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnTime();
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")) {
					
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"ArpCode");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Ünvaný.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"ArpTitle");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
					strlist[i] = ((BigDecimal) ProjectUtil.getMemberValue(obj,
							"ArpBalance")).toString();
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Sicil No.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"PersonCode");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"PersonName");
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Personel Soyadý.")) {
					strlist[i] = ((String) ProjectUtil.getMemberValue(obj,
							"PersonSurName")).toString();
					if(strlist[i]!=null)
					{
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
			}
		}else{
			message.setText(mainMessage.getText());
		}
		
		ProjectUtil.setMemberValue((CustomBusinessObject) container.getData(), "Message", message.getText());
		
		container.resetValueByTag(4001);
	}
	
}
