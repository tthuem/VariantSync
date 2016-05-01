package de.ovgu.variantsync.presentationlayer.view.context;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

import de.ovgu.variantsync.presentationlayer.controller.ContextController;
import de.ovgu.variantsync.presentationlayer.controller.ControllerHandler;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 17.09.2015
 */
public class PartAdapter implements IPartListener {

	private ContextController cc = ControllerHandler.getInstance()
			.getContextController();

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) ((EditorPart) part)
						.getEditorInput()).getFile();
				System.out
						.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR ======");
				System.out.println(file.getLocation());
				System.out
						.println("===============================================");
				MarkerHandler.getInstance().refreshMarker(file);
				cc.setBaseVersion(file);
			}
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart arg0) {
		// not required
	}

	@Override
	public void partClosed(IWorkbenchPart arg0) {
		// not required
	}

	@Override
	public void partDeactivated(IWorkbenchPart arg0) {
		// not required
	}

	@Override
	public void partOpened(IWorkbenchPart arg0) {
		// not required
	}

}
