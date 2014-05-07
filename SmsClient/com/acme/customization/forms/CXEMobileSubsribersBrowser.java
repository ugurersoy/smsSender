package com.acme.customization.forms;


import com.lbs.data.grids.JLbsQuerySelectionGrid;
import com.lbs.data.grids.event.IDataGridSelectionKeyListener;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIGridEvent;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIDataGridEvent;

public class CXEMobileSubsribersBrowser extends JLbsCustomXUIEventListener implements IDataGridSelectionKeyListener {

	public CXEMobileSubsribersBrowser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onGridGetCellValue(JLbsXUIGridEvent event)
	{
		/** onGridGetCellValue : This method is called to get the cell value of each grid cell. It is called once for each visible grid cell in the form. Grid cell's value is bound to a property most of the times, but there are some situations where the cell's display value is different than its value or the cell's value is a calculated value; in these situations this mehtod supplies the display value of the cell. Event parameter object (JLbsXUIGridEvent) contains form object in 'container' property, grid row data object in 'data' property, grid component in 'grid' property, row number in 'row' property (starts from 0), column number in 'column' property (starts from 0), and column's tag value in 'columnTag' property. An object representing cell value is expected as the return value. */
		if(event.getColumnTag() == 1006)
		{
			QueryBusinessObject qbo = (QueryBusinessObject) event.getData();
			Integer type = (Integer) qbo.getProperties().getValue("MBLINFUSER_USERTYPE");
			switch (type) {
			case 0:
				event.setReturnObject("Kullanýcý");
				return ;
			case 1:
				event.setReturnObject("Cari");
				return;
			case 2:
				event.setReturnObject("Diðer");
				return;
			default:
				break;
			}
		}
	}

	@Override
	public Object getKeyAssociatedData(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg1;
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

}
