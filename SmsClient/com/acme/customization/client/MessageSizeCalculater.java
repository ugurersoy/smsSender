package com.acme.customization.client;


import java.awt.Color;
import java.text.ParseException;

import com.lbs.controls.JLbsEditorPane;
import com.lbs.controls.numericedit.JLbsNumericEdit;
import com.lbs.xui.JLbsXUIPane;


public class MessageSizeCalculater {
	
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
	
	public static void sizeControl(JLbsXUIPane container, int messageEditorPaneTag,int mainMessageEditorPaneTag, int numericSizeTag, int remainingNumberTag,int smsNumberTag)
	{
		JLbsEditorPane mMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(messageEditorPaneTag)).getInnerComponent());
		JLbsEditorPane mainMessage = ((JLbsEditorPane) ((com.lbs.controls.JLbsScrollPane) container
				.getComponentByTag(mainMessageEditorPaneTag)).getInnerComponent());
		
		JLbsNumericEdit sizeText = (JLbsNumericEdit) container.getComponentByTag(numericSizeTag);
		JLbsNumericEdit remainingText = (JLbsNumericEdit) container.getComponentByTag(remainingNumberTag);	
		JLbsNumericEdit smsText = (JLbsNumericEdit) container.getComponentByTag(smsNumberTag);
		
		if(mMessage.getText().length()>612)
		{
			String textmMessage = mMessage.getText().substring(0,612);
			mMessage.setText(textmMessage);
			
		    int mainLength= mainMessage.getText().length()-1;
			String testMainMessage= mainMessage.getText().substring(0,mainLength);
			mainMessage.setText(testMainMessage);
			
			sizeText.setForeground(Color.RED);
			remainingText.setForeground(Color.RED);
			smsText.setForeground(Color.RED);
		}else 
		{
			sizeText.setForeground(Color.BLACK);
			remainingText.setForeground(Color.BLACK);
			smsText.setForeground(Color.BLACK);
		}
		 
		
	}
}