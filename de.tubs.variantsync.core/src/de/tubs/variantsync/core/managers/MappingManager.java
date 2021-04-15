package de.tubs.variantsync.core.managers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineGrammar;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.io.manager.SimpleFileHandler;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.SourceFile;
import de.tubs.variantsync.core.managers.persistence.CodeMappingFormat;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;


//TODO: REMOVE THIS FILE SINCE NO FILE DEPENDS ON THIS MANAGER ANYMORE!
public class MappingManager extends AManager implements ISaveableManager {

	private HashMap<IProject, AST<LineGrammar,String>> codeMappings = new HashMap<>();
	private final ConfigurationProject configurationProject;

	public MappingManager(ConfigurationProject configurationProject) {
		this.configurationProject = configurationProject;
	}

	private boolean isActive;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean status) {
		isActive = status;
		if (isActive) {
			fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_RECORDING_START, null,
					VariantSyncPlugin.getConfigurationProjectManager().getActiveConfigurationProject()));
		} else {
			fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_RECORDING_STOP,
					VariantSyncPlugin.getConfigurationProjectManager().getActiveConfigurationProject(), null));
		}
	}
	//return AST which belongs to this File NOT LINE
	public AST<LineGrammar,String> getMapping(IFile file) {
		if ((file != null) && codeMappings.containsKey(file.getProject())) {
			//TODO: AST REFACTORING
/*
			AST<LineGrammar,String> findings = codeMappings.get(file.getProject());
			for (final AST<LineGrammar, String> sourceFile :findings.toListPreorder()) {
				if (sourceFile.getFile().getFullPath().equals(file.getFullPath())) {
					return sourceFile;
				}
			}

 */
		}
		return null;
	}

	@Override
	public void reset() {
		codeMappings.clear();
	}

	@Override
	public void load() {
		//TODO: AST REFACTORING
/*
		for (final IProject project : configurationProject.getVariants()) {
			final List<SourceFile> sourceFiles = new ArrayList<>();
			SimpleFileHandler.load(Paths.get(project.getFile(CodeMappingFormat.FILENAME).getLocationURI()), sourceFiles, new CodeMappingFormat(project));
			if (!sourceFiles.isEmpty()) {
				addCodeMapping(project, sourceFiles);
			}
		}

 */
	}

	@Override
	public void save() {
		//TODO: AST REFACTORING
/*
		for (final IProject project : codeMappings.keySet()) {
			SimpleFileHandler.save(Paths.get(project.getFile(CodeMappingFormat.FILENAME).getLocationURI()), codeMappings.get(project),
					new CodeMappingFormat(project));
		}

 */
	}

	//-------------------------------unused methods, maybe delete?
/*
	public HashMap<IProject, AST<LineGrammar,String>> getCodeMappings() {
		return codeMappings;
	}

	public void setCodeMappings(HashMap<IProject, AST<LineGrammar,String>> codeMappings) {
		this.codeMappings = codeMappings;
	}

	public void addCodeMapping(IProject project, AST<LineGrammar,String> files) {
		codeMappings.put(project, files);
	}

	public void addCodeMapping(IFile file, SourceFile sourceFile) {
		List<SourceFile> sourceFiles = codeMappings.get(file.getProject());
		if ((sourceFiles == null) || sourceFiles.isEmpty()) {
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
*/
}
