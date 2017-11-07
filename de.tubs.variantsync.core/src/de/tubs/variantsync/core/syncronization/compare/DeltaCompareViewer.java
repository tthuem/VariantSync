package de.tubs.variantsync.core.syncronization.compare;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.utilities.FileHelper;

/**
 * Wizard for synchronization of manual deltas
 * 
 * @author Christopher Sontag
 */
@Deprecated
public class DeltaCompareViewer extends Wizard {

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".views.synchronization.compare";
	private DeltaCompareViewerPage page;
	private IFile file;
	private IDelta<?> delta;

	public DeltaCompareViewer(IFile file, IDelta<?> delta) {
		super();
		setWindowTitle("Synchronize Delta");
		this.file = file;
		this.delta = delta;
		page = new DeltaCompareViewerPage(file, delta);
	}

	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		List<String> lines = page.getSourceCode();
		FileHelper.setFileLines(file, lines);
		return true;
	}

	@Override
	public boolean isHelpAvailable() {
		return false;
	}

}
