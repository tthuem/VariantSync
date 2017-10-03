package de.tubs.variantsync.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.builder.ComposerExtensionClass;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantsync.core.persistence.ContextFormat;
import de.tubs.variantsync.core.persistence.FeatureExpressionFormat;

public class VariantSyncComposer extends ComposerExtensionClass {
	
	@Override
	public void performFullBuild(IFile config) {
		VariantSyncPlugin.getDefault().reinit();
	}
	
	@Override
	public void addCompiler(IProject project, String sourcePath, String configPath, String buildPath) {
			File featureExpressionsFile = new File(project.getFile(FeatureExpressionFormat.FILENAME).getLocationURI());
			File contextFile = new File(project.getFile(ContextFormat.FILENAME).getLocationURI());
			try {
				featureExpressionsFile.createNewFile();
				contextFile.createNewFile();
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
	
}
