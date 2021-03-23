package de.variantsync.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

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
	int lineIndex = 0; // only for printTree() testing
	final int INITIAL_AST_SIZE = 11;
	final int INITIAL_TOSTRING_ROWS = 10;

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
	public void toStringOnInitialTest() {
		final String[] lines = root.toString().split(" ");
		assertEquals(INITIAL_TOSTRING_ROWS, lines.length);
		// this loop skips the names of the variable and only checks their value
		for (int i = 2; i < lines.length; i = i + 2) {
			if (lines[i].charAt(lines[i].length() - 1) == ',') {
				lines[i] = lines[i].substring(0, lines[i].length() - 1);
			}
			switch (i) {
			case 2:
				assertEquals(root.getId().getMostSignificantBits(), Long.parseLong(lines[i]));
				break;
			case 4:
				assertEquals(root.getType().toString(), lines[i]);
				break;
			case 6:
				assertEquals(root.getValue().toString(), lines[i]);
				break;
			case 8:
				assertEquals(root.getSubtrees().size(), Integer.parseInt(lines[i]));
				break;
			default:
				throw new IllegalArgumentException(String.format("toStringOnInitialTest has incorrect lines size: %d", i));
			}
		}
	}

	@Test
	public void addOnInitialTest() {
		AST<LineGrammar, String> newTree = new AST<>(LineGrammar.Directory, "newROOT");
		int oldSize = root.getSubtrees().size();
		assertTrue(root.addChild(newTree));
		assertEquals(oldSize + 1, root.getSubtrees().size());

		newTree = new AST<>(LineGrammar.Line, "!LineYouAreLookingFor");
		oldSize = root.getSubtrees().size();
		assertFalse(root.addChild(newTree));
		assertEquals(oldSize, root.getSubtrees().size());
	}

	@Test
	public void addAllOnInitialTest() {
		List<AST<LineGrammar, String>> newTrees = Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), new AST<>(LineGrammar.BinaryFile, "101010"),
				new AST<>(LineGrammar.TextFile, "fancyFile.txt"), new AST<>(LineGrammar.Directory, "fancyFolder"));
		int oldSize = root.getSubtrees().size();
		assertFalse(root.addChildren(newTrees));
		assertEquals(oldSize + 3, root.getSubtrees().size());

		newTrees = Arrays.asList(new AST<>(LineGrammar.Directory, "public class Main {"), new AST<>(LineGrammar.BinaryFile, "101010"));
		oldSize = root.getSubtrees().size();
		assertTrue(root.addChildren(newTrees));
		assertEquals(oldSize + 2, root.getSubtrees().size());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void addOnSubtreesList() {
		root.getSubtrees().add(new AST<>(LineGrammar.Directory, "evilDir"));
	}

	@Test
	public void sizeOnInitialTest() {
		assertEquals(INITIAL_AST_SIZE, root.size());
	}

	@Test
	public void sizeOnEmptyASTTest() {
		root = new AST<>(null, null);
		assertEquals(1, root.size());
	}

	@Test
	public void getDepthOnInitialTest() {
		assertEquals(3, root.getDepth());
	}

	@Test
	public void getDepthOnEmptyASTTest() {
		root = new AST<>(null, null);
		assertEquals(0, root.getDepth());
	}

	@Test
	public void printTreeOnInitialTest() {
		final String[] lines = root.printTree().split(String.format("%n"));
		assertEquals(INITIAL_AST_SIZE, lines.length);
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
		for (final AST<LineGrammar, String> child : node.getSubtrees()) {
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
