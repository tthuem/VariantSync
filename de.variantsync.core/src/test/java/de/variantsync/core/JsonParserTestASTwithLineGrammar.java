package de.variantsync.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.JsonParserASTwithLineGrammar;
import de.variantsync.core.ast.LineGrammar;

public class JsonParserTestASTwithLineGrammar {

	AST<LineGrammar, String> exampleAst;
	Path exmaplePath;

	@Before
	public void InitJsonTest() throws IOException {
		// init
		exampleAst = new AST<>(LineGrammar.Directory, "src");
		final AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
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
		final String json = JsonParserASTwithLineGrammar.exportAST(exampleAst);

		// import ast from json
		final AST<LineGrammar, String> ast = JsonParserASTwithLineGrammar.importAST(json);

		// rexport imported AST
		final String jsonSec = JsonParserASTwithLineGrammar.exportAST(ast);

		// print
		System.out.println("First:" + json);

		System.out.println("Second:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

	}

	@Test
	public void TestJsonParserASTtoFile() throws IOException {

		// export to json file
		final String json = JsonParserASTwithLineGrammar.exportToFile(exmaplePath, exampleAst);

		// import ast from file
		final AST<LineGrammar, String> ast = JsonParserASTwithLineGrammar.importFromFile(exmaplePath);

		// rexport imported AST
		final String jsonSec = JsonParserASTwithLineGrammar.exportAST(ast);

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
		JsonParserASTwithLineGrammar.exportToFile(exmaplePath, exampleAst);

		// import ast from file
		final AST<LineGrammar, String> ast = JsonParserASTwithLineGrammar.importFromFile(exmaplePath);

		// get toString
		final String json = exampleAst.toString();
		final String jsonSec = ast.toString();

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

		// delete created file
		Files.delete(exmaplePath);
	}

}
