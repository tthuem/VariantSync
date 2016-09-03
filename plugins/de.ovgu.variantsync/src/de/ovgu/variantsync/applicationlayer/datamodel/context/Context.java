package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.ovgu.variantsync.VariantSyncConstants;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
@XmlRootElement(name = "Context")
public class Context {

	private String featureExpression;
	private Map<String, Variant> variants;
	private CodeHighlighting color;
	private ChangeLogMap changeLog;

	public Context() {
		this.variants = new HashMap<String, Variant>();
		this.color = CodeHighlighting.YELLOW;
		this.changeLog = new ChangeLogMap();
	}

	public Context(String featureExpression) {
		this.featureExpression = featureExpression;
		this.variants = new HashMap<String, Variant>();
		if (featureExpression.equals(VariantSyncConstants.DEFAULT_CONTEXT)) {
			this.color = CodeHighlighting.DEFAULTCONTEXT;
		} else {
			this.color = CodeHighlighting.YELLOW;
		}
		this.changeLog = new ChangeLogMap();
	}

	public void initProject(String projectName, String pathToProject) {
		variants.put(projectName, new Variant(projectName,
				pathToProject));
	}

	public boolean containsProject(String projectName) {
		return variants.containsKey(projectName);
	}

	/**
	 * @return the featureExpression
	 */
	public String getFeatureExpression() {
		return featureExpression;
	}

	/**
	 * @param featureExpression
	 *            the featureExpression to set
	 */
	public void setFeatureExpression(String featureExpression) {
		this.featureExpression = featureExpression;
	}

	/**
	 * @return the javaProject
	 */
	public Variant getJavaProject(String projectName) {
		return variants.get(projectName);
	}

	/**
	 * @param javaProject
	 *            the javaProject to set
	 */
	public void setJavaProject(Variant javaProject) {
		this.variants.put(javaProject.getName(), javaProject);
	}

	public String getPathToProject(String projectName) {
		if (variants == null || variants.get(projectName) == null)
			return null;
		return variants.get(projectName).getPath();
	}

	public void setPathToProject(String projectName, String path) {
		if (variants.get(projectName) == null) {
			variants.put(projectName, new Variant(projectName, path));
			return;
		}
		variants.get(projectName).setPath(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Context [featureExpression=" + featureExpression
				+ ", javaProject=" + variants + ", color=" + color + "]";
	}

	/**
	 * @return the color
	 */
	public CodeHighlighting getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(CodeHighlighting color) {
		this.color = color;
	}

	/**
	 * @return the javaProjects
	 */
	@XmlElement(name = "color")
	public Map<String, Variant> getJavaProjects() {
		return variants;
	}

	/**
	 * @param javaProjects
	 *            the javaProjects to set
	 */
	public void setJavaProjects(Map<String, Variant> javaProjects) {
		this.variants = javaProjects;
	}

	public boolean isSynchronized(long key, String source, String target) {
		return changeLog.contains(key, source, target);
	}

	public void addSynchronizedChange(long key, String source, String target) {
		changeLog.put(key, source, target);
	}

	public void removeSynchronizedChange(long key) {
		changeLog.removeChange(key);
	}

	public ChangeLogMap getChangeLog() {
		return changeLog;
	}

	/**
	 * @param changeLog
	 *            the changeLog to set
	 */
	public void setChangeLog(ChangeLogMap changeLog) {
		this.changeLog = changeLog;
	}
}
