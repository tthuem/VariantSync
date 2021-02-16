package de.tubs.variantsync.core.patch.base;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.patch.APatch;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

public class DefaultPatchFactory implements IPatchFactory {

	@Override
	public IPatch<IDelta<?>> createPatch(String context) {
		final IPatch<IDelta<?>> patch = new APatch<IDelta<?>>();
		patch.setContext(context);
		patch.setStartTime(System.currentTimeMillis());
		return patch;
	}

	@Override
	public IPatch<IDelta<?>> createPatch() {
		final IPatch<IDelta<?>> patch = new APatch<IDelta<?>>();
		return patch;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IFile applyDelta(IFile file, IPatch<?> patch) {
		IFile newFile = file;
		for (final IDelta delta : patch.getDeltas()) {
			try {
				final IDeltaFactory<?> factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
				newFile = factory.applyDelta(newFile, delta);
			} catch (final NoSuchExtensionException e) {
				LogOperations.logError(String.format("Could not find extension with id: %s", delta.getFactoryId()), e);
			}
		}
		return newFile;
	}

}
