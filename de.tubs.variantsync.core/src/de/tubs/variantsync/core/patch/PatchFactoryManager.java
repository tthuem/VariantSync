package de.tubs.variantsync.core.patch;

import de.ovgu.featureide.fm.core.CoreExtensionLoader;
import de.ovgu.featureide.fm.core.ExtensionManager;
import de.ovgu.featureide.fm.core.IExtensionLoader;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
import de.tubs.variantsync.core.preferences.PreferenceConstants;

@SuppressWarnings("rawtypes")
public class PatchFactoryManager extends ExtensionManager<IPatchFactory> {

	private static PatchFactoryManager instance = new PatchFactoryManager();
	
	public PatchFactoryManager() {
		setExtensionLoaderInternal(new CoreExtensionLoader<>(getDefaultFactory()));
	}

	public static PatchFactoryManager getInstance() {
		return instance;
	}
	
	public static void setExtensionLoader(IExtensionLoader<IPatchFactory> extensionLoader) {
		instance.setExtensionLoaderInternal(extensionLoader);
		instance.addExtension(getDefaultFactory());
	}
	
	public static IPatchFactory getFactoryById(String id) throws NoSuchExtensionException {
		return instance.getExtension(id);
	}
	
	/**
	 * Returns the Factory choosen by the user in the settings
	 * @return IPatchFactory - PatchFactory
	 * @throws NoSuchExtensionException - Default factory is missing as extension
	 */
	public IPatchFactory getFactory() throws NoSuchExtensionException {
		return getFactoryById(VariantSyncPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATCHFACTORY));
	}
	
	public static IPatchFactory getDefaultFactory() {
		return DefaultPatchFactory.getInstance();
	}
	
}
