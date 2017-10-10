package de.tubs.variantsync.core.preferences;

import java.util.List;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.DefaultDeltaFactory;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class VariantSyncPerferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public VariantSyncPerferencePage() {
		super(FLAT);
		setPreferenceStore(VariantSyncPlugin.getDefault().getPreferenceStore());
		setDescription("General VarianSync Preferences:");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@SuppressWarnings("rawtypes")
	public void createFieldEditors() {
		List<IDeltaFactory> factories = DeltaFactoryManager.getInstance().getExtensions();
		String[][] strings = new String[factories.size()][2];
		for (int i=0; i<factories.size(); i++) {
			DefaultDeltaFactory factory = (DefaultDeltaFactory) factories.get(i);
			strings[i][0] = factory.getName();
			strings[i][1] = factory.getId();
		}
		
		addField(new ComboFieldEditor("PatchFactory", "File Comparing Method:", 
				strings, getFieldEditorParent()));
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}