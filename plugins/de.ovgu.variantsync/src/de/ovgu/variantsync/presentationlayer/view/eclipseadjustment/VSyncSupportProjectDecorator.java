package de.ovgu.variantsync.presentationlayer.view.eclipseadjustment;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * 
 * @author Lei Luo
 *
 */
public class VSyncSupportProjectDecorator implements ILightweightLabelDecorator {

	public static final String DECORATOR_ID = VariantSyncConstants.PLUGIN_ID
			+ ".VSyncSupportProjectDecorator";
	private static final ImageDescriptor OVERLAY = VariantSyncPlugin
			.imageDescriptorFromPlugin(VariantSyncConstants.PLUGIN_ID,
					"icons/VariantSyncSupport2.png");

	@Override
	public void addListener(ILabelProviderListener listener) {
		// not required
	}

	@Override
	public void dispose() {
		// not required
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// not required
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
			LogOperations.logError(
					"Project nature support could not be checked.", e);
		}
	}
}
