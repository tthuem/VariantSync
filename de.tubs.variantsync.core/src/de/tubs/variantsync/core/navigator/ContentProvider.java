package de.tubs.variantsync.core.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITreeContentProvider;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.persistence.FeatureExpressionFormat;

public class ContentProvider implements ITreeContentProvider {

	private static final Object[] NO_CHILDREN = new Object[0];
	private IFile parent;
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = null;
		if (parentElement instanceof FeatureExpression) { 
			children = NO_CHILDREN;
		} else if(parentElement instanceof IFile) {
			/* possible feature expressions file */
			IFile modelFile = (IFile) parentElement;
			if(FeatureExpressionFormat.FILENAME.equals(modelFile.getName())) {		
				parent = modelFile;
				children = VariantSyncPlugin.getDefault().getContext(modelFile.getProject()).getFeatureExpressions().toArray();
			}
		}   
		return children != null ? children : NO_CHILDREN;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof FeatureExpression) {
			return parent;
		} 
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof FeatureExpression) {
			return false;		
		} else if(element instanceof IFile) {
			return FeatureExpressionFormat.FILENAME.equals(((IFile) element).getName());
		}
		return false;
	}

}
