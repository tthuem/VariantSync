package de.variantsync.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.JsonParserASTWithLineGrammar;
import de.variantsync.core.ast.LineGrammar;

public class JsonParserTestASTWithLineGrammar {

	AST<LineGrammar, String> exampleAst;
	Path examplePath;

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

		examplePath = Path.of("out.txt");

	}

	@Test
	public void TestJsonParserAST() {

		// export to json
		final String json = JsonParserASTWithLineGrammar.exportAST(exampleAst);

		// import ast from json
		final AST<LineGrammar, String> ast = JsonParserASTWithLineGrammar.importAST(json);

		// rexport imported AST
		final String jsonSec = JsonParserASTWithLineGrammar.exportAST(ast);

		// print
		System.out.println("First:" + json);

		System.out.println("Second:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

	}

	@Test
	public void TestJsonParserASTtoFile() throws IOException {

		// export to json file
		final String json = JsonParserASTWithLineGrammar.exportToFile(examplePath, exampleAst);

		// import ast from file
		final AST<LineGrammar, String> ast = JsonParserASTWithLineGrammar.importFromFile(examplePath);

		// rexport imported AST
		final String jsonSec = JsonParserASTWithLineGrammar.exportAST(ast);

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

		// delete created file
		Files.delete(examplePath);
	}

	@Test
	public void TestJsonParserASTtoFileToString() throws IOException {

		// export to json file
		JsonParserASTWithLineGrammar.exportToFile(examplePath, exampleAst);

		// import ast from file
		final AST<LineGrammar, String> ast = JsonParserASTWithLineGrammar.importFromFile(examplePath);

		// get toString
		final String json = exampleAst.toString();
		final String jsonSec = ast.toString();

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + jsonSec);

		// compare json
		assertTrue(json.equals(jsonSec));

		// delete created file
		Files.delete(examplePath);
	}

}
