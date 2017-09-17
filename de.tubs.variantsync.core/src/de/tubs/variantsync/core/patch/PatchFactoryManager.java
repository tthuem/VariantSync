package de.tubs.variantsync.core.patch;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.fm.core.CoreExtensionLoader;
import de.ovgu.featureide.fm.core.ExtensionManager;
import de.ovgu.featureide.fm.core.IExtensionLoader;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;

@SuppressWarnings("rawtypes")
public class PatchFactoryManager extends ExtensionManager<IPatchFactory> {

	private static PatchFactoryManager instance = new PatchFactoryManager();
	
	public PatchFactoryManager() {
		setExtensionLoaderInternal(new CoreExtensionLoader<>());
	}

	public static PatchFactoryManager getInstance() {
		return instance;
	}
	
	public static void setExtensionLoader(IExtensionLoader<IPatchFactory> extensionLoader) {
		instance.setExtensionLoaderInternal(extensionLoader);
	}
	
	/**
	 * Returns the factory for a given id
	 * @param id
	 * @return factory which has the given id
	 * @throws NoSuchExtensionException
	 */
	public static IPatchFactory getFactoryById(String id) throws NoSuchExtensionException {
		return instance.getExtension(id);
	}
	
	/**
	 * Returns the specific factory for the given file.
	 * If no factory supports the file then the default factory is returned
	 * @param file
	 * @return factory which supports the file
	 * @throws NoSuchExtensionException
	 */
	public IPatchFactory getFactoryByFile(IFile file) throws NoSuchExtensionException {
		for (IPatchFactory factory : instance.getExtensions()) {
			if (factory.isSupported(file)) {
				return factory;
			}
		}
		return getDefaultFactory();
	}
	
	/**
	 * Returns the default factory (line-based diffs)
	 * @return factory for line-based diffs
	 */
	public static IPatchFactory getDefaultFactory() {
		return DefaultPatchFactory.getInstance();
	}
	
}
