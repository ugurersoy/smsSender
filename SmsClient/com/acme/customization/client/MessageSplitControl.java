package com.acme.customization.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
			if (!StringUtil.equals(strlist[i], ".adý.")
					&& !StringUtil.equals(strlist[i], ".Soyadý.")
					&& !StringUtil.equals(strlist[i], ".Telfon Numarasý.")
					&& !StringUtil.equals(strlist[i], ".Tarih.")
					&& !StringUtil.equals(strlist[i], ".Saat.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Unvaný.")) {
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

		if (text.equals(".adý.")) {
			status = false;
		} else if (text.equals(".Soyadý.")) {
			status = false;
		} else if (text.equals(".Telfon Numarasý.")) {
			status = false;
		} else if (text.equals(".Tarih.")) {
			status = false;
		} else if (text.equals(".Saat.")) {
			status = false;
		} else if (text.equals(".Cari Hesap Kodu.")) {
			status = false;
		} else if (text.equals(".Cari Hesap Unvaný.")) {
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

				if (StringUtil.equals(strlist[i], ".adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"SurName");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Telfon Numarasý.")) {
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
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Unvaný.")) {
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
					continue;
				}
			}

		}else{
			message.setText(mainMessage.getText());
		}
		
		ProjectUtil.setMemberValue((CustomBusinessObject)event.getData(), "Message", message.getText());
	}
	
	
	
	public static void messageCalculaterGridEvent(JLbsXUIGridEvent event,
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

				if (StringUtil.equals(strlist[i], ".adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"SurName");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						message.setText(sendToVisualMessage);
					}

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Telfon Numarasý.")) {
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
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Unvaný.")) {
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
					continue;
				}
			}

		}
	}
	
	public static void messageCalculaterLookUp(ILbsXUIPane container, CustomBusinessObject smsAlert,
			int mainMessageTag, int messageTag, int gridTag)
	{
		String sendToVisualMessage = "";
		String mainMessage = ProjectUtil.getBOStringFieldValue(smsAlert, "MainMessage");
		if (!mainMessage.isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			MessageSplitControl control = new MessageSplitControl();

			String strlist[] = control.splitControl(mainMessage);

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());

			for (int i = 0; i < strlist.length; i++) {
				if (control.controlParamsText(strlist[i])) {
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}
					continue;
				}

				if (control.controlParamsText(strlist[i])) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (!strlist[i].isEmpty()) {
						sendToVisualMessage += strlist[i];
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}
					continue;
				}

				if (StringUtil.equals(strlist[i], ".adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Name");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"SurName");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Telfon Numarasý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
							"Phonenumber");
					if (strlist[i] != null) {
						sendToVisualMessage += strlist[i];
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Tarih.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnDate();
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Saat.")) {
					if (strlist[i] != null) {
						sendToVisualMessage += returnTime();
						ProjectUtil.setMemberValueUn(smsAlert, "Message", sendToVisualMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")) {
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Unvaný.")) {
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
					continue;
				}
			}
			container.resetValueByTag(4001);
		}
	}
	
}
