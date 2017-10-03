package de.tubs.variantsync.core.view.resourcechanges;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.utilities.TreeNode;

public class ResourceChangesColumnLabelProvider extends CellLabelProvider {

	private int column;

	public ResourceChangesColumnLabelProvider(int column) {
		this.column = column;
	}

	@Override
	public void update(ViewerCell cell) {
		Object o = cell.getElement();
		if (o instanceof TreeNode) {
			o = ((TreeNode) o).getData();
		}

		if (o instanceof IDelta && ((IDelta<?>) o).isSynchronized()) {
			cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		} else {
			cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		}

		if (column == 0) {
			if (o instanceof IDelta) {
				switch(((IDelta<?>) o).getType()) {
				case ADDED:
					cell.setText("ADDED");
					cell.setImage(VariantSyncPlugin.getDefault().getImageDescriptor("icons/add_obj.gif").createImage());
					break;
				case CHANGED:
					cell.setText("CHANGED");
					cell.setImage(VariantSyncPlugin.getDefault().getImageDescriptor("icons/change_obj.gif").createImage());
					break;
				case REMOVED:
					cell.setText("REMOVED");
					cell.setImage(VariantSyncPlugin.getDefault().getImageDescriptor("icons/delete_obj.gif").createImage());
					break;
				default:
					break;
				}
			} else {
				cell.setText(o.toString());
			}
		}
		if (column == 1 && o instanceof IDelta) {
			IProject project = ((IDelta<?>) o).getProject();
			if (project != null) {
				cell.setText(project.getName());
			}
		}
		if (column == 2 && o instanceof IDelta) {
			// TODO: Calculate possible targets
		}
		if (column == 3 && o instanceof IDelta) {
			// TODO: Calculate Targets
		}
		if (column == 4 && o instanceof IDelta) {
			Timestamp stamp = new Timestamp(((IDelta<?>) o).getTimestamp());
			Date date = new Date(stamp.getTime());
			cell.setText(date.toString());
		}
	}

}
