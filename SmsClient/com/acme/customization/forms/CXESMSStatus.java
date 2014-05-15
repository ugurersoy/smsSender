package com.acme.customization.forms;

import javax.swing.SwingUtilities;

import com.lbs.xui.browser.JLbsWebBrowser;
import com.lbs.xui.browser.JLbsWebBrowserDesigner;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXESMSStatus {
	
	JLbsWebBrowser webBrowser; 

	public CXESMSStatus() {
		// TODO Auto-generated constructor stub
	}


	private void navigatePage(JLbsXUIControlEvent event)
	{
		webBrowser = (JLbsWebBrowser) event.getContainer().getComponentByTag(201);
		webBrowser.setBarsVisible(false);
		webBrowser.setJavascriptEnabled(true);
		if (webBrowser != null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					System.out.println("navigate run started...");
					webBrowser.navigate("https://www.smsexplorer.com/panel/login.aspx");
					
				}
			});
		}
	}

	public void onContainerOpened(JLbsXUIControlEvent event)
	{
		navigatePage(event);
		/** onContainerOpened : This method is called right after the form is opened. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' and 'component' properties, and form data object in 'data' property. No return value is expected. */
	}
}
