package de.tubs.variantsync.core.utilities.event;

import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent;

/**
 * Broadcasts {@link FeatureIDEEvent Events} to the corresponding {@link IEventListener IFeatureModelListeners}.
 *
 * @author Sebastian Krieter
 */
public interface IEventManager {

	void addListener(IEventListener listener);

	void fireEvent(VariantSyncEvent event);

	void removeListener(IEventListener listener);

}
