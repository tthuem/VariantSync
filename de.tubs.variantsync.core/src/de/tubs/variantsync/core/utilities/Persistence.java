package de.tubs.variantsync.core.utilities;

import java.io.File;

import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
 * Loads and saves objects
 * @author Christopher Sontag (c.sontag@tu-bs.de)
 * @since 1.0.0.0
 */
public class Persistence {
	
	/**
	 * Loads all feature expressions
	 * @param filename
	 * @return
	 */
//	public static FeatureExpressions loadFeatureExpression(String filename) {
//		try {
//			return (FeatureExpressions) loadObject(
//					JAXBContext.newInstance(FeatureExpressions.class), filename);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * Saves all feature expressions
//	 * @param context
//	 * @param filename
//	 */
//	public static void writeFeatureExpression(FeatureExpressions fe,
//			String filename) {
//		try {
//			writeObject(JAXBContext.newInstance(FeatureExpressions.class), fe,
//					filename);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Loads objects
	 * @param context
	 * @param filename
	 * @return
	 */
	private static Object loadObject(JAXBContext context, String filename) {
		try {
			Unmarshaller un = context.createUnmarshaller();
			return un.unmarshal(new File(filename));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Save objects
	 * @param c
	 * @param o
	 * @param filename
	 */
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
