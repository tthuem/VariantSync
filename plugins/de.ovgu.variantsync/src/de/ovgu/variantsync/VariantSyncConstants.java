package de.ovgu.variantsync;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Describe file names and paths for feature handling. E.g. path to config
 * files, name of feature model file, ...
 * 
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.05.2015
 */
public final class VariantSyncConstants {

	public static final String PLUGIN_ID = "de.ovgu.variantsync";
	public static final String ADMIN_FOLDER = ".variantsync";
	public static final String ADMIN_FILE = ".variantsyncInfo";
	public static final String CONTEXT_PATH = "/variantsyncFeatureInfo/context";
	private static final String MERGE_PATH = "/variantsyncFeatureInfo/merge";
	public static final String FEATURE_EXPRESSION_PATH = "/variantsyncFeatureInfo/featureExpression/FeatureExpressions.xml";
	public static final String DEFAULT_CONTEXT = "Default_Context";
	public static final String MERGE_OUTPUT = ResourcesPlugin.getWorkspace()
			.getRoot().getLocation().toString()
			+ VariantSyncConstants.MERGE_PATH + "\\Merge.java";
	public static final String MERGE_FOLDER = ResourcesPlugin.getWorkspace()
			.getRoot().getLocation().toString()
			+ VariantSyncConstants.MERGE_PATH;

	private VariantSyncConstants() {
	}

}
