package de.tubs.variantsync.core.patch.base;

import de.tubs.variantsync.core.patch.APatch;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;


public class DefaultPatchFactory implements IPatchFactory {

	@Override
	public IPatch<IDelta<?>> createPatch(String feature) {
		IPatch<IDelta<?>> patch = new APatch<IDelta<?>>();
		patch.setFeature(feature);
		patch.setStartTime(System.currentTimeMillis());
		return patch;
	}

}
