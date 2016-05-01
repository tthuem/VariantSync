package de.ovgu.variantsync.applicationlayer.datamodel.resources;

/**
 * Describe types for resource changes. E.g. a resource (like a java file or a
 * folder) can be changed, removed oder added.
 * 
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
public final class ChangeTypes {

	public static final String CHANGE = "CHANGE";
	public static final String REMOVEFOLDER = "REMOVEFOLDER";
	public static final String REMOVEFILE = "REMOVEFILE";
	public static final String ADDFOLDER = "ADDFOLDER";
	public static final String ADDFILE = "ADDFILE";

	private ChangeTypes() {
	}
}