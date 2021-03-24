package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.fm.core.EclipseExtensionLoader;
import de.ovgu.featureide.fm.core.ExtensionManager;
import de.ovgu.featureide.fm.core.IExtensionLoader;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.base.DefaultDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;

@SuppressWarnings("rawtypes")
public class DeltaFactoryManager extends ExtensionManager<IDeltaFactory> {

	private static DeltaFactoryManager instance = new DeltaFactoryManager();

	private IExtensionLoader<IDeltaFactory> extensionLoader;
	private final List<IDeltaFactory> extensions = new ArrayList<>();

	public DeltaFactoryManager() {
		final IDeltaFactory[] y = new IDeltaFactory[] { getDefaultFactory() };
		final IExtensionLoader<IDeltaFactory> x = new EclipseExtensionLoader<IDeltaFactory>(VariantSyncPlugin.PLUGIN_ID, IDeltaFactory.extensionPointID,
				IDeltaFactory.extensionID, IDeltaFactory.class);
		setExtensionLoaderInternal(x);
		addExtension(getDefaultFactory());
	}

	public static DeltaFactoryManager getInstance() {
		return instance;
	}

	public static void setExtensionLoader(IExtensionLoader<IDeltaFactory> extensionLoader) {
		instance.setExtensionLoaderInternal(extensionLoader);
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
		for (final IDeltaFactory factory : instance.getExtensions()) {
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

	@Override
	public synchronized List<IDeltaFactory> getExtensions() {
		if (extensionLoader != null) {
			synchronized (extensions) {
				if (extensionLoader != null) {
					extensionLoader.loadProviders(this);
					extensionLoader = null;
				}
			}
		}
		return Collections.unmodifiableList(extensions);
	}

	protected void setExtensionLoaderInternal(IExtensionLoader<IDeltaFactory> extensionLoader) {
		this.extensionLoader = extensionLoader;
	}

	@Override
	public synchronized boolean addExtension(IDeltaFactory extension) {
		if (extension != null) {
			for (final IDeltaFactory t : extensions) {
				if (t.getId().equals(extension.getId())) {
					return false;
				}
			}
			if (extension.initExtension()) {
				extensions.add(extension);
				return true;
			}
		}
		return false;
	}

	@Override
	public IDeltaFactory getExtension(String id) throws NoSuchExtensionException {
		java.util.Objects.requireNonNull(id, "ID must not be null!");

		for (final IDeltaFactory extension : getExtensions()) {
			if (id.equals(extension.getId())) {
				return extension;
			}
		}
		final String errorMsg = String.format("No extension found for ID %s", id);
		throw new NoSuchExtensionException(errorMsg);
	}

}
