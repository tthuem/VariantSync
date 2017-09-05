package de.tubs.variantsync.core.data;

import org.eclipse.core.resources.IResource;

public class CodeLine {

	private String code;
	private int line;
	private FeatureExpression featureExpression;

	public CodeLine() {
	}

	public CodeLine(String code, int line) {
		this.code = code;
		this.line = line;
	}

	public CodeLine(String code, int line, FeatureExpression featureExpression) {
		this.code = code;
		this.line = line;
		this.featureExpression = featureExpression;
	}

	/**
	 * @return the code
	 */

	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the line
	 */

	public int getLine() {
		return line;
	}

	/**
	 * @param line
	 *            the line to set
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CodeLine [code=" + code + ", line=" + line + ", featureExpression=" + featureExpression + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CodeLine clone() {
		return new CodeLine(code, line, featureExpression);
	}

	public FeatureExpression getFeatureExpression() {
		return featureExpression;
	}

	public void setFeatureExpression(FeatureExpression featureExpression) {
		this.featureExpression = featureExpression;
	}

}
