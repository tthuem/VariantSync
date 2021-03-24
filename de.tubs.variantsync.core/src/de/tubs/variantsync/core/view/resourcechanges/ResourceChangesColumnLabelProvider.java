package de.tubs.variantsync.core.view.resourcechanges;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.syncronization.TargetsCalculator;
import de.tubs.variantsync.core.utilities.TreeNode;

/**
 *
 * LabelProvider for {@link ResourcesTree}
 *
 * @author Christopher Sontag
 */
public class ResourceChangesColumnLabelProvider extends CellLabelProvider {

	public enum TYPE {
		DELTATYPE, SOURCE, TARGETSWITHOUTCONFLICT, TARGETSWITHCONFLICT, TIMESTAMP, TARGETSSYNCHRONIZED
	}

	private final TYPE type;
	private final TargetsCalculator targetsCalculator = new TargetsCalculator();

	public ResourceChangesColumnLabelProvider(TYPE type) {
		this.type = type;
	}

	@Override
	public void update(ViewerCell cell) {
		Object o = cell.getElement();
		if (o instanceof TreeNode) {
			o = ((TreeNode) o).getData();
		}

		switch (type) {
		case DELTATYPE:
			if (o instanceof IDelta) {
				switch (((IDelta<?>) o).getType()) {
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
			break;
		case SOURCE:
			if (o instanceof IDelta) {
				final IProject project = ((IDelta<?>) o).getProject();
				if (project != null) {
					cell.setText(project.getName());
				}
			}
			break;
		case TARGETSSYNCHRONIZED:
			if (o instanceof IDelta) {
				String projects = "";
				for (final IProject project : ((IDelta<?>) o).getSynchronizedProjects()) {
					projects += String.format("%s, ", project.getName());
				}
				if (projects.lastIndexOf(",") != -1) {
					projects = projects.substring(0, projects.lastIndexOf(","));
				}
				cell.setText(projects);
			}
			break;
		case TARGETSWITHCONFLICT:
			if (o instanceof IDelta) {
				String projects = "";
				for (final IProject project : targetsCalculator.getTargetsWithConflict(((IDelta<?>) o))) {
					projects += String.format("%s, ", project.getName());
				}
				if (projects.lastIndexOf(",") != -1) {
					projects = projects.substring(0, projects.lastIndexOf(","));
				}
				cell.setText(projects);
			}
			break;
		case TARGETSWITHOUTCONFLICT:
			if (o instanceof IDelta) {
				String projects = "";
				for (final IProject project : targetsCalculator.getTargetsWithoutConflict(((IDelta<?>) o))) {
					projects += String.format("%s, ", project.getName());
				}
				if (projects.lastIndexOf(",") != -1) {
					projects = projects.substring(0, projects.lastIndexOf(","));
				}
				cell.setText(projects);
			}
			break;
		case TIMESTAMP:
			if (o instanceof IDelta) {
				final Timestamp stamp = new Timestamp(((IDelta<?>) o).getTimestamp());
				final Date date = new Date(stamp.getTime());
				cell.setText(date.toString());
			}
			break;
		default:
			break;
		}

	}

}
