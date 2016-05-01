package de.ovgu.variantsync.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;

/**
 * Basis for all tests (except AddCodeToEmptyContext-Test) is the adding of the
 * following code:
 * 
 * 5 private int a; 7 public Main(int g) { 8 a = g; 9 } 10 11 public int getA()
 * { 12 return a; 13 } 14 15 public void setA(int a) { 16 this.a = a; 17 }
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 06.09.2015
 */
public class TestSingleContextMapping {

	private final String FEATURE_EXPRESSION = "Test";
	private final String PROJECT_NAME = "TestProject";
	private final String PROJECT_PATH = "arbitraryPathToProject";
	private Context context;
	private IContextOperations co = ModuleFactory.getContextOperations();

	@Before
	public void before() {
		context = new Context(FEATURE_EXPRESSION);
		context.initProject(PROJECT_NAME, PROJECT_PATH);
	}

	@Test
	public void testAddCodeToEmptyContext() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeToEmptyContext()\n");
		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

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

		System.out.println("JavaProject:\n" + jp.toString());
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
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
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
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

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

	@Test
	public void testAddCodeInsideExistingCode_Simple() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeInsideExistingCode_Simple()\n");

		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
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
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

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

	@Test
	public void testAddCodeInsideExistingCode_Comprehensive1() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeInsideExistingCode_Comprehensive1()\n");

		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -4,2 +4,5 @@, -	, -	, +, +	public Main(int g) {, +		System.out.println(\"This is a test.\");, +	}, +"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
		System.out.println("JavaProject:\n" + jp.toString());

		// add code line inside existing code
		diffArray = "--- Main.java, +++ Main.java, @@ -5,1 +5,5 @@, -	public Main(int g) {, +	private boolean isFalse;, +	private int i = 0;, +, +	public Main(int g, boolean isfalse) {, +		isFalse = true;, @@ -7,0 +11,1 @@, +		this.isFalse = isfalse;, @@ -8,0 +13,5 @@, +	, +	private int helpMe(int i) {, +		i = i * 10;, +		return -1;, +	}, @@ -9,0 +19,9 @@, +	public void setA(int a) {, +		helpMe(4);, +	}, +, +	public void test() {, +		System.out.println(\"This is not a test!\");, +		System.out.println(\"This is not a test, too!\");, +	}, +"
				.split(", ");
		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		diff.set(7, "+	public Main(int g, boolean isfalse) {");
		diff.remove(8);
		diff.set(24, "+		System.out.println(\"This is not a test, too!\");");
		diff.remove(25);
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		CodeLine cl = codeOfClass.get(0);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 4);
		cl = codeOfClass.get(1);
		assertEquals(cl.getCode(), "private boolean isFalse;");
		assertEquals(cl.getLine(), 5);
		cl = codeOfClass.get(2);
		assertEquals(cl.getCode(), "private int i = 0;");
		assertEquals(cl.getLine(), 6);
		cl = codeOfClass.get(3);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 7);
		cl = codeOfClass.get(4);
		assertEquals(cl.getCode(), "public Main(int g, boolean isfalse) {");
		assertEquals(cl.getLine(), 8);
		cl = codeOfClass.get(5);
		assertEquals(cl.getCode(), "isFalse = true;");
		assertEquals(cl.getLine(), 9);
		cl = codeOfClass.get(6);
		assertEquals(cl.getCode(), "System.out.println(\"This is a test.\");");
		assertEquals(cl.getLine(), 10);
		cl = codeOfClass.get(7);
		assertEquals(cl.getCode(), "this.isFalse = isfalse;");
		assertEquals(cl.getLine(), 11);
		cl = codeOfClass.get(8);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 12);
		cl = codeOfClass.get(9);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 13);
		cl = codeOfClass.get(10);
		assertEquals(cl.getCode(), "private int helpMe(int i) {");
		assertEquals(cl.getLine(), 14);
		cl = codeOfClass.get(11);
		assertEquals(cl.getCode(), "i = i * 10;");
		assertEquals(cl.getLine(), 15);
		cl = codeOfClass.get(12);
		assertEquals(cl.getCode(), "return -1;");
		assertEquals(cl.getLine(), 16);
		cl = codeOfClass.get(13);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 17);
		cl = codeOfClass.get(14);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 18);
		cl = codeOfClass.get(15);
		assertEquals(cl.getCode(), "public void setA(int a) {");
		assertEquals(cl.getLine(), 19);
		cl = codeOfClass.get(16);
		assertEquals(cl.getCode(), "helpMe(4);");
		assertEquals(cl.getLine(), 20);
		cl = codeOfClass.get(17);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 21);
		cl = codeOfClass.get(18);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 22);
		cl = codeOfClass.get(19);
		assertEquals(cl.getCode(), "public void test() {");
		assertEquals(cl.getLine(), 23);
		cl = codeOfClass.get(20);
		assertEquals(cl.getCode(),
				"System.out.println(\"This is not a test!\");");
		assertEquals(cl.getLine(), 24);
		cl = codeOfClass.get(21);
		assertEquals(cl.getCode(),
				"System.out.println(\"This is not a test, too!\");");
		assertEquals(cl.getLine(), 25);
		cl = codeOfClass.get(22);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), 26);
		cl = codeOfClass.get(23);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), 27);

		System.out.println("JavaProject:\n" + jp.toString());
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
	}

	@Test
	public void testAddCodeInsideExistingCode_Comprehensive2() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeInsideExistingCode_Comprehensive2()\n");

		List<String> diff = new ArrayList<String>();

		// add code to empty context
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,1 +5,3 @@, -, +	public Main(int g) {, +		System.out.println(\"This is a test.\");, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
		System.out.println("JavaProject:\n" + jp.toString());

		// add code line inside existing code
		diffArray = "--- Main.java, +++ Main.java, @@ -5,1 +5,5 @@, -	public Main(int g) {, +	private boolean isFalse;, +	private int i = 0;, +	, +	public Main(int g, boolean isFalse) {, +		isFalse = true;, @@ -7,0 +11,1 @@, +		this.isFalse = isFalse;, @@ -8,0 +13,14 @@, +	, +	private int helpMe(int i) {, +		i = i * 10;, +		return -1;, +	}, +	, +	public void setA(int a) {, +		helpMe(4);, +	}, +	, +	public void test() {, +		System.out.println(\"This is not a test!\");, +		System.out.println(\"This is not a test, too!\");, +	}"
				.split(", ");

		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		diff.set(7, "+	public Main(int g, boolean isFalse) {");
		diff.remove(8);
		diff.set(24, "+		System.out.println(\"This is not a test, too!\");");
		diff.remove(25);
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		int i = 0;
		int line = 5;
		CodeLine cl = codeOfClass.get(i);
		assertEquals(cl.getCode(), "private boolean isFalse;");
		assertEquals(cl.getLine(), line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "private int i = 0;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public Main(int g, boolean isFalse) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "isFalse = true;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println(\"This is a test.\");");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.isFalse = isFalse;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "private int helpMe(int i) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "i = i * 10;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "return -1;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public void setA(int a) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "helpMe(4);");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public void test() {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(),
				"System.out.println(\"This is not a test!\");");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(),
				"System.out.println(\"This is not a test, too!\");");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "}");
		assertEquals(cl.getLine(), ++line);

		System.out.println("JavaProject:\n" + jp.toString());
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
	}

	/**
	 * see TestSingleContextMapping.txt
	 */
	@Test
	public void testAddCodeInsideExistingCode_Comprehensive3() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testAddCodeInsideExistingCode_Comprehensive3()\n");

		List<String> diff = new ArrayList<String>();

		// Step 0
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,1 +5,7 @@, -	, +	private int i;, +, +	public Main(int j) {, +		this.i = j;, +		this.i = j + 3;, +		System.out.println();, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
		System.out.println("JavaProject:\n" + jp.toString());

		// Step 1
		diffArray = "--- Main.java, +++ Main.java, @@ -11,0 +11,1 @@, +		System.out.println();"
				.split(", ");

		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		int i = 0;
		int line = 5;
		CodeLine cl = codeOfClass.get(i);
		assertEquals(cl.getCode(), "private int i;");
		assertEquals(cl.getLine(), line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public Main(int j) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j + 3;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);

		System.out.println("JavaProject:\n" + jp.toString());

		// Step 2
		diffArray = "--- Main.java, +++ Main.java, @@ -8,1 +8,1 @@, -		this.i = j;, +		"
				.split(", ");

		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		codeOfClass = jp.getChildren().get(0).getChildren().get(0)
				.getClonedCodeLines();

		i = 0;
		line = 5;
		cl = codeOfClass.get(i);
		assertEquals(cl.getCode(), "private int i;");
		assertEquals(cl.getLine(), line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public Main(int j) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j + 3;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);

		System.out.println("JavaProject:\n" + jp.toString());

		// Step 3
		diffArray = "--- Main.java, +++ Main.java, @@ -8,1 +8,1 @@, -		, +		this.i = j;"
				.split(", ");

		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		codeOfClass = jp.getChildren().get(0).getChildren().get(0)
				.getClonedCodeLines();

		i = 0;
		line = 5;
		cl = codeOfClass.get(i);
		assertEquals(cl.getCode(), "private int i;");
		assertEquals(cl.getLine(), line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public Main(int j) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j + 3;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);

		System.out.println("JavaProject:\n" + jp.toString());

		// Step 4
		diffArray = "--- Main.java, +++ Main.java, @@ -11,1 +11,0 @@, -		System.out.println();"
				.split(", ");

		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		codeOfClass = jp.getChildren().get(0).getChildren().get(0)
				.getClonedCodeLines();

		i = 0;
		line = 5;
		cl = codeOfClass.get(i);
		assertEquals(cl.getCode(), "private int i;");
		assertEquals(cl.getLine(), line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "public Main(int j) {");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "this.i = j + 3;");
		assertEquals(cl.getLine(), ++line);
		cl = codeOfClass.get(++i);
		assertEquals(cl.getCode(), "System.out.println();");
		assertEquals(cl.getLine(), ++line);

		System.out.println("JavaProject:\n" + jp.toString());
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
	}

	@Test
	public void testRemoveCode() {
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("testRemoveCode()\n");

		List<String> diff = new ArrayList<String>();
		String[] diffArray = "--- Main.java, +++ Main.java, @@ -5,0 +5,1 @@, +	private int a;, @@ -6,0 +7,11 @@, +	public Main(int g) {, +		a = g;, +	}, +, +	public int getA() {, +		return a;, +	}, +, +	public void setA(int a) {, +		this.a = a;, +	}"
				.split(", ");
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		JavaProject jp = context.getJavaProject(PROJECT_NAME);
		System.out.println("JavaProject:\n" + jp.toString());
		List<CodeLine> codeOfClass = jp.getChildren().get(0).getChildren()
				.get(0).getClonedCodeLines();

		// remove code from the inside of existing code
		diffArray = "--- Main.java, +++ Main.java, @@ -11,4 +11,0 @@, -	public int getA() {, -		return a;, -	}, -"
				.split(", ");
		diff.clear();
		for (String s : diffArray) {
			diff.add(s);
		}
		System.out.println("\nDiff-String:\n" + diff.toString());
		co.addCode(PROJECT_NAME, "mainpackage", "Main.java", diff, context,
				new ArrayList<String>());

		codeOfClass = jp.getChildren().get(0).getChildren().get(0)
				.getClonedCodeLines();

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

		System.out.println("JavaProject:\n" + jp.toString());
		System.out
				.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n\n\n");
	}
}
