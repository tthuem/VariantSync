package de.ovgu.variantsync.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.ContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.io.Persistable;

public class TestXMLOutput {

	private static final String FEATURE_EXPRESSION = "Test";
	private static final String PROJECT_NAME = "TestProject";
	private static final String PROJECT_PATH = "arbitraryPathToProject";
	private static final String PATH_CONTEXT_STORAGE = System.getProperty("user.dir")
			+ "/test/variantsyncFeatureInfo/context/" + "TestFeature.xml";
	private Context context;
	private Persistable persistenceOp = ModuleFactory
			.getPersistanceOperations();
	private ContextOperations co = ModuleFactory.getContextOperations();

	@Before
	public void before() {
		context = new Context(FEATURE_EXPRESSION);
		context.initProject(PROJECT_NAME, PROJECT_PATH);

		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>(), 0);
	}

	@Test
	public void testWriteXML() {
		persistenceOp.saveContext(context, PATH_CONTEXT_STORAGE);
		File savedContext = new File(PATH_CONTEXT_STORAGE);
		assertTrue(savedContext.exists());
	}

	@Test
	public void testReadXML() {
		Context c = persistenceOp.loadContext(PATH_CONTEXT_STORAGE);
		Variant jp = c.getJavaProject(PROJECT_NAME);
		List<CodeLine> codeLines = jp.getClonedCodeLines();
		CodeLine cl = codeLines.get(0);
		assertEquals("private int a;", cl.getCode());
		assertEquals(5, cl.getLine());
		cl = codeLines.get(1);
		assertEquals("public Main(int g) {", cl.getCode());
		assertEquals(6, cl.getLine());
		cl = codeLines.get(6);
		assertEquals("return a;", cl.getCode());
		assertEquals(11, cl.getLine());
		cl = codeLines.get(9);
		assertEquals("public void setA(int a) {", cl.getCode());
		assertEquals(14, cl.getLine());
		cl = codeLines.get(10);
		assertEquals("this.a = a;", cl.getCode());
		assertEquals(15, cl.getLine());
		cl = codeLines.get(11);
		assertEquals("}", cl.getCode());
		assertEquals(16, cl.getLine());
		assertEquals(12, codeLines.size());

		Class jc = (Class) jp.getChildren().get(0).getChildren().get(0);
		List<CodeChange> changes = jc.getChanges();
		assertTrue(!changes.isEmpty());
	}
}
