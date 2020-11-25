package de.tubs.variantsync.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.builder.ComposerExtensionClass;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantsync.core.managers.persistence.FeatureContextFormat;

public class VariantSyncComposer extends ComposerExtensionClass {

	@Override
	public void performFullBuild(Path config) {
//		public void performFullBuild(IFile config) {
		VariantSyncPlugin.getConfigurationProjectManager().reinitialize();
	}

	@Override
	public void addCompiler(IProject project, String sourcePath, String configPath, String buildPath) {
		File featureContextFile = new File(project.getFile(FeatureContextFormat.FILENAME).getLocationURI());
		try {
			featureContextFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public boolean hasBuildFolder() {
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

	@Override
	public void postCompile(IResourceDelta delta, IFile buildFile) {

	}

	@Override
	public void copyNotComposedFiles(Configuration config, IFolder destination) {

	}

	@Override
	public void buildPartialFeatureProjectAssets(IFolder sourceFolder, ArrayList<String> removedFeatures,
			ArrayList<String> mandatoryFeatures) throws IOException, CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean supportsPartialFeatureProject() {
		// TODO Auto-generated method stub
		return false;
	}

}
