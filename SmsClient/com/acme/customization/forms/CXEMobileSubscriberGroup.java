package com.acme.customization.forms;

import com.acme.customization.shared.ProjectUtil;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.grids.JLbsObjectListGrid;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIGridEvent;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXEMobileSubscriberGroup extends JLbsCustomXUIEventListener {

	public CXEMobileSubscriberGroup() {
		// TODO Auto-generated constructor stub
	}

	public void onGridLookup(JLbsXUIGridEvent event)
	{
		/** onGridLookup : This method is called when a lookup is initiated from a grid cell. Event parameter object (JLbsXUIGridEvent) contains form object in 'container' property, grid row data object in 'data' property, grid component in 'grid' property, row number in 'row' property (starts from 0), column number in 'column' property (starts from 0), column's tag value in 'columnTag' property, and the editor component that belongs to the cell that is subject to the lookup in 'editor' property. A boolean ('true' if the lookup is successful) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'false'. */
		JLbsXUILookupInfo info = new JLbsXUILookupInfo();
		event.getContainer().openChild("CXFMobileSubscribersBrowser.lfrm", info, true,
				JLbsXUITypes.XUIMODE_DBSELECT);
	}

	public void onInitialize(JLbsXUIControlEvent event)
	{
	 
		CustomBusinessObject data = (CustomBusinessObject) event.getData();
		data.createLinkedObjects();
		CustomBusinessObjects grpLnsLink = (CustomBusinessObjects)ProjectUtil.getMemberValue(data, "MblInfoUsrGrpLnsLink");
		if (grpLnsLink == null)
		{
			ProjectUtil.setMemberValueUn(data, "MblInfoUsrGrpLnsLink", new CustomBusinessObjects());
		}
		if (grpLnsLink.size() == 0)
		{
			CustomBusinessObject grpLine = ProjectUtil.createNewCBO("CBOMblInfoUsrGrpLn");
			ProjectUtil.setMemberValueUn(grpLine, "Linenr", 1);
			grpLnsLink.add(grpLine);
			JLbsObjectListGrid grid =  ((com.lbs.grids.JLbsObjectListGrid)event.getContainer().getComponentByTag(3000008));
			grid.rowListChanged();
		}
	}

	public void onGridRowInserted(JLbsXUIGridEvent event)
	{
		/** onGridRowInserted : This method is called right after a new row is added to an edit grid. Event parameter object (JLbsXUIGridEvent) contains form object in 'container' property, grid row data object in 'data' property, grid component in 'grid' property, and row number in 'row' property (starts from 0). A boolean ('true' if the row data object is changed in this method) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'false'. */
		CustomBusinessObject grpLine = ProjectUtil.createNewCBO("CBOMblInfoUsrGrpLn");
		ProjectUtil.setMemberValueUn(grpLine, "Linenr", event.getRow() + 1);
		 event.setCtxData(grpLine);
		 event.getEditGrid().rowListChanged();
	}

}
