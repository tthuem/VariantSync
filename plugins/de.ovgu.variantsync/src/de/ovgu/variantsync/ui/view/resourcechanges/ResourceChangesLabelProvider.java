package de.ovgu.variantsync.ui.view.resourcechanges;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ovgu.variantsync.applicationlayer.datamodel.resources.IChangedFile;

/**
 * 
 * @author Lei Luo
 *
 */
public class ResourceChangesLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return ((IChangedFile) (element)).getImage();
	}

	@Override
	public String getText(Object element) {
		return ((IChangedFile) (element)).getName();
	}

}
