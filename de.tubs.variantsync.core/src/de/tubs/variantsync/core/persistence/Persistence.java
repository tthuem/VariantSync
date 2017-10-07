package de.tubs.variantsync.core.persistence;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

/**
 * Loads and saves objects
 * 
 * @author Christopher Sontag (c.sontag@tu-bs.de)
 * @since 1.0.0.0
 * @TODO Update DOC
 */
public class Persistence {

	/**
	 * Loads all feature expressions
	 * 
	 * @param filename
	 * @return
	 */
	public static List<FeatureExpression> loadFeatureExpressions(IFeatureProject iFeatureProject) {
		List<FeatureExpression> featureExpressions = new ArrayList<>();
		FileHandler.load(Paths.get(iFeatureProject.getProject().getFile(FeatureExpressionFormat.FILENAME).getLocationURI()), featureExpressions,
				new FeatureExpressionFormat());
		return featureExpressions;
	}

	/**
	 * Saves all feature expressions
	 * 
	 * @param context
	 * @param filename
	 */
	public static void writeFeatureExpressions(Context context) {
		FileHandler.save(Paths.get(context.getConfigurationProject().getProject().getFile(FeatureExpressionFormat.FILENAME).getLocationURI()),
				context.getFeatureExpressions(), new FeatureExpressionFormat());
	}

	/**
	 * Loads all feature expressions
	 * 
	 * @param iFeatureProject
	 * @return
	 */
	public static Context loadContext(IFeatureProject iFeatureProject) {
		Context context = new Context();
		if (iFeatureProject != null) {
			FileHandler.load(Paths.get(iFeatureProject.getProject().getFile(ContextFormat.FILENAME).getLocationURI()), context, new ContextFormat());
		}
		return context;
	}

	/**
	 * Saves all feature expressions
	 * 
	 * @param context
	 * @param filename
	 */
	public static void writeContext(Context context) {
		FileHandler.save(Paths.get(context.getConfigurationProject().getProject().getFile(ContextFormat.FILENAME).getLocationURI()), context,
				new ContextFormat());
	}

	/**
	 * 
	 * @param project
	 * @return
	 */
	public static List<SourceFile> loadCodeMapping(IProject project) {
		List<SourceFile> sourceFiles = new ArrayList<>();
		if (project != null) {
			FileHandler.load(Paths.get(project.getFile(CodeMappingFormat.FILENAME).getLocationURI()), sourceFiles, new CodeMappingFormat(project));
		}
		return sourceFiles;
	}

	/**
	 * Saves all feature expressions
	 * 
	 * @param context
	 * @param filename
	 */
	public static void writeCodeMapping(IProject project, List<SourceFile> sourceFiles) {
		if (project != null)
			FileHandler.save(Paths.get(project.getFile(CodeMappingFormat.FILENAME).getLocationURI()), sourceFiles, new CodeMappingFormat(project));
	}

	/**
	 * 
	 * @param project
	 * @return
	 */
	public static List<IPatch<?>> loadPatches(IFeatureProject iFeatureProject) {
		List<IPatch<?>> patches = new ArrayList<>();
		if (iFeatureProject != null) {
			FileHandler.load(Paths.get(iFeatureProject.getProject().getFile(PatchFormat.FILENAME).getLocationURI()), patches, new PatchFormat(iFeatureProject));
		}
		return patches;
	}

	/**
	 * Saves all feature expressions
	 * 
	 * @param context
	 * @param filename
	 */
	public static void writePatches(IFeatureProject iFeatureProject, List<IPatch<?>> patches) {
		if (iFeatureProject != null)
			FileHandler.save(Paths.get(iFeatureProject.getProject().getFile(PatchFormat.FILENAME).getLocationURI()), patches, new PatchFormat(iFeatureProject));
	}

}
