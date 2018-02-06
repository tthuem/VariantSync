package de.tubs.variantsync.core.managers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException.Type;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.FeatureContext;
import de.tubs.variantsync.core.managers.persistence.FeatureContextFormat;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

public class FeatureContextManager extends AManager implements ISaveableManager {

	public static final String DEFAULT_CONTEXT_NAME = "Default";

	private String actualContext = DEFAULT_CONTEXT_NAME;

	private List<FeatureContext> featureContexts = new ArrayList<>();

	private ConfigurationProject configurationProject;

	public FeatureContextManager(ConfigurationProject configurationProject) {
		this.configurationProject = configurationProject;
	}

	public boolean isDefault() {
		return getActual().equals(DEFAULT_CONTEXT_NAME);
	}

	public String getActual() {
		return actualContext;
	}

	public void setActual(String actualContext) {
		String oldContext = this.actualContext;
		this.actualContext = actualContext;
		fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_CHANGED, oldContext, actualContext));
	}

	public void setDefault() {
		String oldContext = this.actualContext;
		this.actualContext = DEFAULT_CONTEXT_NAME;
		fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_CHANGED, oldContext, actualContext));
	}

	public List<FeatureContext> getContexts() {
		return featureContexts;
	}

	public List<String> getContextsAsStrings() {
		List<String> contexts = new ArrayList<>();
		for (FeatureContext fe : this.featureContexts) {
			contexts.add(fe.name);
		}
		return contexts;
	}

	public void setContext(List<FeatureContext> contexts) {
		this.featureContexts = contexts;
		fireEvent(new VariantSyncEvent(this, EventType.FEATURECONTEXT_CHANGED, null, contexts));
	}

	public void addContext(String context) {
		FeatureContext fe = new FeatureContext(context, FeatureColor.Yellow);
		this.featureContexts.add(fe);
		fireEvent(new VariantSyncEvent(this, EventType.FEATURECONTEXT_ADDED, null, fe));
	}

	public void addContext(String context, FeatureColor color) {
		FeatureContext fe = new FeatureContext(context, color);
		this.featureContexts.add(fe);
		fireEvent(new VariantSyncEvent(this, EventType.FEATURECONTEXT_ADDED, null, fe));
	}

	public void importFeaturesFromModel() throws ProjectNotFoundException {
		if (configurationProject != null) {
			for (IFeature feature : configurationProject.getFeatures()) {
				FeatureContext fe = new FeatureContext(feature.getName());
				if (!featureContexts.contains(fe)) {
					featureContexts.add(fe);
					fireEvent(new VariantSyncEvent(this, EventType.FEATURECONTEXT_ADDED, null, fe));
				}
			}
		} else throw new ProjectNotFoundException(Type.CONFIGURATION);
	}

	public FeatureContext getContext(String name) {
		for (FeatureContext fe : getContexts()) {
			if (fe.name.equals(name)) {
				return fe;
			}
		}
		return null;
	}

	@Override
	public void reset() {
		featureContexts.clear();
	}

	@Override
	public void load() {
		List<FeatureContext> contexts = new ArrayList<>();
		FileHandler.load(Paths.get(configurationProject.getFeatureProject().getProject().getFile(FeatureContextFormat.FILENAME).getLocationURI()), contexts,
				new FeatureContextFormat());
		if (contexts.isEmpty()) {
			try {
				importFeaturesFromModel();
				LogOperations.logInfo("Loaded feature expressions from feature model");
			} catch (ProjectNotFoundException e) {
				LogOperations.logError("Cant read feature model", e);
			}
		} else {
			setContext(contexts);
		}
	}

	@Override
	public void save() {
		FileHandler.save(Paths.get(configurationProject.getFeatureProject().getProject().getFile(FeatureContextFormat.FILENAME).getLocationURI()),
				getContexts(), new FeatureContextFormat());
	}

}
