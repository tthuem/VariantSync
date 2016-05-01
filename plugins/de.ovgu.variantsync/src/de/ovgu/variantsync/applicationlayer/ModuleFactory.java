package de.ovgu.variantsync.applicationlayer;

import de.ovgu.variantsync.applicationlayer.context.ContextProvider;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.deltacalculation.DeltaOperationProvider;
import de.ovgu.variantsync.applicationlayer.deltacalculation.IDeltaOperations;
import de.ovgu.variantsync.applicationlayer.features.FeatureProvider;
import de.ovgu.variantsync.applicationlayer.features.IFeatureOperations;
import de.ovgu.variantsync.applicationlayer.merging.IMergeOperations;
import de.ovgu.variantsync.applicationlayer.merging.MergeOperationProvider;
import de.ovgu.variantsync.applicationlayer.synchronization.ISynchronizationOperations;
import de.ovgu.variantsync.applicationlayer.synchronization.SynchronizationProvider;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.persistencelayer.PersistanceOperationProvider;

/**
 * Creates operation provider which encapsulate functions of a module in
 * application layer.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.05.2015
 */
public class ModuleFactory {

	private ModuleFactory() {
	}

	public static ISynchronizationOperations getSynchronizationOperations() {
		return new SynchronizationProvider();
	}

	public static IDeltaOperations getDeltaOperations() {
		return new DeltaOperationProvider();
	}

	public static IFeatureOperations getFeatureOperations() {
		return new FeatureProvider();
	}

	public static IContextOperations getContextOperations() {
		return new ContextProvider();
	}

	public static IMergeOperations getMergeOperations() {
		return new MergeOperationProvider();
	}

	public static IPersistanceOperations getPersistanceOperations() {
		return new PersistanceOperationProvider();
	}
}