package de.variantsync.core;


import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineGrammar;
import org.prop4j.Literal;

import javax.sound.sampled.Line;

import static org.junit.Assert.*;

/**
 * Here you can find the unit tests for the AST data structure. This also tests the (Line)Grammar indirectly.
 *
 * @author eric
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // run tests in lexicographic order
public class ASTTest {

	AST<LineGrammar, String> root;
	AST<LineGrammar, String> mainDir;
	AST<LineGrammar, String> testDir;
	AST<LineGrammar, String> mainJava;
	int lineIndex = 0; // only for printTree() testing
	final int INITIAL_AST_SIZE = 11;
	final int INITIAL_TOSTRING_ROWS = 10;
	final int INITIAL_ROOT_VARIABLE_COUNT = 4;

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
	public void iterableAndIteratorOnInitialTest() {
		//only returns direct subtree nodes
		int nodeCounter = 0;
		for(AST<?,?> node : root){
			switch (nodeCounter) {
				case 0:
					assertEquals(node,testDir);
					break;
				case 1:
					assertEquals(node,mainDir);
					break;
				default:
					//root does not have more than 2 subtrees
					fail();
					break;

			}
			nodeCounter++;
		}
		assertEquals(root.getSubtrees().size(),nodeCounter);

		//returns whole ast as (pre)ordered list
		for(AST<?,?> node : root.toListPreorder()){
			//System.out.println(node);
		}

		System.out.println("--------------------------------");
		System.out.println(root.printTree());


		assertTrue(true);
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
		assertFalse(root.addChild(null));
		assertEquals(INITIAL_AST_SIZE, root.size());

		// Adding new Directory as subtree of a Directory should work
		AST<LineGrammar, String> newTree = new AST<>(LineGrammar.Directory, "newROOT");
		int oldSize = root.getSubtrees().size();
		assertTrue(root.addChild(newTree));
		assertEquals(oldSize + 1, root.getSubtrees().size());

		// Adding new Line as subtree of a Directory should not work
		newTree = new AST<>(LineGrammar.Line, "!LineYouAreLookingFor");
		oldSize = root.getSubtrees().size();
		assertFalse(root.addChild(newTree));
		assertEquals(oldSize, root.getSubtrees().size());
	}

	@Test
	public void addAllOnInitialTest() {
		assertFalse(root.addChildren(null));
		assertEquals(INITIAL_AST_SIZE, root.size());

		// Adding new List of ASTs, only the one satisfying the isValidChild of Grammar or != null will be added,
		// rest is ignored.
		List<AST<LineGrammar, String>> newTrees = Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), null,
				new AST<>(LineGrammar.BinaryFile, "101010"), new AST<>(LineGrammar.TextFile, "fancyFile.txt"), new AST<>(LineGrammar.Directory, "fancyFolder"));
		int oldSize = root.getSubtrees().size();
		assertFalse(root.addChildren(newTrees));
		assertEquals(oldSize + 3, root.getSubtrees().size());

		// Adding only valid subtrees to the root
		newTrees = Arrays.asList(new AST<>(LineGrammar.Directory, "public class Main {"), new AST<>(LineGrammar.BinaryFile, "101010"));
		oldSize = root.getSubtrees().size();
		assertTrue(root.addChildren(newTrees));
		assertEquals(oldSize + 2, root.getSubtrees().size());
	}

//	@Test(expected = UnsupportedOperationException.class)
//	public void addOnSubtreesList() {
//		root.getSubtrees().add(new AST<>(LineGrammar.Directory, "evilDir"));
//	}

	@Test
	public void sizeOnInitialTest() {
		assertEquals(INITIAL_AST_SIZE, root.size());
	}

	@Test
	public void sizeOnEmptyASTTest() {
		final AST<LineGrammar, String> badAST = new AST<>(null, null);
		assertEquals(1, badAST.size());
	}

	@Test
	public void getDepthOnInitialTest() {
		assertEquals(4, root.getDepth());
	}

	@Test
	public void getDepthOnEmptyASTTest() {
		final AST<LineGrammar, String> badAST = new AST<>(null, null);
		assertEquals(1, badAST.getDepth());
	}

	@Test
	public void printTreeOnInitialTest() {
		// assure that printTrees has as many rows as the AST has subtrees
		final String[] lines = root.printTree().split(String.format("%n"));
		assertEquals(INITIAL_AST_SIZE, lines.length);

		// assure the number of printed variables of the root value
		final String[] rootAttributes = lines[0].split(" ");
		assertEquals(INITIAL_ROOT_VARIABLE_COUNT, rootAttributes.length);

		// check root values
		checkStringRoot(root, rootAttributes);

		// check subtrees
		checkStringSubTree(root, lines);
	}

	private void checkStringRoot(AST<LineGrammar, String> node, String[] rootAttributes) {
		assertEquals(0, lineIndex);
		// this test also tests the order of the attributes but
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
			// in contrast to checkStringRoot this only checks if the attributes are contained in the string
			// but does not assure their order
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
