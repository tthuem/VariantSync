package de.variantsync.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.JsonParserAST;
import de.variantsync.core.ast.LineGrammar;
import org.junit.Before;
import org.junit.Test;

public class JsonParserTest {

	AST<LineGrammar, String> exampleAst;
	Path exmaplePath;

	@Before
	public void InitJsonTest() throws IOException {
		// init
		exampleAst = new AST<>(LineGrammar.Directory, "src");
		AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
		exampleAst.addChild(mainJava);
		mainJava.addChildren(
				Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
						new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"), new AST<>(LineGrammar.Line, "    }"),
						new AST<>(LineGrammar.Line, "}")));

		exmaplePath = Path.of("out.txt");

	}

	@Test
	public void TestJsonParserAST() {

		// export to json
		String json = JsonParserAST.exportAST(exampleAst);

		// import ast from json
		AST<LineGrammar, String> ast = JsonParserAST.importAST(json);

		// rexport imported AST
		String jsonSec = JsonParserAST.exportAST(ast);

		// print
		System.out.println("First:" + json);

		System.out.println("Second:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

	}

	@Test
	public void TestJsonParserASTtoFile() throws IOException {

		// export to json file
		String json = JsonParserAST.exportToFile(exmaplePath, exampleAst);

		// import ast from file
		AST<LineGrammar, String> ast = JsonParserAST.importFromFile(exmaplePath);

		// rexport imported AST
		String jsonSec = JsonParserAST.exportAST(ast);

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

		// delete created file
		Files.delete(exmaplePath);
	}

	@Test
	public void TestJsonParserASTtoFileToString() throws IOException {

		// export to json file
		JsonParserAST.exportToFile(exmaplePath, exampleAst);

		// import ast from file
		AST<LineGrammar, String> ast = JsonParserAST.importFromFile(exmaplePath);

		// get toString
		String json = exampleAst.toString();
		String jsonSec = ast.toString();

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

		// delete created file
		Files.delete(exmaplePath);
	}

}
