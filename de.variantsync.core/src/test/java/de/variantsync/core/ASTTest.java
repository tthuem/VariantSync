package de.variantsync.core;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineGrammar;
import org.junit.Before;
import org.junit.Test;

public class ASTTest {

	AST<LineGrammar, String> ast;
	AST<LineGrammar, String> mainJava;

	@Before public void setup() {
		ast = new AST<>(LineGrammar.Directory, "src");
		AST<LineGrammar, String> mainDir = new AST<>(LineGrammar.Directory, "main");
		AST<LineGrammar, String> testDir = new AST<>(LineGrammar.Directory, "test");
		mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
		AST<LineGrammar, String> emptyJava = new AST<>(LineGrammar.TextFile, "Empty.java");
		AST<LineGrammar, String> emptyTetJava = new AST<>(LineGrammar.TextFile, "EmptyTest.java");
		ast.addChild(testDir);
		testDir.addChild(emptyTetJava);
		ast.addChild(mainDir);
		mainDir.addChild(mainJava);
		mainDir.addChild(emptyJava);

		mainJava.addChildren(
				Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
						new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"), new AST<>(LineGrammar.Line, "    }"),
						new AST<>(LineGrammar.Line, "}")));
	}

	@Test public void sizeTest() {
		assertEquals(11, ast.size());
		ast = new AST<>(null, null);
		assertEquals(1, ast.size());
	}

	@Test public void toStringTest() {
		String expected = "Directory src Depth: 0\n" + "    ├─Directory test Depth: 1\n" + "    │     └─ TextFile EmptyTest.java Depth: 2\n"
				+ "    └─Directory main Depth: 1\n" + "          ├─ TextFile Main.java Depth: 2\n" + "          │      ├─ Line public class Main { Depth: 3\n"
				+ "          │      ├─ Line     public static void main(String[] args) Depth: 3\n"
				+ "          │      ├─ Line         System.out.println(\"Hello World\"); Depth: 3\n" + "          │      ├─ Line     } Depth: 3\n"
				+ "          │      └─ Line } Depth: 3\n" + "          └─ TextFile Empty.java Depth: 2\n";
		assertEquals(expected, ast.toString());
	}

}
