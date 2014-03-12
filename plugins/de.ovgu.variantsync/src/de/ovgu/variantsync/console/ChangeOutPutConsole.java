package de.ovgu.variantsync.console;

import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.ovgu.variantsync.VariantSyncPlugin;
/**
 * 
 * @author Lei Luo
 *
 */
public class ChangeOutPutConsole extends IOConsole {

	private ConsoleDocument document;
	private IOConsoleOutputStream messageStream;
	private boolean initialized = false;
	private boolean visible = false;
	private static final ImageDescriptor ICON = VariantSyncPlugin
			.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID,
					"icons/VariantSyncSupport.png");

	public ChangeOutPutConsole() {
		super("VariantSync", "VariantSync", ICON);
		document = new ConsoleDocument();
	}

	@Override
	protected void init() {
		super.init();
		VariantSyncPlugin.getStandardDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				initializeStreams();
				dump();
			}
		});
	}

	private synchronized void dump() {
		visible = true;
		String[] lines = document.getLines();
		for (int i = 0; i < lines.length; i++) {
			appendLine(lines[i]);
		}
		document.clear();
	}

	private synchronized void appendLine(String msg) {
		if (visible) {
			try {
				messageStream.write(" " + msg);
				messageStream.write('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			document.appendConsoleLine(msg);
		}
	}

	private synchronized void initializeStreams() {
		if (!initialized) {
			messageStream = newOutputStream();
			initialized = true;
		}
	}

	@Override
	protected synchronized void dispose() {
		visible = false;
	}

	public void shutdown() {
		super.dispose();

	}

	private void bringConsoleToFront() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		if (!visible) {
			manager.addConsoles(new IConsole[] { this });
		}
		manager.showConsoleView(this);
	}

	public void logMessage(String msg) {
		appendLine(msg);
	}
}
