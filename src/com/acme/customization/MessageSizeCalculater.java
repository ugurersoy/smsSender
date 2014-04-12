package com.acme.customization;
import java.text.ParseException;

import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.numericedit.JLbsNumericEdit;
import com.lbs.xui.JLbsXUIPane;


public class MessageSizeCalculater {
	
	private static int size;
	private static int remaining;
	
	
	public static void messageFindSize(JLbsXUIPane container, int numericSizeTag, int remainingNumberTag,int messageEditorPaneTag,int smsNumberTag) throws ParseException
	{    
		 int size=0;
		
		JLbsNumericEdit sizeText = (JLbsNumericEdit) container.getComponentByTag(numericSizeTag);
		JLbsNumericEdit remainingText = (JLbsNumericEdit) container.getComponentByTag(remainingNumberTag);	
		JLbsNumericEdit smsText = (JLbsNumericEdit) container.getComponentByTag(smsNumberTag);
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageEditorPaneTag)).getInnerComponent());
		
		if(!mainMessage.getText().isEmpty())
		{
			 size=mainMessage.getText().length();
		     sizeText.setNumber(size);  
		     remainingText.setNumber(612-size);
		
		     if(sizeText.getNumber().intValue()<= 160)
		     {
		    	smsText.setNumber(1);
		     }else if(sizeText.getNumber().intValue()>160&&sizeText.getNumber().intValue()<=306)
		     {
		    	 smsText.setNumber(2);
		     }else if(sizeText.getNumber().intValue()>306&&sizeText.getNumber().intValue()<=459)
		     {
		    	 smsText.setNumber(3);
		     }else if(sizeText.getNumber().intValue()>459&&sizeText.getNumber().intValue()<=612)
		     {
		    	 smsText.setNumber(4);
		     }else if(sizeText.getNumber().intValue()>612){
		    	 remainingText.setNumber(0);
		    	 sizeText.setNumber(612);
		     }
		     
		}else 
		{
			return;
		}
	}

}
