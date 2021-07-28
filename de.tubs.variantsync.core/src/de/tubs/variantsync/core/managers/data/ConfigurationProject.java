package de.tubs.variantsync.core.managers.data;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.io.EclipseFileSystem;
import de.ovgu.featureide.fm.core.io.manager.ConfigurationManager;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.AManager;
import de.tubs.variantsync.core.managers.FeatureContextManager;
import de.tubs.variantsync.core.managers.ISaveableManager;
import de.tubs.variantsync.core.managers.PatchesManager;
import de.tubs.variantsync.core.managers.persistence.FeatureContextFormat;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;
import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.JsonParserASTWithLineGrammar;
import de.variantsync.core.ast.LineBasedParser;
import de.variantsync.core.ast.LineGrammar;

/**
 * A class for managing all informations about the product line for one configuration project
 *
 * @author Christopher Sontag
 * @since 1.1
 */
public class ConfigurationProject extends AManager implements ISaveableManager {

	private IFeatureProject configurationProject = null;

	private final FeatureContextManager featureContextManager = new FeatureContextManager(this);
	private final PatchesManager patchesManager = new PatchesManager(this);
	
	private HashMap<IProject, AST<LineGrammar,String>> projects = new HashMap<>();

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

	public IFeatureProject getFeatureProject() {
		if ((configurationProject != null) && configurationProject.getProject().exists()) {
			return configurationProject;
		}

		return null;
	}

	public void setFeatureProject(IFeatureProject configurationProject) {
		this.configurationProject = configurationProject;
		fireEvent(new VariantSyncEvent(this, EventType.CONFIGURATIONPROJECT_SET, null, configurationProject));
	}

	public List<String> getVariantNames() {
		final List<String> projectNames = new ArrayList<>();
		for (final IProject project : projects.keySet()) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}

	public Set<IProject> getVariants() {
		return projects.keySet();
	}

	public IProject getVariant(String name) {
		for (final IProject project : projects.keySet()) {
			if (project.getName().equals(name)) {
				return project;
			}
		}
		return null;
	}


	public void addVariant(IProject project) {
		
				
		String pathS = project.getLocation().toOSString();
		
		Path path = Paths.get(pathS);
		
		LogOperations.logRefactor("[addVariatn] "+ path.toString());
		
		AST<LineGrammar, String> srcDir = null;
		LineBasedParser parser = new LineBasedParser();
		try {
			srcDir = parser.parseDirectory(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogOperations.logRefactor("[addVariatn] "+ e.getMessage());
		}
		
		projects.put(project,srcDir);
		
	}

	public Configuration getConfigurationForVariant(IProject project) {
		if (project != null) {
			for (final Path confPath : configurationProject.getAllConfigurations()) {
				final IFile configPath = (IFile) EclipseFileSystem.getResource(confPath);
				final String configFileName = configPath.getName();
				final String configName = configFileName.substring(0, configFileName.lastIndexOf('.'));
				System.out.println(String.format("[ConfigurationProject.getConfigurationForVariant] Check name equality Project(%s) with Config(%s)",
						project.getName(), configName));
				if (configName.equals(project.getName())) {
					final ConfigurationManager configurationManager = ConfigurationManager.getInstance(Paths.get(configPath.getRawLocationURI()));
					if (configurationManager != null) {
						return configurationManager.getObject();
					}
				}
			}
		}
		return null;
	}

	public Iterable<IFeature> getFeatures() {
		return configurationProject.getFeatureModel().getFeatures();
	}

	@Override
	public void reset() {
		projects.clear();
	}

	public FeatureContextManager getFeatureContextManager() {
		return featureContextManager;
	}

	public AST<LineGrammar,String> getAST(IProject project) {
		return projects.get(project);
	}

	public PatchesManager getPatchesManager() {
		return patchesManager;
	}
	
	public void saveProjects() {
		
		for (final Entry<IProject, AST<LineGrammar, String>> entry : projects.entrySet()) {
			String ProjectPath = getFeatureProject().getProject().getLocation().toOSString();
			
			String path = ProjectPath+File.separator+entry.getKey().getName().toString();
			
			LogOperations.logRefactor("[save] " + path);
			
			try {
				JsonParserASTWithLineGrammar.exportAST(Paths.get(path), entry.getValue());
			} catch (IOException e) {
				LogOperations.logError("[saveProjects] could not export Project", e);
			}
		}
	
	}
	
	
	public void loadProjects() {
		
		for (final Entry<IProject, AST<LineGrammar, String>> entry : projects.entrySet()) {
			String ProjectPath = getFeatureProject().getProject().getLocation().toOSString();
			
			//linux oder windows?
			String path = ProjectPath+File.separator+entry.getKey().getName().toString();
			
			LogOperations.logRefactor("[load] " + path);
			
			AST<LineGrammar, String> importedAST = null;
			try {
				importedAST = JsonParserASTWithLineGrammar.importAST(Paths.get(path));
			} catch (IOException e) {
				LogOperations.logError("[loadProjects] could not import Project" + e.getMessage(), e);
			}
			 
			//TODO: Paul implement
			//ASTdiffer(entry.getValue, importedAST), getMarkers from imported, get new Lines from Worspace AST (entry.getValue()) 
			
			//AST do net get updated if this gets executed
//			if(importedAST != null)
//				entry.setValue(importedAST);	 
		}
		
	}

	@Override
	public void save() {
		
		LogOperations.logRefactor("[save] "+ projects.entrySet().size());
		
		featureContextManager.save();
		patchesManager.save();
		saveProjects();
		
		
	}

	@Override
	public void load() {
		LogOperations.logRefactor("[load] ");

		featureContextManager.load();
		patchesManager.load();
		loadProjects();
		

	}

}
