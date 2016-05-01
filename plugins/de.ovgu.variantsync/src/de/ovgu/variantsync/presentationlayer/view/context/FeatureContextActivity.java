package de.ovgu.variantsync.presentationlayer.view.context;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.presentationlayer.controller.ContextController;
import de.ovgu.variantsync.presentationlayer.controller.ControllerHandler;

public class FeatureContextActivity extends AbstractHandler implements
		IElementUpdater {

	private static ContextController controller = ControllerHandler
			.getInstance().getContextController();
	private static UIElement staticElement;
	private static boolean isEnabled = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (isEnabled) {
			long timeOfContext = (System.nanoTime() - FeatureContextSelection.time) / 1000000000 / 60;
			MessageDialog dialog = new MessageDialog(
					display.getActiveShell(),
					"Feature Context",
					null,
					"Context for feature expression "
							+ ControllerHandler.getInstance()
									.getContextController()
									.getActiveFeatureContext()
							+ " is active for "
							+ timeOfContext
							+ " minutes.\nDo you want to stop the recording of this context?",
					MessageDialog.QUESTION, new String[] { "Yes, stop it.",
							"No, continue recording.", }, 0);
			int result = dialog.open();
			if (result == 0) {
				staticElement.setIcon(VariantSyncPlugin
						.imageDescriptorFromPlugin(
								VariantSyncConstants.PLUGIN_ID,
								"icons/inactiveContext.png"));
				staticElement.setText("context is disabled");
				ControllerHandler.getInstance().getContextController()
						.stopContextRecording();
				isEnabled = false;
			}
		} else {
			MessageDialog
					.openInformation(display.getActiveShell(), "Info",
							"You have not chosen a context. So, the default context is active.");
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element,
			@SuppressWarnings("rawtypes") Map parameters) {
		staticElement = element;
		String context = controller.getActiveFeatureContext();
		if (context == null || context.isEmpty()) {
			context = "context not active";
		}
		staticElement.setText(context);
	}

	public static void refreshMenuItem() {
		isEnabled = true;
		StringBuilder context = new StringBuilder(
				controller.getActiveFeatureContext());
		String project = controller.getActiveProject();
		if ((context.length() + project.length()) <= 20) {
			context.append("/");
			context.append(project);
		} else {
			staticElement.setTooltip("Project: " + project);
		}
		if (context == null || context.capacity() == 0) {
			context.append("default context active");
		}
		staticElement.setIcon(VariantSyncPlugin.imageDescriptorFromPlugin(
				VariantSyncConstants.PLUGIN_ID, "icons/activeContext.png"));
		staticElement.setText(context.toString());
	}
}