package de.tubs.variantsync.core.syncronization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

/**
 * Calculates all target projects for given deltas.
 *
 * @author Christopher Sontag
 */
public class TargetsCalculator {

	/**
	 * Returns target projects without conflicts.
	 *
	 * @param delta
	 * @return
	 */
	public List<IProject> getTargetsWithoutConflict(IDelta<?> delta) {
		final List<IProject> targets = new ArrayList<>();
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		for (final IProject project : configurationProject.getVariants()) {
			final Configuration config = configurationProject.getConfigurationForVariant(project);
			if ((config == null) || !config.getSelectedFeatureNames().contains(delta.getContext())) {
				continue;
			}
			if ((project != delta.getProject()) && isTargetWithoutConflict(project, delta) && !delta.getSynchronizedProjects().contains(project)) {
				targets.add(project);
			}
		}
		return targets;
	}

	/**
	 * Returns target projects with conflicts.
	 *
	 * @param delta
	 * @return
	 */
	public List<IProject> getTargetsWithConflict(IDelta<?> delta) {
		final List<IProject> targets = new ArrayList<>();
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		for (final IProject project : configurationProject.getVariants()) {
			final Configuration config = configurationProject.getConfigurationForVariant(project);
			if ((config == null) || !config.getSelectedFeatureNames().contains(delta.getContext())) {
				continue;
			}
			if ((project != delta.getProject()) && isTargetWithConflict(project, delta) && !delta.getSynchronizedProjects().contains(project)) {
				targets.add(project);
			}
		}
		return targets;
	}

	/**
	 * Returns all target projects which have feature context selected in their configuration
	 *
	 * @param deltas
	 * @return
	 */
	public List<IProject> getTargetsForFeatureContext(List<IDelta<?>> deltas) {
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		final List<IProject> targets = new ArrayList<>();
		if (configurationProject != null) {
			for (final IProject project : configurationProject.getVariants()) {
				final Configuration config = configurationProject.getConfigurationForVariant(project);
				if (config != null) {
					for (final IDelta<?> delta : deltas) {
						if (config.getSelectedFeatureNames().contains(delta.getContext()) && !targets.contains(project) && (!delta.getProject().equals(project))
							&& !delta.getSynchronizedProjects().contains(project)) {
							targets.add(project);
						}
					}
				}
			}
		}
		return targets;
	}

	/**
	 * Checks whether the given delta can be applied to the given project without conflicts.
	 *
	 * @param project
	 * @param delta
	 * @return true, if delta can be applied without problems
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isTargetWithoutConflict(IProject project, IDelta<?> delta) {
		final IFile file = project.getFile(delta.getResource().getProjectRelativePath());
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
		} catch (final NoSuchExtensionException e) {
			LogOperations.logError("PatchFactory not found", e);
		}
		if (factory == null) {
			return false;
		}
		if (factory.verifyDelta(file, delta)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the given delta can be applied to the given project with conflicts.
	 *
	 * @param project
	 * @param delta
	 * @return true, if delta can be applied only with problems
	 */
	public boolean isTargetWithConflict(IProject project, IDelta<?> delta) {
		return !isTargetWithoutConflict(project, delta);
	}
}
