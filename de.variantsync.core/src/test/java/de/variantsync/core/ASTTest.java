package de.variantsync.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineGrammar;

//TODO. Add compare method to AST and then compare ASTs with this method.

public class ASTTest {

	AST<LineGrammar, String> ast;
	AST<LineGrammar, String> mainJava;

	@Before
	public void setup() {
		ast = new AST<>(LineGrammar.Directory, "src");
		final AST<LineGrammar, String> mainDir = new AST<>(LineGrammar.Directory, "main");
		final AST<LineGrammar, String> testDir = new AST<>(LineGrammar.Directory, "test");
		mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
		final AST<LineGrammar, String> emptyJava = new AST<>(LineGrammar.TextFile, "Empty.java");
		final AST<LineGrammar, String> emptyTestJava = new AST<>(LineGrammar.TextFile, "EmptyTest.java");
		ast.addChild(testDir);
		testDir.addChild(emptyTestJava);
		ast.addChild(mainDir);
		mainDir.addChild(mainJava);
		mainDir.addChild(emptyJava);

		mainJava.addChildren(
				Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
						new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"), new AST<>(LineGrammar.Line, "    }"),
						new AST<>(LineGrammar.Line, "}")));
	}

	@Test
	public void sizeTest() {
		assertEquals(11, ast.size());
		ast = new AST<>(null, null);
		assertEquals(1, ast.size());
	}

	@Test
	public void toStringTest() {
		final String expected = "Directory src Depth: 0\n" + "    ├─Directory test Depth: 1\n" + "    │     └─ TextFile EmptyTest.java Depth: 2\n"
			+ "    └─Directory main Depth: 1\n" + "          ├─ TextFile Main.java Depth: 2\n"
			+ "          │      ├─ Line public class Main { Depth: 3\n"
			+ "          │      ├─ Line     public static void main(String[] args) Depth: 3\n"
			+ "          │      ├─ Line         System.out.println(\"Hello World\"); Depth: 3\n" + "          │      ├─ Line     } Depth: 3\n"
			+ "          │      └─ Line } Depth: 3\n" + "          └─ TextFile Empty.java Depth: 2\n";
		assertEquals(expected, ast.toString());
	}

}
