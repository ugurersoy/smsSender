package com.acme.events;

import javax.mail.search.SentDateTerm;

import com.acme.customization.MessageSplitControl;
import com.acme.customization.ProjectUtil;
import com.lbs.controls.JLbsEditorPane;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.util.StringUtil;
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
