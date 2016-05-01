package de.ovgu.variantsync.applicationlayer.datamodel.context;

import javax.xml.bind.annotation.XmlAttribute;

public class CodeLine {

	private String code;
	private int line;
	private boolean isMapped;
	private boolean isNew;

	public CodeLine() {
	}

	public CodeLine(String code, int line) {
		this.code = code;
		this.line = line;
		isMapped = true;
	}

	public CodeLine(String code, int line, boolean isMapped, boolean isNew) {
		this.code = code;
		this.line = line;
		this.isMapped = isMapped;
		this.isNew = isNew;
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
	@XmlAttribute
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
	@XmlAttribute
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
		return "CodeLine [code=" + code + ", line=" + line + ", isMapped="
				+ isMapped + ", isNew=" + isNew + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CodeLine clone() {
		return new CodeLine(code, line, isMapped, isNew);
	}

	/**
	 * @return the isMapped
	 */
	public boolean isMapped() {
		return isMapped;
	}

	/**
	 * @param isMapped
	 *            the isMapped to set
	 */
	public void setMapped(boolean isMapped) {
		this.isMapped = isMapped;
	}

	/**
	 * @return the isNew
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * @param isNew
	 *            the isNew to set
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
