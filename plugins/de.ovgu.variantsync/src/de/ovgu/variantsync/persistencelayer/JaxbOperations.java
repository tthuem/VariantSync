package de.ovgu.variantsync.persistencelayer;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 12.09.2015
 */
class JaxbOperations {

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static Context loadContext(String filename) {
		try {
			Context c = (Context) loadObject(
					JAXBContext.newInstance(Context.class), filename);
			return c;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @param filename
	 */
	public static void writeContext(Context context, String filename) {
		try {
			writeObject(JAXBContext.newInstance(Context.class), context,
					filename);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static FeatureExpressions loadFeatureExpression(String filename) {
		try {
			return (FeatureExpressions) loadObject(
					JAXBContext.newInstance(FeatureExpressions.class), filename);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @param filename
	 */
	public static void writeFeatureExpression(FeatureExpressions fe,
			String filename) {
		try {
			writeObject(JAXBContext.newInstance(FeatureExpressions.class), fe,
					filename);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static Object loadObject(JAXBContext context, String filename) {
		try {
			Unmarshaller un = context.createUnmarshaller();
			return un.unmarshal(new File(filename));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void writeObject(JAXBContext c, Object o, String filename) {
		try {
			Marshaller m = c.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(o, new File(filename));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
