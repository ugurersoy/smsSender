package com.acme.customization.forms;

import java.util.ArrayList;

import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.data.query.QueryParams;
import com.lbs.grid.interfaces.ILbsQueryGrid;
import com.lbs.grid.interfaces.IMultiSelectionList;
import com.lbs.hr.em.EMConstants;
import com.lbs.util.QueryUtil;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXEPersonCardBrowser {

	public CXEPersonCardBrowser() {
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
				ArrayList personRefList = new ArrayList();

				if (list != null && list.size() > 0)
				{
					int listSize = list.size();
					Integer personRef;
					if (listSize == 1)
					{
						personRef = QueryUtil.getIntegerProp((QueryBusinessObject) grid.getSelectedObject(), "Logical_Reference");
						personRefList.add(personRef);
					}
					else
					{
						for(int i=0; i<listSize;i++)
						{
							QueryObjectIdentifier id = (QueryObjectIdentifier) list.get(i);
							personRef = id.getSimpleKey();
							personRefList.add(personRef);
						}
					}
					
				}
				CustomBusinessObject alert = ProjectUtil.createNewCBO("CBOSMSAlert");
				CustomBusinessObjects userList = ProjectUtil.getUserListWithPersonInfo(event.getClientContext(), personRefList);
				for (int i = 0; i < userList.size(); i++)
				{
					CustomBusinessObject user = (CustomBusinessObject) userList.get(i);
					ProjectUtil.setMemberValueUn(user, "UserType",  ProjectGlobals.USER_TYPE_EMPLOYEE);
					ProjectUtil.setMemberValueUn(user, "CardReference",  ProjectUtil.getBOIntFieldValue(user, "PersonRef"));
					ProjectUtil.setMemberValueUn(user, "Name",  ProjectUtil.getBOStringFieldValue(user, "PersonName"));
					ProjectUtil.setMemberValueUn(user, "SurName",  ProjectUtil.getBOStringFieldValue(user, "PersonSurName"));
					ProjectUtil.setMemberValueUn(user, "Title",  ProjectUtil.getBOStringFieldValue(user, "PersonName") + ' ' +  ProjectUtil.getBOStringFieldValue(user, "PersonSurName"));
					ProjectUtil.setMemberValueUn(user, "Tckno",  ProjectUtil.getBOStringFieldValue(user, "PersonIdTCNo"));
					ProjectUtil.setMemberValueUn(user, "Phonenumber",  ProjectUtil.getBOStringFieldValue(user, "PersonPhonenumber"));
					
				}
				
				
				ProjectUtil.setMemberValueUn(alert, "AlertUsers", userList);
				event.getContainer().openChild("Forms/CXFSMSAlert.lfrm", alert, true, JLbsXUITypes.XUIMODE_DEFAULT);
							 
		 }
	}

}
