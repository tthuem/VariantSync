package de.ovgu.variantsync.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ovgu.variantsync.model.IResourceChangesViewItem;
/**
 * 
 * @author Lei Luo
 *
 */
public class ResourceChangesLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return ((IResourceChangesViewItem) (element)).getImage();
	}

	@Override
	public String getText(Object element) {
		return ((IResourceChangesViewItem) (element)).getName();
	}

}
