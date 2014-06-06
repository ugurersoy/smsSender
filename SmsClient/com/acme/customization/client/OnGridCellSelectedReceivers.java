package com.acme.customization.client;

import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class OnGridCellSelectedReceivers {

	
	public static void OnCellSelected(JLbsXUIGridEvent event,
			int mainMessageTag, int messageTag, int gridTag,CustomBusinessObject m_SMSAlert) {
		MessageSplitControl.messageCalculaterGridEvent(event, mainMessageTag, messageTag, gridTag,m_SMSAlert);
		JLbsXUIPane container = event.getContainer();
		container.resetValueByTag(messageTag);
	}
}
