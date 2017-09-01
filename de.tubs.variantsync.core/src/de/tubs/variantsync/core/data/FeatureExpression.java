package de.tubs.variantsync.core.data;

import de.ovgu.featureide.fm.core.color.FeatureColor;

public class FeatureExpression {
	
	public String name;
	public FeatureColor highlighter;
	
	public FeatureExpression() {
		this.name = "";
		this.highlighter = FeatureColor.NO_COLOR;
	}
	
	public FeatureExpression(String name) {
		super();
		this.name = name;
		this.highlighter = FeatureColor.Yellow;
	}

	public FeatureExpression(String name, FeatureColor highlighter) {
		super();
		this.name = name;
		this.highlighter = highlighter;
	}

	public boolean isComposed() {
		return name.matches(".*(or|and|not).*");
	}
	
	
	
}
