package de.ovgu.variantsync.ui.controller.data;

import java.util.ArrayList;
import java.util.List;

public class MappingElement {

	private String feature;
	private String title;
	private JavaElements type;
	private String pathToSelectedElement;
	private List<String> code;
	private int startLineOfSelection;
	private int endLineOfSelection;
	private int offset;
	private String pathToProject;
	private List<String> wholeClass;
	private boolean isFirstStep;
	private boolean isLastStep;
	private boolean ignore;
	private long modificationTime;

	public MappingElement(String feature, String title, JavaElements type, String pathToSelectedElement,
			String pathToProject) {
		this.feature = feature;
		this.title = title;
		this.type = type;
		this.pathToSelectedElement = pathToSelectedElement;
		this.pathToProject = pathToProject;
	}

	public MappingElement(String feature, String title, JavaElements type, String pathToSelectedElement,
			List<String> code, int startLineOfSelection, int endLineOfSelection, int offset, List<String> wholeClass,
			boolean isFirstStep, boolean isLastStep, boolean ignore, long modificationTime) {
		this.feature = feature;
		this.title = title;
		this.type = type;
		this.pathToSelectedElement = pathToSelectedElement;
		this.code = code;
		this.startLineOfSelection = startLineOfSelection;
		this.endLineOfSelection = endLineOfSelection;
		this.offset = offset;
		this.wholeClass = wholeClass;
		this.isFirstStep = isFirstStep;
		this.isLastStep = isLastStep;
		this.ignore = ignore;
		this.modificationTime = modificationTime;
	}

	public MappingElement(String feature, String title, JavaElements type, String pathToSelectedElement, String code,
			int startLineOfSelection, int endLineOfSelection, int offset) {
		this.feature = feature;
		this.title = title;
		this.type = type;
		this.pathToSelectedElement = pathToSelectedElement;
		String[] codeLines = code.split("\n");
		this.code = new ArrayList<String>();
		for (String line : codeLines) {
			this.code.add(line);
		}
		this.startLineOfSelection = startLineOfSelection;
		this.endLineOfSelection = endLineOfSelection;
		this.offset = offset;
	}

	/**
	 * @return the feature
	 */
	public String getFeature() {
		return feature;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public long getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(long modificationTime) {
		this.modificationTime = modificationTime;
	}

	/**
	 * @return the type
	 */
	public JavaElements getType() {
		return type;
	}

	/**
	 * @return the pathToSelectedElement
	 */
	public String getPathToSelectedElement() {
		return pathToSelectedElement;
	}

	/**
	 * @return the code
	 */
	public List<String> getCode() {
		return code;
	}

	/**
	 * @return the startLineOfSelection
	 */
	public int getStartLineOfSelection() {
		return startLineOfSelection;
	}

	/**
	 * @return the endLineOfSelection
	 */
	public int getEndLineOfSelection() {
		return endLineOfSelection;
	}

	/**
	 * @return the pathToProject
	 */
	public String getPathToProject() {
		return pathToProject;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		return "MappingElement [feature=" + feature + ", title=" + title + ", type=" + type + ", pathToSelectedElement="
				+ pathToSelectedElement + ", code=" + code + ", startLineOfSelection=" + startLineOfSelection
				+ ", endLineOfSelection=" + endLineOfSelection + ", offset=" + offset + ", pathToProject="
				+ pathToProject + ", wholeClass=" + wholeClass + ", isFirstStep=" + isFirstStep + ", isLastStep="
				+ isLastStep + ", ignore=" + ignore + ", modificationTime=" + modificationTime + "]";
	}

	/**
	 * @param pathToProject
	 *            the pathToProject to set
	 */
	public void setPathToProject(String pathToProject) {
		this.pathToProject = pathToProject;
	}

	/**
	 * @return the wholeClass
	 */
	public List<String> getWholeClass() {
		return wholeClass;
	}

	/**
	 * @return the isLastStep
	 */
	public boolean isLastStep() {
		return isLastStep;
	}

	/**
	 * @param isLastStep
	 *            the isLastStep to set
	 */
	public void setLastStep(boolean isLastStep) {
		this.isLastStep = isLastStep;
	}

	/**
	 * @return the isFirstStep
	 */
	public boolean isFirstStep() {
		return isFirstStep;
	}

	/**
	 * @param isFirstStep
	 *            the isFirstStep to set
	 */
	public void setFirstStep(boolean isFirstStep) {
		this.isFirstStep = isFirstStep;
	}

	/**
	 * @return the ignore
	 */
	public boolean isIgnore() {
		return ignore;
	}

}
