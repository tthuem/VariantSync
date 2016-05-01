package de.ovgu.variantsync.applicationlayer.features;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.applicationlayer.features.mapping.ClassMapping;
import de.ovgu.variantsync.applicationlayer.features.mapping.CodeMapping;
import de.ovgu.variantsync.applicationlayer.features.mapping.IMappingOperations;
import de.ovgu.variantsync.applicationlayer.features.mapping.PackageMapping;
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * Provides functions to map code to features. Maps code to data structures of
 * package {@link de.ovgu.variantsync.applicationlayer.datamodel.features}.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 04.06.2015
 */
class FeatureMapping {
	// ConcurrentModificationException remove code fragment (x done)

	// TODO: evtl.: add Java-Elements constructor and global variable
	// TODO: Refactoring: split this class in smaller classes
	// TODO: Decorators
	// TODO: Handle Duplicates
	// TODO: test and documentation function overview
	// TODO: save data structure persistent
	// TODO: load data after eclipse start and set markers
	// TODO: set marker after class or package mapping
	// TODO: error: marking all code of class, add to feature. Then the code
	// cannot be removed...

	private static FeatureMapping instance;
	// private List<Feature> features;
	private IMappingOperations packageMapping;
	private IMappingOperations classMapping;
	private IMappingOperations codeMapping;

	private String elementName;
	private JavaElements elementType;
	private String elementPath;

	private FeatureMapping() {
		// features = new ArrayList<Feature>();
		packageMapping = new PackageMapping();
		classMapping = new ClassMapping();
		codeMapping = new CodeMapping();
	}

	public static FeatureMapping getInstance() {
		if (instance == null) {
			instance = new FeatureMapping();
		}
		return instance;
	}

	public JavaProject mapCodeFragment(MappingElement mapping,
			JavaProject project) {
		readMappingInfo(mapping);
		List<String> code = mapping.getCode();
		String projectPath = project.getPath();
		String projectName = project.getName();
		String relativeClassPath = UtilOperations.getInstance()
				.parseToRelativeElementPath(projectName, elementPath);
		switch (elementType) {
		case CODE_FRAGMENT: {

			// check
			if (project != null) {
				codeMapping.addMapping(project, mapping);

				// check
			} else {
				JavaProject javaProject = ((ClassMapping) classMapping)
						.addClassWithCode(projectPath, elementName,
								relativeClassPath, code,
								mapping.getStartLineOfSelection(),
								mapping.getEndLineOfSelection(),
								mapping.getOffset());
				try {
					return (JavaProject) javaProject.clone();
				} catch (CloneNotSupportedException e) {
					LogOperations
							.logError(
									"Clone-method in class JavaProject not yet implemented.",
									e);
				}
			}
		}
		default:
			break;
		}
		return project;
	}

	public JavaProject mapElement(MappingElement mapping, JavaProject project) {
		readMappingInfo(mapping);
		String projectPath = mapping.getPathToProject();
		if (project != null) {
			switch (elementType) {

			// check
			case PACKAGE: {
				packageMapping.addMapping(project, mapping);
				break;
			}

			// check
			case CLASS: {
				classMapping.addMapping(project, mapping);
				break;
			}
			case METHOD: {
				// not yet implemented
				break;
			}
			default:
				break;
			}
		} else {
			JavaElement javaProject = new JavaProject(UtilOperations
					.getInstance().parseProjectName(projectPath), projectPath,
					null);
			switch (elementType) {

			// check
			case PACKAGE: {
				javaProject.addChild(packageMapping.createElement(projectPath,
						elementName, elementPath));
				break;
			}

			// check
			case CLASS: {
				javaProject = (JavaProject) classMapping.createElement(
						projectPath, elementName, elementPath);
				break;
			}
			case METHOD: {
				// not yet implemented
				break;
			}
			default:
				break;
			}
			try {
				return (JavaProject) javaProject.clone();
			} catch (CloneNotSupportedException e) {
				LogOperations
						.logError(
								"Clone-method in class JavaProject not yet implemented.",
								e);
			}
		}
		return null;
	}

	public JavaProject removeMapping(MappingElement mapping, JavaProject project) {
		String pathToFile = UtilOperations.getInstance().unifyStartOfPath(
				mapping.getPathToSelectedElement());
		// pathToFile =
		// UtilOperations.getInstance().removeSrcInPath(pathToFile);
		String filename = mapping.getTitle();
		JavaElements elementType = mapping.getType();
		switch (elementType) {

		// check
		case PACKAGE: {
			packageMapping.removeMapping(filename, pathToFile, null, project,
					mapping.isFirstStep(), mapping.isLastStep(),
					mapping.getWholeClass());
			break;
		}

		// check
		case CLASS: {
			classMapping.removeMapping(filename, pathToFile, null, project,
					mapping.isFirstStep(), mapping.isLastStep(),
					mapping.getWholeClass());
			break;
		}

		// check
		case CODE_FRAGMENT: {
			codeMapping.removeMapping(filename, pathToFile, new CodeFragment(
					mapping.getCode(), mapping.getStartLineOfSelection(),
					mapping.getEndLineOfSelection(), mapping.getOffset()),
					project, mapping.isFirstStep(), mapping.isLastStep(),
					mapping.getWholeClass());
			break;
		}
		default:
			break;
		}
		return null;
	}

	// private boolean containsFeature(String feature) {
	// for (Feature listElement : features) {
	// if (feature.equals(listElement.getName())) {
	// return true;
	// }
	// }
	// return false;
	// }

	// private Feature getFeature(String feature) {
	// for (Feature listElement : features) {
	// if (feature.equals(listElement.getName())) {
	// return listElement;
	// }
	// }
	// return null;
	// }

	private void readMappingInfo(MappingElement mapping) {
		elementName = mapping.getTitle();
		elementType = mapping.getType();
		elementPath = mapping.getPathToSelectedElement();
	}
}
