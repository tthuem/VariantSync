package de.ovgu.variantsync.ui.view.resourcechanges;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.ControllerProperties;
import de.ovgu.variantsync.ui.controller.ControllerTypes;
import de.ovgu.variantsync.ui.controller.ProjectController;
import de.ovgu.variantsync.ui.controller.SynchronizationController;
import de.ovgu.variantsync.ui.view.AbstractView;

/**
 * Provides content for cell header in table of resource changes view.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class ResourceChangesColumnLabelProvider extends CellLabelProvider
		implements AbstractView {

	private int headID = 0;
	private ViewerCell cell;
	private ProjectController projectController = ControllerHandler
			.getInstance().getProjectController();
	private SynchronizationController synchronizationController = ControllerHandler
			.getInstance().getSynchronizationController();
	private ResourceChangesFilePatch patch;

	/**
	 * Registers this instance as view which can receive events from controller
	 * as part of MCV-Implementation.
	 * 
	 * @param i
	 * @param controller
	 */
	public ResourceChangesColumnLabelProvider(int i) {
		this.headID = i;
		VariantSyncPlugin.getDefault().registerView(this,
				ControllerTypes.PROJECT);
		VariantSyncPlugin.getDefault().registerView(this,
				ControllerTypes.SYNCHRONIZATION);
	}

	@Override
	public void update(ViewerCell cell) {
		this.cell = cell;
		Object o = cell.getElement();
		if (o instanceof ResourceChangesFilePatch
				&& ((ResourceChangesFilePatch) o).isSynchronized()) {
			cell.setBackground(Display.getDefault().getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			cell.setBackground(Display.getDefault().getSystemColor(
					SWT.COLOR_WHITE));
		}
		if (headID == 0) {
			cell.setImage(new ResourceChangesLabelProvider().getImage(o));
			if (o instanceof ResourceChangesFilePatch) {
				cell.setText(((ResourceChangesFilePatch) o).getStatus()
						+ "     " + ((ResourceChangesFilePatch) o).getTime());
			} else {
				cell.setText(new ResourceChangesLabelProvider().getText(o));
			}
		}
		if (headID == 1 && o instanceof ResourceChangesFilePatch) {
			IProject project = ((ResourceChangesFilePatch) o).getProject();
			if (project != null) {
				cell.setText(project.getName());
			}
		}
		if (headID == 2 && o instanceof ResourceChangesFilePatch) {
			patch = (ResourceChangesFilePatch) o;
			projectController.getProjectNames(patch);
		}
		if (headID == 3 && o instanceof ResourceChangesFilePatch) {
			patch = (ResourceChangesFilePatch) o;
			synchronizationController.getSynchronizedProjects(patch);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (cell != null
				&& headID == 2
				&& evt.getPropertyName().equals(
						ControllerProperties.PROJECTNAMES_PROPERTY
								.getProperty())) {
			String projects = "";
			List<String> projectNames = (List<String>) evt.getNewValue();
			for (String name : projectNames) {
				projects = projects + name + " ";
			}
			cell.setText(projects);
		} else if (cell != null
				&& headID == 3
				&& evt.getPropertyName().equals(
						ControllerProperties.SYNCHRONIZEDPROJECTS_PROPERTY
								.getProperty())) {
			List<String> projectList = (List<String>) evt.getNewValue();
			String projects = "";
			if (projectList != null) {
				for (String project : projectList) {
					projects = projects + project + " ";
				}
				cell.setText(projects);
			}
		}
	}
}
