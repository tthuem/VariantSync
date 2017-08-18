package de.tubs.variantsync.core.utilities;

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
