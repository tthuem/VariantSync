package de.ovgu.variantsync.persistencelayer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.ovgu.variantsync.applicationlayer.datamodel.exception.XMLException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorItemStorage;

/**
 * Provides operations to read and write xml files.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 16.05.2015
 */
class XMLOperations {

	/**
	 * Reads a xml file and creates a SynchroInfo object.
	 * 
	 * @param inputStream
	 * @return SynchroInfo object
	 */
	public MonitorItemStorage readSynchroXMLFile(InputStream inputStream) {
		MonitorItemStorage info = null;
		XMLDecoder decoder = new XMLDecoder(inputStream);
		info = (MonitorItemStorage) decoder.readObject();
		decoder.close();
		return info;
	}

	/**
	 * Writes SynchroInfo object in xml file.
	 * 
	 * @param file
	 *            target file
	 * @param info
	 *            SynchroInfo object
	 * @throws XMLException
	 *             target does not exists
	 */
	public void writeXMLFile(File file, MonitorItemStorage info)
			throws XMLException {
		XMLEncoder encoder = null;
		try {
			encoder = new XMLEncoder(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new XMLException(
					"XML-based synchro info file to write could not be found. Does target file exists?",
					e);
		}
		encoder.writeObject(info);
		encoder.close();
	}

}
