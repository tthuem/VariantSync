package de.tubs.variantsync.core.managers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.SourceFile;
import de.tubs.variantsync.core.managers.persistence.CodeMappingFormat;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

public class MappingManager extends AManager implements ISaveableManager {

	private HashMap<IProject, List<SourceFile>> codeMappings = new HashMap<>();
	private ConfigurationProject configurationProject;

	public MappingManager(ConfigurationProject configurationProject) {
		this.configurationProject = configurationProject;
	}

	private boolean isActive;

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean status) {
		this.isActive = status;
		if (isActive) {
			fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_RECORDING_START, null,
					VariantSyncPlugin.getConfigurationProjectManager().getActiveConfigurationProject()));
		} else {
			fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_RECORDING_STOP,
					VariantSyncPlugin.getConfigurationProjectManager().getActiveConfigurationProject(), null));
		}
	}

	public SourceFile getMapping(IFile file) {
		if (file != null && codeMappings.containsKey(file.getProject())) {
			for (SourceFile sourceFile : codeMappings.get(file.getProject())) {
				if (sourceFile.getFile().getFullPath().equals(file.getFullPath())) {
					return sourceFile;
				}
			}
		}
		return null;
	}

	public HashMap<IProject, List<SourceFile>> getCodeMappings() {
		return codeMappings;
	}

	public void setCodeMappings(HashMap<IProject, List<SourceFile>> codeMappings) {
		this.codeMappings = codeMappings;
	}

	public void addCodeMapping(IProject project, List<SourceFile> files) {
		this.codeMappings.put(project, files);
	}

	public void addCodeMapping(IFile file, SourceFile sourceFile) {
		List<SourceFile> sourceFiles = codeMappings.get(file.getProject());
		if (sourceFiles == null || sourceFiles.isEmpty()) {
			sourceFiles = new ArrayList<>();
			addCodeMapping(file.getProject(), sourceFiles);
		}
		for (SourceFile sf : sourceFiles) {
			if (sf.getFile().getFullPath().equals(file.getFullPath())) {
				sf = sourceFile;
				return;
			}
		}
		sourceFiles.add(sourceFile);
	}

	@Override
	public void reset() {
		codeMappings.clear();
	}

	@Override
	public void load() {
		for (IProject project : configurationProject.getVariants()) {
			List<SourceFile> sourceFiles = new ArrayList<>();
			FileHandler.load(Paths.get(project.getFile(CodeMappingFormat.FILENAME).getLocationURI()), sourceFiles, new CodeMappingFormat(project));
			if (!sourceFiles.isEmpty()) {
				addCodeMapping(project, sourceFiles);
			}
		}
	}

	@Override
	public void save() {
		for (IProject project : codeMappings.keySet()) {
			FileHandler.save(Paths.get(project.getFile(CodeMappingFormat.FILENAME).getLocationURI()), codeMappings.get(project),
					new CodeMappingFormat(project));
		}
	}

}
