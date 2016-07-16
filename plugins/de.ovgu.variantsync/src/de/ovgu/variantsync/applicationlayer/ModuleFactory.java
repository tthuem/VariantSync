package de.ovgu.variantsync.applicationlayer;

import de.ovgu.variantsync.applicationlayer.context.ContextOperations;
import de.ovgu.variantsync.applicationlayer.context.ContextProvider;
import de.ovgu.variantsync.applicationlayer.deltacalculation.DeltaOperationProvider;
import de.ovgu.variantsync.applicationlayer.deltacalculation.DeltaOperations;
import de.ovgu.variantsync.applicationlayer.features.FeatureOperations;
import de.ovgu.variantsync.applicationlayer.features.FeatureProvider;
import de.ovgu.variantsync.applicationlayer.merging.MergeOperationProvider;
import de.ovgu.variantsync.applicationlayer.merging.Merging;
import de.ovgu.variantsync.applicationlayer.synchronization.SynchronizationProvider;
import de.ovgu.variantsync.applicationlayer.synchronization.Synchronizationable;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.io.PersistanceOperationProvider;

/**
 * Creates operation provider which encapsulate functions of a module in
 * application layer.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.05.2015
 */
public class ModuleFactory {

	private ModuleFactory() {
	}

	public static Synchronizationable getSynchronizationOperations() {
		return new SynchronizationProvider();
	}

	public static DeltaOperations getDeltaOperations() {
		return new DeltaOperationProvider();
	}

	public static FeatureOperations getFeatureOperations() {
		return new FeatureProvider();
	}

	public static ContextOperations getContextOperations() {
		return ContextProvider.getInstance();
	}

	public static Merging getMergeOperations() {
		return new MergeOperationProvider();
	}

	public static Persistable getPersistanceOperations() {
		return PersistanceOperationProvider.buildProvider();
	}
}