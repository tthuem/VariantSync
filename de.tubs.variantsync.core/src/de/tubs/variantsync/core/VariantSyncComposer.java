package de.tubs.variantsync.core;

import org.eclipse.core.resources.IFile;
import de.ovgu.featureide.core.builder.ComposerExtensionClass;

public class VariantSyncComposer extends ComposerExtensionClass {
	
	@Override
	public void performFullBuild(IFile config) {
		VariantSyncPlugin.getDefault().reinit();
	}

	@Override
	public boolean hasSourceFolder() {
		return false;
	}
	
	@Override
	public boolean hasFeatureFolder() {
		return false;
	}
	
	@Override
	public boolean createFolderForFeatures() {
		return false;
	}
	
	@Override
	public boolean showContextFieldsAndMethods() {
		return false;
	}

	@Override
	public boolean hasMetaProductGeneration() {
		return false;
	}

	@Override
	public Mechanism getGenerationMechanism() {
		return null;
	}

	@Override
	public boolean supportsAndroid() {
		return false;
	}

	@Override
	public boolean supportsMigration() {
		return false;
	}

	@Override
	public boolean clean() {
		return false;
	}
	
	
	
}
