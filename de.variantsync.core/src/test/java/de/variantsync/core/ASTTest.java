package de.variantsync.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineGrammar;

/**
 * Here you can find the unit tests for the AST data structure.
 *
 * @author eric
 */
public class ASTTest {

	AST<LineGrammar, String> root;
	AST<LineGrammar, String> mainDir;
	AST<LineGrammar, String> testDir;
	AST<LineGrammar, String> mainJava;
	int lineIndex = 0; // only for toString testing

	@Before
	public void setup() {
		root = new AST<>(LineGrammar.Directory, "src");
		mainDir = new AST<>(LineGrammar.Directory, "main");
		testDir = new AST<>(LineGrammar.Directory, "test");
		mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
		final AST<LineGrammar, String> emptyJava = new AST<>(LineGrammar.TextFile, "Empty.java");
		final AST<LineGrammar, String> emptyTestJava = new AST<>(LineGrammar.TextFile, "EmptyTest.java");
		root.addChild(testDir);
		testDir.addChild(emptyTestJava);
		root.addChild(mainDir);
		mainDir.addChild(mainJava);
		mainDir.addChild(emptyJava);

		mainJava.addChildren(
				Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
						new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"), new AST<>(LineGrammar.Line, "    }"),
						new AST<>(LineGrammar.Line, "}")));
	}

	@Test
	public void sizeOnInitialTest() {
		assertEquals(11, root.size());
		root = new AST<>(null, null);
		assertEquals(1, root.size());
	}

	@Test
	public void sizeOnEmptyASTTest() {
		root = new AST<>(null, null);
		assertEquals(1, root.size());
	}

	@Test
	public void getMaxDepthOnInitialTest() {
		assertEquals(3, root.getMaxDepth());
	}

	@Test
	public void getMaxDepthOnEmptyASTTest() {
		root = new AST<>(null, null);
		assertEquals(0, root.getMaxDepth());
	}

	@Test
	public void toStringOnInitialTest() {
		final String[] lines = root.toString().split(String.format("%n"));
		assertEquals(11, lines.length);
		// test root values
		final String[] rootAttributes = lines[0].split(" ");
		assertEquals(4, rootAttributes.length);

		// check root values
		checkStringRoot(root, rootAttributes);

		// check subtrees
		checkStringSubTree(root, lines);
	}

	private void checkStringRoot(AST<LineGrammar, String> node, String[] rootAttributes) {
		assertEquals(0, lineIndex);
		// in general checking like this is not possible due to the nature of AST.INDENT_STRING
		// but root line does not contain AST.INDENT_STRING
		assertEquals(node.getType().toString(), rootAttributes[0]); // Type
		assertEquals(node.getValue(), rootAttributes[1]); // Value
		assertEquals("uuid:", rootAttributes[2]);
		assertEquals(((Long) node.getId().getMostSignificantBits()).toString(), rootAttributes[3]); // UUID
	}

	private void checkStringSubTree(AST<LineGrammar, String> node, String[] lines) {
		lineIndex++;
		for (final AST<LineGrammar, String> child : node.getSubtree()) {
			final String line = lines[lineIndex];
			assertTrue(line.contains(AST.INDENT_STRING));
			assertTrue(line.contains(AST.NEXT_SEPARATOR) || line.contains(AST.NEXT_ACT_SEPARATOR) || line.contains(AST.LAST_SEPARATOR));
			assertTrue(line.contains(child.getType().toString()));
			assertTrue(line.contains(child.getValue()));
			assertTrue(line.contains(((Long) child.getId().getMostSignificantBits()).toString()));

			checkStringSubTree(child, lines);
		}
	}
}
