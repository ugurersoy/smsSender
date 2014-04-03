package com.acme.events;

import java.util.ArrayList;

import com.acme.customization.MessageSplitControl;
import com.acme.customization.ProjectUtil;
import com.acme.entity.entityParameter;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.util.StringUtil;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class OnClickButtonEvent {

	public void addParameterOnGrid(JLbsXUIControlEvent event,
			int tagNumberMessage, int tagNumberGrid,Integer gridTag,Integer messageTag) {
		
		String addMessage="";

		JLbsXUIPane container = event.getContainer();
		// ((JLbsEditorPane)((com.lbs.controls.JLbsScrollPane)container.getComponentByTag(3000007)).getInnerComponent()).getText();
		JLbsEditorPane messageTemplate = ((JLbsEditorPane) ((JLbsScrollPane) container
				.getComponentByTag(tagNumberMessage)).getInnerComponent());
		
		
		int possion=messageTemplate.getCaretPosition();
		
		JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
				.getComponentByTag(tagNumberGrid);

		ArrayList list = (ArrayList) objectGrid.getDisplayList();

		for (int i = 0; i < list.size(); i++) {
			if (list.indexOf(list.get(i)) == objectGrid.getSelectedRow()) {
				entityParameter param = (entityParameter) list.get(i);
				if (!messageTemplate.getText().isEmpty()) {
				
					if(possion>=messageTemplate.getText().length())
					{
						messageTemplate.setText(messageTemplate.getText() + " "
								+ param.parameter);
					}
					else{
						messageTemplate.setText(messageTemplate.getText().substring(0,possion) + param.parameter + messageTemplate.getText().substring(possion));
						
					}
				
				} else{
					messageTemplate.setText(param.parameter);
				  }
				
				break;
			}
		}
		
		
		if (gridTag != null&& !messageTemplate.getText().isEmpty()) {
			JLbsObjectListGrid messageReveiverGrid = (JLbsObjectListGrid) container
					.getComponentByTag(gridTag);
			
			JLbsEditorPane otherMessage = ((JLbsEditorPane) ((JLbsScrollPane) container
					.getComponentByTag(messageTag)).getInnerComponent());

			MessageSplitControl control = new MessageSplitControl();
			
			
			String strlist[] = control.splitControl(messageTemplate.getText());
            CustomBusinessObject obj =   (CustomBusinessObject) messageReveiverGrid.getRowObject(messageReveiverGrid.getSelectedRow());  
		
            
			for (int i = 0; i < strlist.length; i++) {

				if (control.controlParamsText(strlist[i])) {
					if(strlist[i]!=null){
						addMessage+=strlist[i];
						otherMessage.setText(strlist[i]);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".adý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(
							obj, "Name");
					if(strlist[i]!=null){
					addMessage +=strlist[i];
					otherMessage.setText(addMessage);
				}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Soyadý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(
							obj, "SurName");
					if(strlist[i]!=null){
					addMessage+=strlist[i];
					otherMessage.setText(addMessage);
					}
					
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Telfon Numarasý.")) {
					strlist[i] = (String) ProjectUtil.getMemberValue(
							obj, "Phonenumber");
					if(strlist[i]!=null){
					addMessage+=strlist[i];
					otherMessage.setText(addMessage);
					}
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Tarih.")) {

					continue;
				}
				if (StringUtil.equals(strlist[i], ".Saat.")) {
					continue;
				}
				if (StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")) {
					continue;
				}
				if (StringUtil
						.equals(strlist[i], ".Cari Hesap Unvaný.")) {
					continue;
				}
				if (StringUtil.equals(strlist[i],
						".Cari Hesap Bakiyesi.")) {
					continue;
				}
				
			}

		}
		
		
		
	}


}
