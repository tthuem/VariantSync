package de.ovgu.variantsync.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;

import de.ovgu.variantsync.VariantSyncPlugin;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ConsoleFactory implements IConsoleFactory {

	@Override
	public void openConsole() {
		showConsole();
	}

	private void showConsole() {
		ChangeOutPutConsole console = VariantSyncPlugin.getDefault().getConsole();
		if (console != null) {
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			IConsole[] existing = manager.getConsoles();
			boolean exist = false;
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i]) {
					exist = true;
				}
			}
			if (!exist) {
				manager.addConsoles(new IConsole[] { console });
			}
			manager.showConsoleView(console);
		}

	}

}
