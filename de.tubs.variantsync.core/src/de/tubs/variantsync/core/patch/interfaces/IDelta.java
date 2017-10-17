package de.tubs.variantsync.core.patch.interfaces;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * 
 * 
 * @author Christopher Sontag
 * @version 1.0
 * @since 18.08.2017
 * @param <T> file element, e.g. line, ast element, ...
 */
public interface IDelta<T> extends Serializable {

	/**
	 * Specifies the type of the delta.
	 *
	 */
	public enum DELTATYPE {
		/** An addition was made */
		ADDED,
		/** An deletion was made */
		REMOVED,
		/** A change was made */
		CHANGED
	}

	/**
	 * Returns original delta
	 * 
	 * @return T - original delta
	 */
	T getOriginal();

	/**
	 * Returns original as string. Do not use this method outside of saving or loading!
	 * 
	 * @return
	 */
	String getOriginalAsString();

	/**
	 * Sets orginal delta
	 * 
	 * @param original - original delta
	 */
	void setOriginal(T original);

	/**
	 * Sets original from string. Do not use this method outside of saving or loading!
	 * 
	 * @param original
	 */
	void setOriginalFromString(String original);

	/**
	 * Returns revised delta
	 * 
	 * @return T - revised delta
	 */
	T getRevised();

	/**
	 * Returns revised as string. Do not use this method outside of saving or loading!
	 * 
	 * @return
	 */
	String getRevisedAsString();

	/**
	 * Sets revised delta
	 * 
	 * @param revised - revised delta
	 */
	void setRevised(T revised);

	/**
	 * Sets revised from string. Do not use this method outside of saving or loading!
	 * 
	 * @param revised
	 */
	void setRevisedFromString(String revised);

	/**
	 * Returns delta type
	 * 
	 * @return TYPE - type
	 */
	DELTATYPE getType();

	/**
	 * Sets type of delta
	 * 
	 * @param type - type
	 */
	void setType(DELTATYPE type);

	/**
	 * Returns feature
	 * 
	 * @return FeatureExpression - feature
	 */
	String getFeature();

	/**
	 * Sets feature expression of delta
	 * 
	 * @param feature - FeatureExpression
	 */
	void setFeature(String feature);

	/**
	 * Returns resource
	 */
	IFile getResource();

	IPatch<?> getPatch();

	void setPatch(IPatch<?> patch);

	IProject getProject();

	void setProject(IProject project);

	String getFactoryId();

	/**
	 * 
	 * @return
	 */
	boolean isSynchronizedProject(IProject project);

	boolean isSynchronizedProject(String projectName);

	void addSynchronizedProject(IProject project);

	List<IProject> getSynchronizedProjects();

	void setSynchronizedProjects(List<IProject> projects);

	/**
	 * 
	 * @return
	 */
	long getTimestamp();

	void setTimestamp(long timestamp);

	String getProperty(String key);

	HashMap<String, String> getProperties();

	void addProperty(String key, String obj);

	String getRepresentation();

	static DELTATYPE DELTATYPE(int i) {
		switch (i) {
		case 1:
			return DELTATYPE.ADDED;
		case 2:
			return DELTATYPE.REMOVED;
		case 4:
			return DELTATYPE.CHANGED;
		default:
			return null;
		}
	}
}
