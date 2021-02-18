package de.tubs.variantsync.core.managers;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

public abstract class AManager {

	/**
	 * Wrapper for {@link VariantSyncPlugin#fireEvent(VariantSyncEvent)}
	 *
	 * @param variantSyncEvent
	 */
	protected void fireEvent(VariantSyncEvent variantSyncEvent) {
		VariantSyncPlugin.getDefault().fireEvent(variantSyncEvent);
	}

}
