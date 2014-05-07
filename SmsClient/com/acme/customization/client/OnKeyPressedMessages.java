package com.acme.customization.client;

import com.lbs.xui.JLbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class OnKeyPressedMessages {

	

	public static void OnKeyPress(JLbsXUIControlEvent event,
			int mainMessageTag, int messageTag, int gridTag) {
		
		MessageSplitControl.messageCalculaterControlEvent(event, mainMessageTag, messageTag, gridTag);
		JLbsXUIPane container = event.getContainer();
		
		container.resetValueByTag(messageTag);
	}

}
