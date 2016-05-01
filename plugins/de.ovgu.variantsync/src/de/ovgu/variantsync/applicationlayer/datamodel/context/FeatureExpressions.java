package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FeatureExpressions")
public class FeatureExpressions {

	private Collection<String> featureExpressions;

	public FeatureExpressions() {
		featureExpressions = new HashSet<String>();
	}

	public FeatureExpressions(Collection<String> featureExpressions) {
		this.featureExpressions = featureExpressions;
	}

	public void addFeatureExpression(String fe) {
		featureExpressions.add(fe);
	}

	public void addFeatureExpressions(Collection<String> collection) {
		featureExpressions.addAll(collection);
	}

	public void removeFeatureExpression(String fe) {
		featureExpressions.remove(fe);
	}

	public void clearFeatureExpressions() {
		featureExpressions.clear();
	}

	public Iterator<String> iterator() {
		return featureExpressions.iterator();
	}

	/**
	 * @return the featureExpressions
	 */
	public Collection<String> getFeatureExpressions() {
		return featureExpressions;
	}

	public Set<String> getFeatureExpressionsAsSet() {
		return new HashSet<String>(featureExpressions);
	}

	/**
	 * @param featureExpressions
	 *            the featureExpressions to set
	 */
	public void setFeatureExpressions(Collection<String> featureExpressions) {
		this.featureExpressions = featureExpressions;
	}

}