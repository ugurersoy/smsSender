package com.acme.customization;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.acme.entity.entityParameter;
import com.acme.enums.Parameters;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.buttonpanel.JLbsPrefixPanel;
import com.lbs.controls.maskededit.JLbsTextEdit;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class MessageTemplateHandler extends JLbsCustomXUIEventListener {
	private Parameters parameter[] = Parameters.values();

	public MessageTemplateHandler() {
		// TODO Auto-generated constructor stub
	}

	public void onInitialize(JLbsXUIControlEvent event) {
		JLbsXUIPane container = event.getContainer();

		ArrayList parameterList = new ArrayList();

		for (Parameters param : parameter) {
			parameterList.add(new entityParameter(param.getParameterName()));
		}

		JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
				.getComponentByTag(3000005);
		objectGrid.setObjectClass(parameterList.getClass());
		objectGrid.setObjects(parameterList);

	}

	public void parameterOnClick(JLbsXUIControlEvent event) {
		JLbsXUIPane container = event.getContainer();

		JLbsTextEdit messageTemplate = (JLbsTextEdit) container
				.getComponentByTag(3000007);

		JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
				.getComponentByTag(3000005);

		ArrayList list = (ArrayList) objectGrid.getDisplayList();

		for (int i = 0; i < list.size(); i++) {
			if (list.indexOf(list.get(i)) == objectGrid.getSelectedRow()) {
				entityParameter param = (entityParameter) list.get(i);
				if(!messageTemplate.getText().isEmpty())
				{
					messageTemplate.setText(messageTemplate.getText()+" "+param.parameter);
				}
				else 
				messageTemplate.setText(param.parameter);
				continue;
			}
		}

	}

	public void callParameter(JLbsXUIGridEvent event) {

	}

	public void selectParameterDoubleClick(JLbsXUIGridEvent event)
	{
		JLbsXUIPane container = event.getContainer();

		JLbsTextEdit messageTemplate = (JLbsTextEdit) container
				.getComponentByTag(3000007);

		JLbsObjectListGrid objectGrid = (JLbsObjectListGrid) container
				.getComponentByTag(3000005);

		ArrayList list = (ArrayList) objectGrid.getDisplayList();

		for (int i = 0; i < list.size(); i++) {
			if (list.indexOf(list.get(i)) == objectGrid.getSelectedRow()) {
				entityParameter param = (entityParameter) list.get(i);
				if(!messageTemplate.getText().isEmpty())
				{
					messageTemplate.setText(messageTemplate.getText()+" "+param.parameter);
				}
				else 
				messageTemplate.setText(param.parameter);
				continue;
			}
		}
	}

}
