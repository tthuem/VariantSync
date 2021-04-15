package de.tubs.variantsync.core.monitor;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.exceptions.DiffException;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.nature.VariantNature;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.base.DefaultPatchFactory;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

/**
 * Visits given resource delta and reacts on added, removed or changed resource deltas. "A resource delta represents changes in the state of a resource tree
 * between two discrete points in time" - @see org.eclipse.core.resources.IResourceDelta. A resource tree represents a project of workspace.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.1
 * @since 15.05.2015
 */
@SuppressWarnings("rawtypes")
class ResourceChangeVisitor implements IResourceDeltaVisitor {

	/**
	 * Called from ResourceChangeListener if a resource change have happened. Filters changed resource and handle change event.
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		final IResource res = delta.getResource();
		final IProject project = res.getProject();

		if (!filterResource(project, res)) {
			return false;
		}

		analyseDeltaType(delta);
		return true;
	}

	/**
	 * Triggers action depending on kind of delta. Possible actions: add, remove or change file in admin folder.
	 *
	 * @param type kind of delta
	 */
	private void analyseDeltaType(IResourceDelta delta) {
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			handleAddedResource(delta);
			break;
		case IResourceDelta.REMOVED:
			handleRemovedResource(delta);
			break;
		case IResourceDelta.CHANGED:
			handleChangedResource(delta);
			break;
		default:
			break;
		}
	}

	/**
	 * Adds patch file to admin folder.
	 *
	 * @param res changed resource
	 * @param delta resource delta
	 * @param flag
	 */
	@SuppressWarnings("unchecked")
	private void handleAddedResource(IResourceDelta delta) {
		if ((delta.getResource().getType() == IResource.FILE)
			&& (((delta.getFlags() & IResourceDelta.MARKERS) == 0) || ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0))) {
			final IFile file = (IFile) delta.getResource();
			final ConfigurationProject configurationProject = VariantSyncPlugin.getConfigurationProjectManager().getConfigurationProject(file.getProject());
			try {
				if (configurationProject.isActive() && !configurationProject.getFeatureContextManager().isDefault()) {
					final IDeltaFactory factory = DeltaFactoryManager.getInstance().getFactoryByFile(file);
					IPatch patch;
					if (configurationProject.getPatchesManager().getActualContextPatch() == null) {
						final IPatchFactory patchFactory = new DefaultPatchFactory();
						patch = patchFactory.createPatch(configurationProject.getFeatureContextManager().getActual());
						VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(file, EventType.PATCH_ADDED, null, patch));
					} else {
						patch = configurationProject.getPatchesManager().getActualContextPatch();
					}
					final List<IDelta<?>> deltas = factory.createDeltas(file, file.getModificationStamp(), IDelta.DELTATYPE(delta.getKind()));
					patch.addDeltas(deltas);
					configurationProject.getPatchesManager().setActualContextPatch(patch);
					VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(file, EventType.PATCH_CHANGED, null, patch));
					//TODO: AST REFACTORING
					CodeMappingHandler.addCodeMappingsForDeltas(deltas);
				}
			} catch (final DiffException ex) {
				LogOperations.logError("Patch could not be created", ex);
			} catch (final NoSuchExtensionException ex) {
				LogOperations.logError("PatchFactory Extension does not exist!", ex);
			}
		}
		LogOperations.logInfo(String.format("Resource %s was added with flag %s", delta.getResource().getFullPath(), getFlagText(delta.getFlags())));
	}

	/**
	 * Removes patch file from admin folder.
	 *
	 * @param res changed resource
	 * @param delta resource delta
	 */
	@SuppressWarnings("unchecked")
	private void handleRemovedResource(IResourceDelta delta) {
		if ((delta.getResource().getType() == IResource.FILE)
			&& (((delta.getFlags() & IResourceDelta.MARKERS) != 0) || ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0))) {
			final IFile file = (IFile) delta.getResource();
			final ConfigurationProject configurationProject = VariantSyncPlugin.getConfigurationProjectManager().getConfigurationProject(file.getProject());
			try {
				if (configurationProject.isActive() && !configurationProject.getFeatureContextManager().isDefault()) {
					final IDeltaFactory factory = DeltaFactoryManager.getInstance().getFactoryByFile(file);
					IPatch patch;
					if (configurationProject.getPatchesManager().getActualContextPatch() == null) {
						final IPatchFactory patchFactory = new DefaultPatchFactory();
						patch = patchFactory.createPatch(configurationProject.getFeatureContextManager().getActual());
						VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(file, EventType.PATCH_ADDED, null, patch));
					} else {
						patch = configurationProject.getPatchesManager().getActualContextPatch();
					}
					final List<IDelta<?>> deltas = factory.createDeltas(file, file.getModificationStamp(), IDelta.DELTATYPE(delta.getKind()));
					patch.addDeltas(deltas);
					configurationProject.getPatchesManager().setActualContextPatch(patch);
					VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(file, EventType.PATCH_CHANGED, null, patch));
					//TODO: AST REFACTORING
					CodeMappingHandler.addCodeMappingsForDeltas(deltas);
				}
			} catch (final DiffException ex) {
				LogOperations.logError("Patch could not be created", ex);
			} catch (final NoSuchExtensionException ex) {
				LogOperations.logError("PatchFactory Extension does not exist!", ex);
			}
		}
		LogOperations.logInfo(String.format("Resource %s was removed with flag %s", delta.getResource().getFullPath(), getFlagText(delta.getFlags())));
	}

	/**
	 * Creates patch for changed resource.
	 *
	 * @param res changed resource
	 * @param delta resource delta
	 */
	@SuppressWarnings("unchecked")
	private void handleChangedResource(IResourceDelta delta) {
		if ((delta.getResource().getType() == IResource.FILE) && ((delta.getFlags() & IResourceDelta.CONTENT) != 0)) {
			final IFile file = (IFile) delta.getResource();
			final IFileState[] states;
			try {
				states = file.getHistory(null);
				if (states.length > 0) {
					final long t = states[0].getModificationTime();

					final ConfigurationProject configurationProject =
						VariantSyncPlugin.getConfigurationProjectManager().getConfigurationProject(file.getProject());
					try {
						if (configurationProject.isActive() && !configurationProject.getFeatureContextManager().isDefault()) {
							final IDeltaFactory factory = DeltaFactoryManager.getInstance().getFactoryByFile(file);
							IPatch patch;
							if (configurationProject.getPatchesManager().getActualContextPatch() == null) {
								final IPatchFactory patchFactory = new DefaultPatchFactory();
								patch = patchFactory.createPatch(configurationProject.getFeatureContextManager().getActual());
								VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(file, EventType.PATCH_ADDED, null, patch));
							} else {
								patch = configurationProject.getPatchesManager().getActualContextPatch();
							}
							final List<IDelta<?>> deltas = factory.createDeltas(file, states[0], t, IDelta.DELTATYPE(delta.getKind()));
							patch.addDeltas(deltas);
							configurationProject.getPatchesManager().setActualContextPatch(patch);
							VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(file, EventType.PATCH_CHANGED, null, patch));
							//TODO: AST REFACTORING
							CodeMappingHandler.addCodeMappingsForDeltas(deltas);
						}
					} catch (final DiffException ex) {
						LogOperations.logError("Patch could not be created", ex);
					} catch (final NoSuchExtensionException ex) {
						LogOperations.logError("PatchFactory Extension does not exist!", ex);
					}
				}
			} catch (final CoreException e) {
				LogOperations.logError("File states could not be retrieved.", e);
			}
			LogOperations.logInfo(String.format("Resource %s was changed with flag %s", delta.getResource().getFullPath(), getFlagText(delta.getFlags())));
		}
	}

	/**
	 * Checks if resource does not fulfill any following criteria:<br> <ul> <li>project has nature support</li> <li>project is open</li> <li>resource still
	 * exists</li> <li>resource is a file</li> <li>resource starts not with \".\"</li> </ul>
	 *
	 * @param project project resource belongs to
	 * @param res resource to check
	 * @return true if any criteria was fulfilled
	 * @throws CoreException
	 */
	private boolean filterResource(IProject project, IResource res) throws CoreException {
		if ((project != null) && project.isOpen() && !project.hasNature(VariantNature.NATURE_ID)) {
			return false;
		}
		if ((project != null) && !project.isOpen()) {
			return false;
		}
		final String name = res.getName();
		return !(res.isDerived() || name.startsWith("."));
	}

	/**
	 * Adds debug messages.
	 *
	 * @param flag
	 * @return debug message
	 */
	private String getFlagText(int flag) {
		String flags = "F_";
		if ((flag & IResourceDelta.ADDED) != 0) {
			flags += "ADDED ";
		}
		if ((flag & IResourceDelta.ADDED_PHANTOM) != 0) {
			flags += "ADDED_PHANTOM ";
		}
		if ((flag & IResourceDelta.ALL_WITH_PHANTOMS) != 0) {
			flags += "ALL_WITH_PHANTOMS ";
		}
		if ((flag & IResourceDelta.CHANGED) != 0) {
			flags += "CHANGED ";
		}
		if ((flag & IResourceDelta.CONTENT) != 0) {
			flags += "CONTENT ";
		}
		if ((flag & IResourceDelta.COPIED_FROM) != 0) {
			flags += "COPIED_FROM ";
		}
		if ((flag & IResourceDelta.DERIVED_CHANGED) != 0) {
			flags += "DERIVED_CHANGED ";
		}
		if ((flag & IResourceDelta.DESCRIPTION) != 0) {
			flags += "DESCRIPTION ";
		}
		if ((flag & IResourceDelta.ENCODING) != 0) {
			flags += "ENCODING ";
		}
		if ((flag & IResourceDelta.LOCAL_CHANGED) != 0) {
			flags += "LOCAL_CHANGED ";
		}
		if ((flag & IResourceDelta.MARKERS) != 0) {
			flags += "MARKERS ";
		}
		if ((flag & IResourceDelta.MOVED_FROM) != 0) {
			flags += "MOVED_FROM ";
		}
		if ((flag & IResourceDelta.MOVED_TO) != 0) {
			flags += "MOVED_TO ";
		}
		if ((flag & IResourceDelta.NO_CHANGE) != 0) {
			flags += "NO_CHANGE ";
		}
		if ((flag & IResourceDelta.OPEN) != 0) {
			flags += "OPEN ";
		}
		if ((flag & IResourceDelta.REMOVED) != 0) {
			flags += "REMOVED ";
		}
		if ((flag & IResourceDelta.REMOVED_PHANTOM) != 0) {
			flags += "REMOVED_PHANTOM ";
		}
		if ((flag & IResourceDelta.REPLACED) != 0) {
			flags += "REPLACED ";
		}
		if ((flag & IResourceDelta.SYNC) != 0) {
			flags += "SYNC ";
		}
		if ((flag & IResourceDelta.TYPE) != 0) {
			flags += "TYPE ";
		}
		return flags;
	}
}
