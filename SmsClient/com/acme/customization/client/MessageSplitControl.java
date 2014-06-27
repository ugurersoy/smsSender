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

   
   
   public static void messageCalculaterControlEvent(JLbsXUIControlEvent event,
			int mainMessageTag, int messageTag, int gridTag)
	{

	    String m_Message=null;
		JLbsXUIPane container = event.getContainer();
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageTag)).getInnerComponent());
		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageTag)).getInnerComponent());

		if (!mainMessage.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());
			
			 m_Message=mainMessage.getText();
			 
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
				m_Message=m_Message.replace("P4",returnDate());
			}
			
			if(m_Message.contains("P5"))
			{
				m_Message=m_Message.replace("P5",returnTime());
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
			
		}
		message.setText(m_Message);
		ProjectUtil.setMemberValue((CustomBusinessObject)event.getData(), "Message", m_Message);
	}
	
   
   
   /**
    *Bu metod Gönderilecek mesaja yazýlan alaný parametre olup olmadýðýný kontrol 
    *edip ana mesaj alanýna eklenmesini saðlar. 
    *
    * @author Ugur.Ersoy
    **/
//	public static void messageCalculaterControlEvent(JLbsXUIControlEvent event,
//			int mainMessageTag, int messageTag, int gridTag)
//	{
//		String sendToVisualMessage = "";
//		JLbsXUIPane container = event.getContainer();
//		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
//				.getComponentByTag(mainMessageTag)).getInnerComponent());
//
//		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
//				.getComponentByTag(messageTag)).getInnerComponent());
//
//		if (!mainMessage.getText().isEmpty()) {
//			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
//					.getComponentByTag(gridTag);
//
//			MessageSplitControl control = new MessageSplitControl();
//
//			String strlist[] = control.splitControl(mainMessage.getText());
//
//			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
//					.getRowObject(messageReveiverGrid.getSelectedRow());
//			
//			if(ProjectUtil.getBOStringFieldValue(obj, "Phonenumber").length() == 0)
//			{
//				message.setText("");
//				ProjectUtil.setMemberValue((CustomBusinessObject) event.getContainer().getData(), "Message", message.getText());
//				return ;
//			}
//			for (int i = 0; i < strlist.length; i++) {
//				if (control.controlParamsText(strlist[i])) {
//					if (!strlist[i].isEmpty()) {
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//
//				if (control.controlParamsText(strlist[i])) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"Name");
//					if (!strlist[i].isEmpty()) {
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//
//				if (StringUtil.equals(strlist[i], ".Abone Adý.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"Name");
//					if (strlist[i] != null) {
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Abone Soyadý.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"SurName");
//					if (strlist[i] != null) {
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Abone Telefonu.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"Phonenumber");
//					if (strlist[i] != null) {
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Tarih.")) {
//					if (strlist[i] != null) {
//						sendToVisualMessage += returnDate();
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Saat.")) {
//					if (strlist[i] != null) {
//						sendToVisualMessage += returnTime();
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"ArpCode");
//					if(strlist[i]!=null)
//					{
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Cari Hesap Ünvaný.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"ArpTitle");
//					if(strlist[i]!=null)
//					{
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Cari Hesap Bakiyesi.")) {
//					strlist[i] = ((BigDecimal) ProjectUtil.getMemberValue(obj,
//							"ArpBalance")).toString();
//					if(strlist[i]!=null)
//					{
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				
//				if (StringUtil.equals(strlist[i], ".Personel Sicil No.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"PersonCode");
//					if(strlist[i]!=null)
//					{
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Personel Adý.")) {
//					strlist[i] = (String) ProjectUtil.getMemberValue(obj,
//							"PersonName");
//					if(strlist[i]!=null)
//					{
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//				if (StringUtil.equals(strlist[i], ".Personel Soyadý.")) {
//					strlist[i] = ((String) ProjectUtil.getMemberValue(obj,
//							"PersonSurName")).toString();
//					if(strlist[i]!=null)
//					{
//						sendToVisualMessage += strlist[i];
//						message.setText(sendToVisualMessage);
//					}
//					continue;
//				}
//			}
//
//		}else{
//			message.setText(mainMessage.getText());
//		}
//		
//		ProjectUtil.setMemberValue((CustomBusinessObject)event.getData(), "Message", message.getText());
//	}
	
	
	
	public static void messageCalculaterGridEvent(JLbsXUIGridEvent event,
			int mainMessageTag, int messageTag, int gridTag,CustomBusinessObject m_SMSAlert)
	{
		JLbsXUIPane container = event.getContainer();
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageTag)).getInnerComponent());
        String m_Message=null;
		
        JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageTag)).getInnerComponent());

		if (!mainMessage.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());
			
			 m_Message=mainMessage.getText();
			 
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
				m_Message=m_Message.replace("P4",returnDate());
			}
			
			if(m_Message.contains("P5"))
			{
				m_Message=m_Message.replace("P5",returnTime());
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
			
		}
		message.setText(m_Message);
		ProjectUtil.setMemberValue(m_SMSAlert, "Message", message.getText());
	}
	
	public static void messageCalculaterLookUp(ILbsXUIPane container, CustomBusinessObject smsAlert,
			int mainMessageTag, int messageTag, int gridTag)
	{
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageTag)).getInnerComponent());
		JLbsEditorPane message = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageTag)).getInnerComponent());
	    String m_Message=null;
		
		
		if (!mainMessage.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);

			CustomBusinessObject obj = (CustomBusinessObject) messageReveiverGrid
					.getRowObject(messageReveiverGrid.getSelectedRow());
			
			 m_Message=mainMessage.getText();
			 
			if(m_Message.contains("P10"))
			{
				if(ProjectUtil.getMemberValue(obj,
						"PersonName")!=null)
				m_Message=m_Message.replace("P10",(String) ProjectUtil.getMemberValue(obj,
						"PersonName"));
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
				m_Message=m_Message.replace("P4",returnDate());
			}
			
			if(m_Message.contains("P5"))
			{
				m_Message=m_Message.replace("P5",returnTime());
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
					m_Message= m_Message.replace("P8","");
			}
	
			if(m_Message.contains("P8"))
			{
				if(ProjectUtil.getMemberValue(obj,
						"ArpBalance")!=null)
				m_Message=m_Message.replace("P8",((BigDecimal) ProjectUtil.getMemberValue(obj,
						"ArpBalance")).toString());
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
			
		}
		message.setText(m_Message);
		ProjectUtil.setMemberValue((CustomBusinessObject) container.getData(), "Message", message.getText());
		
		container.resetValueByTag(4001);
	}
	
}
