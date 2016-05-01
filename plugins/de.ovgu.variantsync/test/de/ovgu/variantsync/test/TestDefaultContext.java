package de.ovgu.variantsync.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;

/**
 * Test behavior and code tagging of default context. Run this test as JUnit
 * Plug-in test.
 * 
 * Basis for all tests (except AddCodeToEmptyContext-Test) is the adding of the
 * following code:
 * 
 * 5 private int a; 7 public Main(int g) { 8 a = g; 9 } 10 11 public int getA()
 * { 12 return a; 13 } 14 15 public void setA(int a) { 16 this.a = a; 17 }
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.09.2015
 */
public class TestDefaultContext {

	private final String PROJECT_NAME = "TestProject";
	private final String PROJECT_PATH = "arbitraryPathToProject";
	private final String PACKAGE_NAME = "mainpackage";
	private final String CLASS_NAME = "Main.java";
	private final String DEFAULT_CONTEXT = VariantSyncConstants.DEFAULT_CONTEXT;
	private IContextOperations co = ModuleFactory.getContextOperations();

	@Before
	public void before() {
		co.activateContext(DEFAULT_CONTEXT);
	}
	
	@After
	public void after() {
		co.deleteAllContexts();
	}

	@Test
	public void testAddCodeToEmptyDefaultContext() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeToEmptyDefaultContext()\n");

		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());

		co.recordCodeChange(diff, PROJECT_NAME, PROJECT_PATH, PACKAGE_NAME,
				CLASS_NAME, new ArrayList<String>());
		co.stopRecording();
		Context defaultContext = co.getContext(DEFAULT_CONTEXT);
		JavaProject jp = defaultContext.getJavaProject(PROJECT_NAME);

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
		assertEquals(cl.getCode(), "public int getA() {");
		assertEquals(cl.getLine(), 11);
		cl = codeOfClass.get(6);
		assertEquals(cl.getCode(), "return a;");
		assertEquals(cl.getLine(), 12);
		cl = codeOfClass.get(7);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 13);
		cl = codeOfClass.get(8);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 14);
		cl = codeOfClass.get(9);
		assertEquals(cl.getCode(), "public void setA(int a) {");
		assertEquals(cl.getLine(), 15);
		cl = codeOfClass.get(10);
		assertEquals(cl.getCode(), "this.a = a;");
		assertEquals(cl.getLine(), 16);
		cl = codeOfClass.get(11);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 17);

		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
	}

	@Test
	public void testAddCodeInsideExistingCode() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeInsideExistingCode()\n");

		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());

		co.recordCodeChange(diff, PROJECT_NAME, PROJECT_PATH, PACKAGE_NAME,
				CLASS_NAME, new ArrayList<String>());

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
		co.stopRecording();
		Context defaultContext = co
				.getContext(VariantSyncConstants.DEFAULT_CONTEXT);
		JavaProject jp = defaultContext.getJavaProject(PROJECT_NAME);

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		System.out.println("JavaProject:\n" + jp.toString());

		CodeLine cl = codeOfClass.get(0);
		assertEquals(cl.getCode(), "private int a;");
		assertEquals(cl.getLine(), 5);
		cl = codeOfClass.get(1);
		assertEquals(cl.getCode(), "private int b;");
		assertEquals(cl.getLine(), 6);
		cl = codeOfClass.get(2);
		assertEquals(cl.getCode(), "public Main(int g, int h) {");
		assertEquals(cl.getLine(), 8);
		cl = codeOfClass.get(3);
		assertEquals(cl.getCode(), "a = g;");
		assertEquals(cl.getLine(), 9);
		cl = codeOfClass.get(4);
		assertEquals(cl.getCode(), "b = h;");
		assertEquals(cl.getLine(), 10);
		cl = codeOfClass.get(5);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 11);
		cl = codeOfClass.get(6);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 12);
		cl = codeOfClass.get(7);
		assertEquals(cl.getCode(), "public int getA() {");
		assertEquals(cl.getLine(), 13);
		cl = codeOfClass.get(8);
		assertEquals(cl.getCode(), "return a;");
		assertEquals(cl.getLine(), 14);
		cl = codeOfClass.get(9);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 15);
		cl = codeOfClass.get(10);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 16);
		cl = codeOfClass.get(11);
		assertEquals(cl.getCode(), "public void setA(int a) {");
		assertEquals(cl.getLine(), 17);
		cl = codeOfClass.get(12);
		assertEquals(cl.getCode(), "this.a = a;");
		assertEquals(cl.getLine(), 18);
		cl = codeOfClass.get(13);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 19);

		System.out.println("JavaProject:\n" + jp.toString());
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
	}
}
