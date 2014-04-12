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
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class DoubleClickOnGridEvent {
	
	public void addDoubleClickOnText(JLbsXUIGridEvent event,int tagNumberMessage,int tagNumberGrid,Integer gridTag,Integer messageTag)
	{
		String addMessage="";
		 JLbsXUIPane container = event.getContainer();
			//	((JLbsEditorPane)((com.lbs.controls.JLbsScrollPane)container.getComponentByTag(3000007)).getInnerComponent()).getText();
				JLbsEditorPane messageTemplate =((JLbsEditorPane)((JLbsScrollPane)container.getComponentByTag(tagNumberMessage)).getInnerComponent());

				JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
						.getComponentByTag(tagNumberGrid);

				int possion=messageTemplate.getCaretPosition();
				
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
					MessageSplitControl. messageCalculaterGridEvent(event, tagNumberMessage, messageTag, gridTag);

				}
	 }
	}


