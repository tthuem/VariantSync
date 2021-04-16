package de.variantsync.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.ASTLineGrammarProcessor;
import de.variantsync.core.ast.LineGrammar;
import de.variantsync.core.marker.AMarkerInformation;

public class ASTProcessorTest {
	

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
						new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"), new AST<>(LineGrammar.Line, "    }", "Redo"),
						new AST<>(LineGrammar.Line, "}", "Redo")));
	}
	
	
	@Test
	public void getMarkersTest() {

		List<AMarkerInformation> markers = ASTLineGrammarProcessor.getMarkers(root);


		assertTrue(true);
	}


}
