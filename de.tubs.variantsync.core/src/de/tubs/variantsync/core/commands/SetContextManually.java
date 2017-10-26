package de.tubs.variantsync.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.tubs.variantsync.core.VariantSyncPlugin;

public class SetContextManually extends AbstractHandler {

	public static final String ID = "de.tubs.variantsync.core.command.context.manually";
	public static final String PARM_MSG = "de.tubs.variantsync.core.command.context.msg";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String msg = event.getParameter(PARM_MSG);
		if (msg != null) {
			System.out.println(msg);
			System.out.println(event);
		}
		return null;
	}

}
