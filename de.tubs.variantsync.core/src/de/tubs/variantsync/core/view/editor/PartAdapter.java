package de.tubs.variantsync.core.view.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.CodeLine;
import de.tubs.variantsync.core.markers.MarkerHandler;
import de.tubs.variantsync.core.markers.MarkerInformation;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.1
 * @since 17.09.2015
 */
public class PartAdapter implements IPartListener {

	@Override
	public void partActivated(IWorkbenchPart part) {
		// if (part instanceof IEditorPart) {
		// if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
		// IFile file = ((IFileEditorInput) ((EditorPart)
		// part).getEditorInput()).getFile();
		// System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR (PART
		// ACTIVATED) ======");
		// System.out.println(file.getLocation());
		// System.out.println("===============================================");
		// // MarkerHandler.getInstance().refreshMarker(file);
		// // cc.setBaseVersion(file);
		// // if (cc.isFeatureView() || cc.isProductView()) {
		// // cc.stopContextRecording();
		// // cc.setFeatureView(false);
		// // cc.setProductView(false);
		// // }
		// }
		// }

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
				System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR (PART BROUGHT TO TOP) ======");
				System.out.println(file.getLocation());
				System.out.println("===============================================");
				try {
					MarkerHandler.getInstance().clearResource(file);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MarkerHandler.addMarker(file, 1, 5, "Test", FeatureColor.Light_Green);
				// List<CodeLine> codeLines = VariantSyncPlugin.getContext().getMapping(file);
				//
				// List<MarkerInformation> markers = new ArrayList<>();
				//
				// MarkerInformation mi_test = new MarkerInformation(0, 0, 1, 0, 0);
				// mi_test.setFeatureExpression(VariantSyncPlugin.getContext().getFeatureExpressions().get(0));
				// markers.add(mi_test);
				//
				//
				// if (codeLines != null) {
				// for (CodeLine cl : codeLines) {
				// MarkerInformation mi = new MarkerInformation(0, cl.getLine(), cl.getLine(),
				// 0, 0);
				// mi.setFeatureExpression(cl.getFeatureExpression());
				// markers.add(mi);
				// }
				// }
				// MarkerHandler.getInstance().setMarker(file, markers);

				// MarkerHandler.getInstance().refreshMarker(file);
				// cc.setBaseVersion(file);
				// if (cc.isFeatureView() || cc.isProductView()) {
				// cc.stopContextRecording();
				// cc.setFeatureView(false);
				// cc.setProductView(false);
				// }
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
				System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR (PART OPENED) ======");
				System.out.println(file.getLocation());
				System.out.println("===============================================");
				// MarkerHandler.getInstance().refreshMarker(file);
				// cc.setBaseVersion(file);
				// if (cc.isFeatureView() || cc.isProductView()) {
				// cc.stopContextRecording();
				// cc.setFeatureView(false);
				// cc.setProductView(false);
				// }
			}
		}
	}

}
