package de.tubs.variantsync.core.utilities.event;

/**
 * Interface for components listening to events fired by other components. <br/>
 * Some classes of FeatureIDE use the observer-pattern, to notify listening
 * clients. For instance, a feature model fires a "model data changed" event
 * when the model is changed. Listening clients, e.g., the diagram editor can
 * react on this event. <br/>
 * <br/>
 * The follow sketch outlines the observer-pattern usage in FeatureIDE, taken
 * {@link de.ovgu.featureide.fm.ui.editors.FeatureDiagramEditor} and
 * {@link FeatureModel} as example. <code><pre>
 * IEventListener  <--- propertyChange is called ---------
 *       |                                               |
 *       |                                               |
 *  implements                                      fires event
 *       |                                               |
 *       V                                               |       										  
 *  Receiver -- register  as listener ---> Sender
 * </pre></code> <br/>
 * Please note that events fired and received over this interface are from type
 * {@link VariantSyncEvent}.
 * 
 * @author Sebastian Krieter
 */
public interface IEventListener {

	/**
	 * This method is called whenever the
	 * 
	 * @param event
	 */
	void propertyChange(VariantSyncEvent event);

}
