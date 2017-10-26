package de.tubs.variantsync.core.syncronization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.FileHelper;
import de.tubs.variantsync.core.utilities.LogOperations;

public class TargetsCalculator {

	ThreeWayMerger merger = new ThreeWayMerger();

	public List<IProject> getTargetsWithoutConflict(IDelta<?> delta) {
		List<IProject> targets = new ArrayList<>();
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		for (IProject project : context.getProjects()) {
			Configuration config = context.getConfigurationForProject(project);
			if (config == null || !config.getSelectedFeatureNames().contains(delta.getFeature())) continue;
			if (project != delta.getProject() && isTargetWithoutConflict(project, delta) && !delta.getSynchronizedProjects().contains(project)) targets.add(project);
		}
		return targets;
	}

	public List<IProject> getTargetsWithConflict(IDelta<?> delta) {
		List<IProject> targets = new ArrayList<>();
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		for (IProject project : context.getProjects()) {
			Configuration config = context.getConfigurationForProject(project);
			if (config == null || !config.getSelectedFeatureNames().contains(delta.getFeature())) continue;
			if (project != delta.getProject() && isTargetWithConflict(project, delta)  && !delta.getSynchronizedProjects().contains(project)) targets.add(project);
		}
		return targets;
	}

	/**
	 * 
	 * @param project
	 * @param delta
	 * @return true, if delta can be applied without problems
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isTargetWithoutConflict(IProject project, IDelta<?> delta) {
		IFile file = project.getFile(delta.getResource().getProjectRelativePath());
		if (!file.exists() && delta.getType().equals(DELTATYPE.ADDED)) {
			return true;
		}
		if (!file.exists()) {
			return false;
		}
		if (file.exists() && delta.getType().equals(DELTATYPE.ADDED)) {
			return false;
		}

		IDeltaFactory factory = null;
		try {
			factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isTargetWithConflict(IProject project, IDelta<?> delta) {
		IFile file = project.getFile(delta.getResource().getProjectRelativePath());

		if (!file.exists() && delta.getType().equals(DELTATYPE.ADDED)) {
			return false;
		}
		if (!file.exists()) {
			return true;
		}
		if (file.exists() && delta.getType().equals(DELTATYPE.ADDED)) {
			return true;
		}

		IDeltaFactory factory = null;
		try {
			factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
		} catch (NoSuchExtensionException e) {
			LogOperations.logError("PatchFactory not found", e);
		}
		if (factory == null) return true;
		if (factory.verifyDelta(file, delta)) return false;
		return true;
	}

	public List<IProject> getTargetsForFeatureExpression(String feature) {
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		List<IProject> targets = new ArrayList<>();
		if (context != null) {
			for (IProject project : context.getProjects()) {
				Configuration config = context.getConfigurationForProject(project);
				if (config != null) {
					if (config.getSelectedFeatureNames().contains(feature)) targets.add(project);
				}
			}
		}
		return targets;
	}
}
