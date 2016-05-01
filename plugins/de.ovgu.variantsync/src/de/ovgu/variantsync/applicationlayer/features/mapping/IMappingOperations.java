package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;

public interface IMappingOperations {

	Element getElement(List<Element> javaPackages, String className,
			String classPath);

	Variant addMapping(Variant project, MappingElement mappingp);

	Element createElement(String pathToProject, String elementName,
			String pathToElement);

	boolean containsElement(List<Element> elements, String elementName,
			String pathToElement, String contentOfElement);

	void removeMapping(String elementName, String pathToElement,
			CodeFragment code, Element project, boolean isFirstStep,
			boolean isLastStep, List<String> wholeClass);
}