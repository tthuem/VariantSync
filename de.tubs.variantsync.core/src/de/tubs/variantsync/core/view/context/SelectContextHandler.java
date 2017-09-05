package de.tubs.variantsync.core.view.context;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.commands.INamedHandleStateIds;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.jface.menus.TextState;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.utilities.IEventListener;
import de.tubs.variantsync.core.utilities.VariantSyncEvent;

/**
 * 
 * @author Christopher Sontag
 *
 *         TODO: Update the current label of the toolbar button
 */
public class SelectContextHandler extends AbstractHandler {

	public static final String ID = "de.tubs.variantsync.core.command.context";
	public static final String PARM_MSG = "de.tubs.variantsync.core.command.context.msg";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String msg = event.getParameter(PARM_MSG);
		if (event.getCommand().getState(INamedHandleStateIds.NAME)==null) {
			TextState state = new TextState();
			state.setShouldPersist(true);
			state.setValue("Test");
			event.getCommand().addState(IMenuStateIds.NAME, state);
			event.getCommand().getState(IMenuStateIds.NAME).setValue("Test");
		}
		if (msg != null) {
			VariantSyncPlugin.getContext().setActualContext(msg);
			System.out.println("Setting context to: " + msg);
			TextState state = (TextState) event.getCommand().getState(IMenuStateIds.NAME);
			state.setShouldPersist(true);
			state.setValue(msg);
			event.getCommand().addState(IMenuStateIds.NAME, state);
		}
		return null;
	}
//
//	@Override
//	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
//		String msg = (String) parameters.get(PARM_MSG);
//		System.out.println("Update: " + parameters.keySet().toString());
//		if (msg != null) {
//			if (VariantSyncPlugin.getContext().getActualContext().equals(msg))
//				element.setChecked(true);
//			else
//				element.setChecked(false);
//		}
//	}
//
//	@Override
//	public void handleStateChange(State state, Object oldValue) {
//		// TODO Auto-generated method stub
//		
//	}
//	
	

}
