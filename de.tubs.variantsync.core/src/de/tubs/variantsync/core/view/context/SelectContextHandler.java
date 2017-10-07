package de.tubs.variantsync.core.view.context;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.jface.menus.TextState;
import org.eclipse.ui.menus.UIElement;
import de.tubs.variantsync.core.VariantSyncPlugin;

/**
 * @deprecated as eclipse can not update the text when an item is selected
 * @author Christopher Sontag
 *
 * TODO: Update the current label of the toolbar button
 */
public class SelectContextHandler extends AbstractHandler {

	public static final String ID = "de.tubs.variantsync.core.command.context";
	public static final String PARM_MSG = "de.tubs.variantsync.core.command.context.msg";
	private static UIElement updateElement;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String msg = event.getParameter(PARM_MSG);
//		if (event.getCommand().getState(INamedHandleStateIds.NAME)==null) {
//			TextState state = new TextState();
//			state.setShouldPersist(true);
//			state.setValue("Test");
//			event.getCommand().addState(IMenuStateIds.NAME, state);
//			event.getCommand().getState(IMenuStateIds.NAME).setValue("Test");
//		}
		if (msg != null) {
			VariantSyncPlugin.getDefault().getActiveEditorContext().setActualContext(msg);
			System.out.println("Setting context to: " + msg);
//			TextState state = (TextState) event.getCommand().getState(IMenuStateIds.NAME);
//			state.setShouldPersist(true);
//			state.setValue(msg);
//			event.getCommand().addState(IMenuStateIds.NAME, state);
		}
		return null;
	}
	

}
