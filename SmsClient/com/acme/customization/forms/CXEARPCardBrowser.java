package com.acme.customization.forms;

import java.util.ArrayList;

import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.appobjects.GOBOUser;
import com.lbs.customization.report.customize.online.IOnlineReportingOperations;
import com.lbs.data.factory.IObjectFactory;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.data.query.QueryParams;
import com.lbs.grid.interfaces.ILbsQueryGrid;
import com.lbs.grid.interfaces.IMultiSelectionList;
import com.lbs.util.IObjectCopyListener;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXEARPCardBrowser {

	public CXEARPCardBrowser() {
		// TODO Auto-generated constructor stub
	}

	public void onPopupMenuAction(JLbsXUIControlEvent event)
	{
		/** onPopupMenuAction : This method is called when user selects any item in the form's popup menu. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' and 'component' properties, form data object in 'data' property, selected popup item's id value in 'index' and 'tag' properties, and selected popup item object (JLbsPopupMenuItem) in 'ctxData' property. This method is expected to execute the action corresponding to the selected popup menu item. No return value is expected. */
		 if(event.getIndex() == 60)
		 {
			 	ILbsQueryGrid grid = (ILbsQueryGrid) event.getContainer().getComponentByTag(100);
				IMultiSelectionList list = grid.getMultiSelectionList();
				int errCnt = 0;
				ArrayList arpRefList = new ArrayList();

				if (list != null && list.size() > 0)
				{
					int listSize = list.size();
					Integer arpRef;
					if (listSize == 1)
					{
						arpRef = QueryUtil.getIntegerProp((QueryBusinessObject) grid.getSelectedObject(), "Reference");
						arpRefList.add(arpRef);
					}
					else
					{
						for(int i=0; i<listSize;i++)
						{
							QueryObjectIdentifier id = (QueryObjectIdentifier) list.get(i);
							arpRef = id.getSimpleKey();
							arpRefList.add(arpRef);
						}
					}
					
				}
				CustomBusinessObject alert = ProjectUtil.createNewCBO("CBOSMSAlert");
				CustomBusinessObjects alertUsersList = new CustomBusinessObjects<CustomBusinessObject>();
				
				try {
					QueryParams params = new QueryParams();
					params.getEnabledTerms().enable("T2");
					params.setCustomization(ProjectGlobals.getM_ProjectGUID());
					params.getVariables().put("V_ARPREFS", arpRefList);
					QueryBusinessObjects results = new QueryBusinessObjects();
					IQueryFactory factory = (IQueryFactory) event.getClientContext().getQueryFactory();
					factory.select("CQOGetArpInfo", params, results, -1);
					if (results!=null && results.size() > 0) {
						for (int i = 0; i < results.size(); i++) {
							QueryBusinessObject result = results
									.get(i);
							
							String description = QueryUtil.getStringProp(result, "DESCRIPTION");
							String tckNo = QueryUtil.getStringProp(result, "IDTCNO");
							String phoneNumber = QueryUtil.getStringProp(result, "MOBILEPHONE");

							CustomBusinessObject alertUser = ProjectUtil.createNewCBO("CBOSMSAlertUser");
							ProjectUtil.setMemberValueUn(alertUser, "Title", description);
							ProjectUtil.setMemberValueUn(alertUser, "Phonenumber", phoneNumber);
							ProjectUtil.setMemberValueUn(alertUser, "Tckno", tckNo);
							alertUsersList.add(alertUser);
						}
					}
					
				} 
				catch (Exception e) {
					event.getClientContext()
							.getLogger()
							.error("CQOGetArpInfo query could not be executed properly :",
									e);

				}
				
				ProjectUtil.setMemberValueUn(alert, "AlertUsers", alertUsersList);
				event.getContainer().openChild("Forms/CXFSMSAlert.lfrm", alert, true, JLbsXUITypes.XUIMODE_DEFAULT);
							 
		 }
	}

}
