package de.ovgu.variantsync;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
/**
 * 
 * @author Lei Luo
 *
 */
public class VSyncSupportProjectDecorator implements ILightweightLabelDecorator {

	public static final String DECORATOR_ID = VariantSyncPlugin.PLUGIN_ID
			+ ".VSyncSupportProjectDecorator";
	private static final ImageDescriptor OVERLAY = VariantSyncPlugin
			.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID,
					"icons/VariantSyncSupport2.png");

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		IProject project = (IProject) element;
		try {
			if (project.isOpen()
					&& project.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
				decoration.addOverlay(OVERLAY, IDecoration.BOTTOM_LEFT);
			}
		} catch (CoreException e) {
			VariantSyncLog.logError(e);
		}
	}
}
