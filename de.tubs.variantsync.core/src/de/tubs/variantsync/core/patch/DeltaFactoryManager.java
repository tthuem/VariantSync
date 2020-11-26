package de.tubs.variantsync.core.patch;

import org.eclipse.core.resources.IFile;

//import de.ovgu.featureide.fm.core.CoreExtensionLoader;
import de.ovgu.featureide.fm.core.EclipseExtensionLoader;
import de.ovgu.featureide.fm.core.ExtensionManager;
import de.ovgu.featureide.fm.core.IExtensionLoader;
import de.tubs.variantsync.core.patch.base.DefaultDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;

@SuppressWarnings("rawtypes")
public class DeltaFactoryManager extends ExtensionManager<IDeltaFactory> {

	private static DeltaFactoryManager instance = new DeltaFactoryManager();

	public DeltaFactoryManager() {
		IDeltaFactory[] y = new IDeltaFactory[]{getDefaultFactory()};
		//IExtensionLoader<IDeltaFactory> x = new EclipseExtensionLoader<IDeltaFactory>(y);
		//setExtensionLoader(x);
		addExtension(getDefaultFactory());
	}

	public static DeltaFactoryManager getInstance() {
		return instance;
	}

	public static void setExtensionLoader(IExtensionLoader<IDeltaFactory> extensionLoader) {
		instance.setExtensionLoader(extensionLoader);
	}

	/**
	 * Returns the factory for a given id
	 * 
	 * @param id
	 * @return factory which has the given id
	 * @throws NoSuchExtensionException
	 */
	public static IDeltaFactory getFactoryById(String id) throws NoSuchExtensionException {
		return instance.getExtension(id);
	}

	/**
	 * Returns the specific factory for the given file. If no factory supports the file then the default factory is returned
	 * 
	 * @param file
	 * @return factory which supports the file
	 * @throws NoSuchExtensionException
	 */
	public IDeltaFactory getFactoryByFile(IFile file) throws NoSuchExtensionException {
		for (IDeltaFactory factory : instance.getExtensions()) {
			if (factory.isSupported(file)) {
				return factory;
			}
		}
		return getDefaultFactory();
	}

	/**
	 * Returns the default factory (line-based diffs)
	 * 
	 * @return factory for line-based diffs
	 */
	public static IDeltaFactory getDefaultFactory() {
		return DefaultDeltaFactory.getInstance();
	}

}
