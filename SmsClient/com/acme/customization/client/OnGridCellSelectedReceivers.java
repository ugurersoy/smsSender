package com.acme.customization.client;

import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIGridEvent;

public class OnGridCellSelectedReceivers {

	
	public static void OnCellSelected(JLbsXUIGridEvent event,
			int mainMessageTag, int messageTag, int gridTag) {
	
		MessageSplitControl.messageCalculaterGridEvent(event, mainMessageTag, messageTag, gridTag);
		JLbsXUIPane container = event.getContainer();
		container.resetValueByTag(messageTag);
	}
}
