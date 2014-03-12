package de.ovgu.variantsync.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.model.IResourceChangesViewItem;
import de.ovgu.variantsync.model.ResourceChangesFilePatch;
import de.ovgu.variantsync.model.ResourceChangesFolder;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesColumnLabelProvider extends CellLabelProvider {

	private int headID = 0;

	public ResourceChangesColumnLabelProvider(int i) {
		this.headID = i;
	}

	@Override
	public void update(ViewerCell cell) {
		Object o = cell.getElement();
		if (o instanceof ResourceChangesFilePatch
				&& ((ResourceChangesFilePatch) o).isSynchronisiert()) {
			cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		} else {
			cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		}
		if (headID == 0) {
			cell.setImage(new ResourceChangesLabelProvider().getImage(o));
			if (o instanceof ResourceChangesFilePatch) {
				cell.setText(((ResourceChangesFilePatch) o).getStatus() + "     "
						+ ((ResourceChangesFilePatch) o).getTime());
			} else {
				cell.setText(new ResourceChangesLabelProvider().getText(o));
			}
		}
		if (headID == 1) {
			if (o instanceof ResourceChangesFilePatch) {
				IProject project = ((ResourceChangesFilePatch) o).getProject();
				if (project != null) {
					cell.setText(project.getName());
				}
			}
		}
		if (headID == 2) {
			if (o instanceof ResourceChangesFilePatch) {
				String projects = "";
				ArrayList<IProject> projectList = ((ResourceChangesFilePatch) o)
						.getProjectList();
				for (IProject project : projectList) {
					projects = projects + project.getName() + " ";
				}
				cell.setText(projects);
			}
		}
		if (headID == 3) {
			if (o instanceof ResourceChangesFilePatch) {
				String projects = "";
				ArrayList<String> projectList = ((ResourceChangesFilePatch) o)
						.getSynchoProjects();
				if (projectList != null) {
					for (String project : projectList) {
						projects = projects + project + " ";
					}
					cell.setText(projects);
				}
			}
		}
	}
}
