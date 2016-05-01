package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 16.10.2015
 */
public class CodeChange {

	private List<CodeLine> baseVersion;
	private List<CodeLine> baseVersionWholeClass;
	private List<CodeLine> newVersion;
	private List<CodeLine> newVersionWholeClass;
	private long timestamp;

	public CodeChange() {
		baseVersion = new ArrayList<CodeLine>();
		newVersion = new ArrayList<CodeLine>();
		baseVersionWholeClass = new ArrayList<CodeLine>();
		newVersionWholeClass = new ArrayList<CodeLine>();
	}

	public void createTimeStamp() {
		this.timestamp = new Timestamp(new Date().getTime()).getTime();
	}

	/**
	 * @return the baseVersion
	 */
	@XmlElement
	public List<CodeLine> getBaseVersion() {
		return baseVersion;
	}

	/**
	 * @param baseVersion
	 *            the baseVersion to set
	 */
	public void setBaseVersion(List<CodeLine> baseVersion) {
		this.baseVersion = baseVersion;
	}

	/**
	 * @return the newVersion
	 */
	@XmlElement
	public List<CodeLine> getNewVersion() {
		return newVersion;
	}

	/**
	 * @param newVersion
	 *            the newVersion to set
	 */
	public void setNewVersion(List<CodeLine> newVersion) {
		this.newVersion = newVersion;
	}

	/**
	 * @return the timestamp
	 */
	@XmlElement
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CodeChange [baseVersion=" + baseVersion
				+ ", baseVersionWholeClass=" + baseVersionWholeClass
				+ ", newVersion=" + newVersion + ", newVersionWholeClass="
				+ newVersionWholeClass + ", timestamp=" + timestamp + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected CodeChange clone() {
		CodeChange copy = new CodeChange();
		copy.setTimestamp(this.timestamp);
		List<CodeLine> baseVersion = new ArrayList<CodeLine>();
		for (CodeLine line : this.baseVersion) {
			baseVersion.add(line.clone());
		}
		copy.setBaseVersion(baseVersion);
		List<CodeLine> newVersion = new ArrayList<CodeLine>();
		for (CodeLine line : this.newVersion) {
			newVersion.add(line.clone());
		}
		copy.setNewVersion(newVersion);
		List<CodeLine> baseVersionWholeClass = new ArrayList<CodeLine>();
		for (CodeLine line : this.baseVersionWholeClass) {
			baseVersionWholeClass.add(line.clone());
		}
		copy.setBaseVersionWholeClass(baseVersionWholeClass);
		List<CodeLine> newVersionWholeClass = new ArrayList<CodeLine>();
		for (CodeLine line : this.newVersionWholeClass) {
			newVersionWholeClass.add(line.clone());
		}
		copy.setNewVersionWholeClass(newVersionWholeClass);
		return copy;
	}

	/**
	 * @return the baseVersionWholeClass
	 */
	@XmlElement
	public List<CodeLine> getBaseVersionWholeClass() {
		return baseVersionWholeClass;
	}

	/**
	 * @param baseVersionWholeClass
	 *            the baseVersionWholeClass to set
	 */
	public void setBaseVersionWholeClass(List<CodeLine> baseVersionWholeClass) {
		this.baseVersionWholeClass = baseVersionWholeClass;
	}

	/**
	 * @return the newVersionWholeClass
	 */
	@XmlElement
	public List<CodeLine> getNewVersionWholeClass() {
		return newVersionWholeClass;
	}

	/**
	 * @param newVersionWholeClass
	 *            the newVersionWholeClass to set
	 */
	public void setNewVersionWholeClass(List<CodeLine> newVersionWholeClass) {
		this.newVersionWholeClass = newVersionWholeClass;
	}

}
