package de.ovgu.variantsync.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;

/**
 * Tests the update algorithm. This algorithms checks if the code inside
 * contexts that are not active was changed by the active contexts. In case of a
 * change, it adapts the code inside the inactive contexts. Run as JUnit Plug-in
 * test.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.09.2015
 */
public class TestUpdateCodeAlgorithm {

	private final String TESTFEATURE_CONTEXT = "TestFeature";
	private final String DEFAULT_CONTEXT = VariantSyncConstants.DEFAULT_CONTEXT;
	private final String PROJECT_NAME = "TestProject";
	private final String PROJECT_PATH = "arbitraryPathToProject";
	private final String PACKAGE_NAME = "mainpackage";
	private final String CLASS_NAME = "Main.java";
	private IContextOperations co = ModuleFactory.getContextOperations();

	@After
	public void after() {
		co.deleteAllContexts();
	}

	@Test
	public void testAddCodeUdate() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeUdate()\n");
		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());

		co.activateContext(TESTFEATURE_CONTEXT);
		co.recordCodeChange(diff, PROJECT_NAME, PROJECT_PATH, PACKAGE_NAME,
				CLASS_NAME, new ArrayList<String>());
		co.stopRecording();

		Context testFeatureContext = co.getContext(TESTFEATURE_CONTEXT);
		JavaProject jp = testFeatureContext.getJavaProject(PROJECT_NAME);
		System.out.println("JavaProject:\n" + jp.toString());

		// add code line inside existing code
		diffArray = "--- Main.java, +++ Main.java, @@ -6,0 +6,1 @@, +	private int b;, @@ -7,1 +8,1 @@, -	public Main(int g) {, +	public Main(int g, int h) {, @@ -9,0 +10,1 @@, +		b = h;"
				.split(", ");
		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		diff.set(6, "+	public Main(int g, int h) {");
		diff.remove(7);
		System.out.println("\nDiff-String:\n" + diff.toString());

		co.recordCodeChange(diff, PROJECT_NAME, PROJECT_PATH, PACKAGE_NAME,
				CLASS_NAME, new ArrayList<String>());

		testFeatureContext = co.getContext(TESTFEATURE_CONTEXT);
		jp = testFeatureContext.getJavaProject(PROJECT_NAME);

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		System.out.println("JavaProject:\n" + jp.toString());

		CodeLine cl = codeOfClass.get(0);
		assertEquals(cl.getCode(), "private int a;");
		assertEquals(cl.getLine(), 5);
		cl = codeOfClass.get(1);
		assertEquals(cl.getCode(), "a = g;");
		assertEquals(cl.getLine(), 9);
		cl = codeOfClass.get(2);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 11);
		cl = codeOfClass.get(3);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 12);
		cl = codeOfClass.get(4);
		assertEquals(cl.getCode(), "public int getA() {");
		assertEquals(cl.getLine(), 13);
		cl = codeOfClass.get(5);
		assertEquals(cl.getCode(), "return a;");
		assertEquals(cl.getLine(), 14);
		cl = codeOfClass.get(6);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 15);
		cl = codeOfClass.get(7);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 16);
		cl = codeOfClass.get(8);
		assertEquals(cl.getCode(), "public void setA(int a) {");
		assertEquals(cl.getLine(), 17);
		cl = codeOfClass.get(9);
		assertEquals(cl.getCode(), "this.a = a;");
		assertEquals(cl.getLine(), 18);
		cl = codeOfClass.get(10);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 19);

		Context defaultContext = co.getContext(DEFAULT_CONTEXT);
		jp = defaultContext.getJavaProject(PROJECT_NAME);

		System.out.println("JavaProject:\n" + jp.toString());

		codeOfClass = jp.getChildren().get(0).getChildren().get(0)
				.getClonedCodeLines();

		cl = codeOfClass.get(0);
		assertEquals(cl.getCode(), "private int b;");
		assertEquals(cl.getLine(), 6);
		cl = codeOfClass.get(1);
		assertEquals(cl.getCode(), "public Main(int g, int h) {");
		assertEquals(cl.getLine(), 8);
		cl = codeOfClass.get(2);
		assertEquals(cl.getCode(), "b = h;");
		assertEquals(cl.getLine(), 10);

		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
	}

	@Test
	public void testRemoveCodeUdate() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeUdate()\n");
		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());

		co.activateContext(TESTFEATURE_CONTEXT);
		co.recordCodeChange(diff, PROJECT_NAME, PROJECT_PATH, PACKAGE_NAME,
				CLASS_NAME, new ArrayList<String>());
		co.stopRecording();

		Context testFeatureContext = co.getContext(TESTFEATURE_CONTEXT);
		JavaProject jp = testFeatureContext.getJavaProject(PROJECT_NAME);
		System.out.println("JavaProject:\n" + jp.toString());

		// remove code from the inside of existing code
		diffArray = "--- Main.java, +++ Main.java, @@ -11,4 +11,0 @@, -	public int getA() {, -		return a;, -	}, -"
				.split(", ");
		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());

		co.recordCodeChange(diff, PROJECT_NAME, PROJECT_PATH, PACKAGE_NAME,
				CLASS_NAME, new ArrayList<String>());

		testFeatureContext = co.getContext(TESTFEATURE_CONTEXT);
		jp = testFeatureContext.getJavaProject(PROJECT_NAME);

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		System.out.println("JavaProject:\n" + jp.toString());

		CodeLine cl = codeOfClass.get(0);
		assertEquals(cl.getCode(), "private int a;");
		assertEquals(cl.getLine(), 5);
		cl = codeOfClass.get(1);
		assertEquals(cl.getCode(), "public Main(int g) {");
		assertEquals(cl.getLine(), 7);
		cl = codeOfClass.get(2);
		assertEquals(cl.getCode(), "a = g;");
		assertEquals(cl.getLine(), 8);
		cl = codeOfClass.get(3);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 9);
		cl = codeOfClass.get(4);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 10);
		cl = codeOfClass.get(5);
		assertEquals(cl.getCode(), "public void setA(int a) {");
		assertEquals(cl.getLine(), 11);
		cl = codeOfClass.get(6);
		assertEquals(cl.getCode(), "this.a = a;");
		assertEquals(cl.getLine(), 12);
		cl = codeOfClass.get(7);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 13);

		Context defaultContext = co.getContext(DEFAULT_CONTEXT);
		jp = defaultContext.getJavaProject(PROJECT_NAME);

		System.out.println("JavaProject:\n" + jp.toString());

		assertTrue(jp.getChildren() == null);

		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");

	}

}
