package com.acme.customization;

import com.lbs.data.grids.JLbsQuerySelectionGrid;
import com.lbs.data.grids.event.IDataGridSelectionKeyListener;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIDataGridEvent;

public class MobileSubscriberGroupsBrowserHandler extends JLbsCustomXUIEventListener implements IDataGridSelectionKeyListener {

	public MobileSubscriberGroupsBrowserHandler() {
	}

	public void onInitialize(JLbsXUIControlEvent event)
	{
		/** onInitialize : This is the initialization method for XUI forms. The method is called when the form and its components are created and ready to display. Event parameter object (JLbsXUIControlEvent) contains the form object (JLbsXUIPane) in 'component' and 'container' properties, and form's data in 'data' property. This method is meant to be void (no return value is expected). */
		JLbsQuerySelectionGrid grid = (JLbsQuerySelectionGrid) event.getContainer().getComponentByTag(100);
		grid.setDataGridSelectionListener(this);
	}

	public void onGridSetLookupData(JLbsXUIDataGridEvent event)
	{
		/** onGridSetLookupData : This method is called right before the query grid's row is selected for a lookup. Event parameter object contains form object in 'container' property, selected row object (QueryBusinessObject) to be selected in 'data' property, grid component in 'grid' property, and form data (JLbsXUILookupInfo) in 'ctxData' property. A boolean ('true' if the selected row can be selected for lookup) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'true'. If 'false' is returned from this method, lookup selection will be prevented. */
		JLbsQuerySelectionGrid grid = (JLbsQuerySelectionGrid) event.getContainer().getComponentByTag(100);
		JLbsXUILookupInfo lookupInfo = (JLbsXUILookupInfo) event.getLookupData();
		lookupInfo.getParameters().put("MultiSelectionList", grid.getMultiSelectionList());
	}

	@Override
	public Object getKeyAssociatedData(Object arg0, Object arg1) {
		
		return arg1;
	}


}
