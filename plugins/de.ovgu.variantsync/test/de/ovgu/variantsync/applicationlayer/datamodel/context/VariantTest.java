package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class VariantTest {

	private static final String NAME = "elementName";
	private static final String PATH = "PathToElement";

	private Variant variant;
	private List<CodeLine> cls;

	@Before
	public void before() {
		variant = new Variant();

		cls = new ArrayList<CodeLine>();
		cls.add(new CodeLine("", 0));
	}

	@Test@Ignore
	public void testSetCodeLines() throws Exception {
		Assert.assertFalse(variant.setCodeLines(null));
		Assert.assertFalse(variant.setCodeLines(new ArrayList<CodeLine>()));
		Assert.assertFalse(variant.setCodeLines(cls));
	}

	@Test@Ignore
	public void testIsEmpty() throws Exception {
		Assert.assertTrue(variant.isEmpty());

		variant.addChild(new Package());
		Assert.assertTrue(variant.isEmpty());

		Class c = new Class();
		c.addCode(cls);
		Package p = new Package(NAME, PATH, c);
		variant.addChild(p);
		Assert.assertFalse(variant.isEmpty());

		variant = new Variant();
		variant.addChild(null);
		Assert.assertTrue(variant.isEmpty());
	}

	@Test@Ignore
	public void testGetConcreteType() throws Exception {
		Assert.assertTrue(variant.getConcreteType(NAME, PATH) instanceof Variant);
	}

	@Test@Ignore
	public void testToString() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testVariant() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testVariantStringStringElement() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testVariantStringString() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testGetProject() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testSetProject() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testElement() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testGetClonedCodeLines() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testAddChild() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testRemoveChild() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testHasNoChildren() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testGetName() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testSetName() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testGetPath() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testSetPath() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testGetType() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testGetChildren() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testSetChildren() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	@Test@Ignore
	public void testClone() throws Exception {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

}
