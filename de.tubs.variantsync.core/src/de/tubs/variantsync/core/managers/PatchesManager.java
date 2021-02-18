package de.tubs.variantsync.core.managers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.ovgu.featureide.fm.core.io.manager.SimpleFileHandler;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.persistence.PatchFormat;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

public class PatchesManager extends AManager implements ISaveableManager, IEventListener {

	private IPatch<?> actualPatch = null;

	private List<IPatch<?>> patches = new ArrayList<IPatch<?>>();

	private final ConfigurationProject configurationProject;

	public PatchesManager(ConfigurationProject configurationProject) {
		VariantSyncPlugin.getDefault().addListener(this);
		this.configurationProject = configurationProject;
	}

	public IPatch<?> getActualContextPatch() {
		if (actualPatch == null) {
			return null;
		}
		return actualPatch;
	}

	public void setActualContextPatch(IPatch<?> patch) {
		actualPatch = patch;
	}

	public List<IPatch<?>> getPatches() {
		return patches;
	}

	public void addPatch(IPatch<?> patch) {
		patches.add(patch);
	}

	public void setPatches(List<IPatch<?>> patches) {
		this.patches = patches;
	}

	public void closeActualPatch() {
		if (actualPatch != null) {
			actualPatch.setEndTime(System.currentTimeMillis());
			if (!actualPatch.isEmpty() && !patches.contains(actualPatch)) {
				patches.add(actualPatch);
			}
			actualPatch = null;
			fireEvent(new VariantSyncEvent(this, EventType.PATCH_CLOSED, null, null));
		}
	}

	@Override
	public void reset() {
		patches.clear();
	}

	@Override
	public void load() {
		if (configurationProject.getFeatureProject() != null) {
			final List<IPatch<?>> patches = new ArrayList<>();
			SimpleFileHandler.load(Paths.get(configurationProject.getFeatureProject().getProject().getFile(PatchFormat.FILENAME).getLocationURI()), patches,
					new PatchFormat(configurationProject.getFeatureProject()));
			if (!patches.isEmpty()) {
				setPatches(patches);
			}
		}
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONTEXT_CHANGED:
		case CONTEXT_RECORDING_STOP:
			closeActualPatch();
			break;
		default:
			break;
		}
	}

	@Override
	public void save() {
		SimpleFileHandler.save(Paths.get(configurationProject.getFeatureProject().getProject().getFile(PatchFormat.FILENAME).getLocationURI()), patches,
				new PatchFormat(configurationProject.getFeatureProject()));
	}

}
