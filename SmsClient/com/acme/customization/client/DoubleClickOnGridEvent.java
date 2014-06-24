package com.acme.customization.client;

import java.util.ArrayList;

import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class DoubleClickOnGridEvent {
	
	public void addDoubleClickOnText(JLbsXUIGridEvent event,int tagNumberMessage,int tagNumberGrid,Integer gridTag,Integer messageTag,CustomBusinessObject m_SMSAlert)
	{
		 JLbsXUIPane container = event.getContainer();
			//	((JLbsEditorPane)((com.lbs.controls.JLbsScrollPane)container.getComponentByTag(3000007)).getInnerComponent()).getText();
				JLbsEditorPane messageTemplate =((JLbsEditorPane)((JLbsScrollPane)container.getComponentByTag(tagNumberMessage)).getInnerComponent());

				JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
						.getComponentByTag(tagNumberGrid);

				int possion=messageTemplate.getCaretPosition();
				
				ArrayList list = (ArrayList) objectGrid.getDisplayList();

				for (int i = 0; i < list.size(); i++) {
					if (list.indexOf(list.get(i)) == objectGrid.getSelectedRow()) {
						EntityParameter param = (EntityParameter) list.get(i);
						if (!messageTemplate.getText().isEmpty()) {
							
							if(possion>=messageTemplate.getText().length())
							{
								messageTemplate.setText(messageTemplate.getText() + " "
										+ param.paramName);
							}
							else{
								messageTemplate.setText(messageTemplate.getText().substring(0,possion) +" "+param.paramName+" "+ messageTemplate.getText().substring(possion));
								
							}
						
						} else{
							messageTemplate.setText(param.paramName+" ");
						  }
						
						break;
					}
				}
				
				if (gridTag != null&& !messageTemplate.getText().isEmpty()) {
					MessageSplitControl. messageCalculaterGridEvent(event, tagNumberMessage, messageTag, gridTag,m_SMSAlert);

				}
	 }
	}


