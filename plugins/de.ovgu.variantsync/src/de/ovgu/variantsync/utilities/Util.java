package de.ovgu.variantsync.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;

/**
 *
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 16.09.2015
 */
public class Util {

	public static void getClassesByClassName(List<Element> elements, List<Class> classes, String className) {
		if (elements != null && !elements.isEmpty()) {
			for (Element je : elements) {
				if (je instanceof Class && je.getName().equals(className)) {
					classes.add((Class) je);
				} else {
					getClassesByClassName(je.getChildren(), classes, className);
				}
			}
		}
	}

	public static boolean containsClass(List<Element> elements, String className) {
		if (elements != null && !elements.isEmpty()) {
			for (Element je : elements) {
				if (je instanceof Class && je.getName().equals(className)) {
					return true;
				} else {
					return containsClass(je.getChildren(), className);
				}
			}
		}
		return false;
	}

	public static String parseStorageLocation(Context c) {
		String storageLocation = VariantSyncPlugin.getWorkspaceLocation() + VariantSyncConstants.CONTEXT_PATH;
		String filename = "/" + c.getFeatureExpression() + ".xml";

		// creates target folder if it does not already exist
		File folder = new File(storageLocation);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return storageLocation += filename;
	}

	@SuppressWarnings("serial")
	public static Collection<String> getConfiguredFeatures(String projectName) {
		List<IProject> projects = VariantSyncPlugin.getDefault().getSupportProjectList();
		for (IProject p : projects) {
			if (p.getName().equals(projectName)) {
				Collection<String> features = new HashSet<String>();
				Iterator<IFeature> it = ModuleFactory.getFeatureOperations().getConfiguredFeaturesOfProject(p)
						.iterator();
				while (it.hasNext()) {
					IFeature f = it.next();
					features.add(f.getName());
				}
				return features;
			}
		}
		return new HashSet<String>() {
		};
	}

	public static List<String> getFileLines(IResource res) {
		List<String> currentFilelines = null;
		IFile currentFile = (IFile) res;
		try {
			currentFilelines = ModuleFactory.getPersistanceOperations().readFile(currentFile.getContents(),
					currentFile.getCharset());
		} catch (CoreException | FileOperationException e) {
			LogOperations.logError("File states could not be read.", e);
		}
		return currentFilelines;
	}

	public static String parsePackageNameFromResource(IResource res) {
		String packageName = res.getLocation().toString();
		try {
			packageName = packageName.substring(packageName.indexOf("src") + 4, packageName.lastIndexOf("/"));
			packageName = packageName.replace("/", ".");
		} catch (IndexOutOfBoundsException e) {
			return "defaultpackage";
		}
		return packageName;
	}

	public static List<String> parseCodeLinesToString(List<CodeLine> codelines) {
		List<String> list = new ArrayList<String>();
		for (CodeLine cl : codelines) {
			list.add(cl.getCode());
		}
		return list;
	}
}
