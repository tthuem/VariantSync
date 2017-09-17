package de.tubs.variantsync.core.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.persistence.FeatureExpressionFormat;

public class LabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IFile)
			if  (FeatureExpressionFormat.FILENAME.equals(((IFile) element).getName()))
				return VariantSyncPlugin.getDefault().getImageDescriptor("icons/FeatureExpressionSmall.png").createImage();
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof FeatureExpression) {
			FeatureExpression data = (FeatureExpression) element;
			return data.name;
		}  
		return null;
	}

}
