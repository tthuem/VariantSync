package de.tubs.variantsync.core.view.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

import de.tubs.variantsync.core.jobs.MarkerUpdateJob;

/**
 * 
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.1
 * @since 17.09.2015
 */
public class PartAdapter implements IPartListener {

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
//				System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR (PART BROUGHT TO TOP) ======");
//				System.out.println(file.getLocation());
//				System.out.println("===============================================");
				MarkerUpdateJob job = new MarkerUpdateJob(file);
				job.schedule();
			}
		}

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
//				System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR (PART BROUGHT TO TOP) ======");
//				System.out.println(file.getLocation());
//				System.out.println("===============================================");
				MarkerUpdateJob job = new MarkerUpdateJob(file);
				job.schedule();
			}
		}
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
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
//				System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR (PART BROUGHT TO TOP) ======");
//				System.out.println(file.getLocation());
//				System.out.println("===============================================");
				MarkerUpdateJob job = new MarkerUpdateJob(file);
				job.schedule();
			}
		}
	}

}
