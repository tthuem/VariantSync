package de.tubs.variantsync.core.view.context;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import de.tubs.variantsync.core.VariantSyncPlugin;

public class ActiveContextHandler extends AbstractHandler {

	private boolean oldValue;
	

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
	    oldValue = HandlerUtil.toggleCommandState(command);
	    VariantSyncPlugin.getContext().setActive(!oldValue);
		
		return null;
	}

}
