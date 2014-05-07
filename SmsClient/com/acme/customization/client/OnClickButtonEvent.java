package com.acme.customization.client;

import java.util.ArrayList;

import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.JLbsScrollPane;
import com.lbs.grids.JLbsObjectListGrid;
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
				EntityParameter param = (EntityParameter) list.get(i);
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
			MessageSplitControl.messageCalculaterControlEvent(event, tagNumberMessage, messageTag, gridTag);
		}
		
		
		
	}


}
