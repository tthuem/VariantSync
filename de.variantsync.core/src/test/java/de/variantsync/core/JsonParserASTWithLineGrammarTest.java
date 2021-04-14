package de.variantsync.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.JsonParserASTWithLineGrammar;
import de.variantsync.core.ast.LineGrammar;

//TODO. Add compare method to AST and then compare ASTs with this method.

public class JsonParserASTWithLineGrammarTest {

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

		examplePath = Paths.get("out.txt");

	}

	@Test
	public void TestJsonParserAST() {
		
		

		// export to json
		final String json = JsonParserASTWithLineGrammar.toJson(exampleAst);

		// import ast from json
		final AST<LineGrammar, String> importAST = JsonParserASTWithLineGrammar.toAST(json);

		// toJson imported AST
		final String importedJson = JsonParserASTWithLineGrammar.toJson(importAST);

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + importedJson);

		// compare json
		assertEquals(json, importedJson);

	}

	@Test
	public void TestJsonParserASTtoFile() throws IOException {

		// export to json file
		final String json = JsonParserASTWithLineGrammar.exportAST(examplePath, exampleAst);

		// import ast from file
		final AST<LineGrammar, String> importedAST = JsonParserASTWithLineGrammar.importAST(examplePath);

		// toJson imported AST
		final String importedJson = JsonParserASTWithLineGrammar.toJson(importedAST);

		// print
		System.out.println("FileFirst:" + json);

		System.out.println("FileSecond:" + importedJson);

		// compare json
		assertEquals(json, importedJson);

		// delete created file
		Files.delete(examplePath);
	}

	@Test
	public void TestJsonParserASTtoFileToString() throws IOException {

//		// export to json file
//		JsonParserASTWithLineGrammar.exportAST(examplePath, exampleAst);
//
//		// import ast from file
//		final AST<LineGrammar, String> importedAST = JsonParserASTWithLineGrammar.importAST(examplePath);
//
//		// get toString
//		final String astString = exampleAst.toString();
//		final String importedString = importedAST.toString();
//
//		// print
//		System.out.println("FileFirst:" + astString);
//
//		System.out.println("FileSecond:" + importedString);
//
//		// compare json
//		assertEquals(astString, importedString);
//
//		// delete created file
//		Files.delete(examplePath);
	}

}
