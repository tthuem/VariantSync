package de.tubs.variantsync.core.syncronization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.patch.PatchFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

public class TargetsCalculator {

	public List<IProject> getTargetsWithoutConflict(IDelta<?> delta) {
		List<IProject> targets = new ArrayList<>();
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		for (IProject project : context.getProjects()) {
			Configuration config = context.getConfigurationForProject(project);
			if (config == null || !config.getSelectedFeatureNames().contains(delta.getFeature())) continue;
			if (project != delta.getProject() && isTargetWithoutConflict(project, delta)) targets.add(project);
		}
		return targets;
	}

	public List<IProject> getTargetsWithConflict(IDelta<?> delta) {
		List<IProject> targets = new ArrayList<>();
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		for (IProject project : context.getProjects()) {
			Configuration config = context.getConfigurationForProject(project);
			if (config == null || !config.getSelectedFeatureNames().contains(delta.getFeature())) continue;
			if (project != delta.getProject() && isTargetWithConflict(project, delta)) targets.add(project);
		}
		return targets;
	}

	/**
	 * 
	 * @param project
	 * @param delta
	 * @return true, if delta can be applied without problems
	 */
	public boolean isTargetWithoutConflict(IProject project, IDelta<?> delta) {
		IFile file = project.getFile(delta.getResource().getProjectRelativePath());
		if (!file.exists()) {
			return true;
		}

		IPatchFactory factory = null;
		try {
			factory = PatchFactoryManager.getFactoryById(delta.getFactoryId());
		} catch (NoSuchExtensionException e) {
			LogOperations.logError("PatchFactory not found", e);
		}
		if (factory == null) return false;
		if (factory.verifyDelta(file, delta)) return true;
		return false;
	}

	/**
	 * 
	 * @param project
	 * @param delta
	 * @return true, if delta can be applied only with problems
	 */
	public boolean isTargetWithConflict(IProject project, IDelta<?> delta) {
		IFile file = project.getFile(delta.getResource().getProjectRelativePath());
		if (!file.exists()) {
			return false;
		}

		IPatchFactory factory = null;
		try {
			factory = PatchFactoryManager.getFactoryById(delta.getFactoryId());
		} catch (NoSuchExtensionException e) {
			LogOperations.logError("PatchFactory not found", e);
		}
		if (factory == null) return true;
		if (factory.verifyDelta(file, delta)) return false;
		return true;
	}
}
