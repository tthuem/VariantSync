package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Package;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;

public class TestVariant {

	@Test
	public void testIsEmpty() {

		// variant is empty
		Element variant = new Variant();
		Assert.assertTrue(variant.isEmpty());

		// variant contains a class with code lines inside a package
		List<CodeLine> lines = new ArrayList<CodeLine>();
		lines.add(new CodeLine("test", 0));
		Element javaClass = new Class();
		javaClass.setCodeLines(lines);

		Element packageElement = new Package("test", "test", javaClass);
		List<Element> packages = new ArrayList<Element>();
		packages.add(packageElement);

		variant = new Variant("test", "test", "test", packages);

		Assert.assertFalse(variant.isEmpty());
	}
}
