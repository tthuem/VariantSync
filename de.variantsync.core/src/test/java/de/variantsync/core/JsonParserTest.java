package de.variantsync.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.LineGrammar;

public class JsonParserTest {
	
	AST<LineGrammar, String> exampleAst;
	Path exmaplePath;
	
	
    @Before
    public void InitJsonTest() throws IOException
    {
    	//init
    	exampleAst = new AST<>(LineGrammar.Directory, "src");
    	AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.File, "Main.java");
    	exampleAst.children.add(mainJava);
    	mainJava.children.addAll(Arrays.asList(
    	        new AST<>(LineGrammar.Line, "public class Main {"),
    	        new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
    	        new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"),
    	        new AST<>(LineGrammar.Line, "    }"),
    	        new AST<>(LineGrammar.Line, "}")));
    	
    	
    	exmaplePath = Path.of("out.txt");
    	
    }
	 
    @Test
    public void TestJsonParserAST()
    {

    	    	
    	//export to json
    	String json = JsonParserAST.exportAST(exampleAst);
    	

    	//import ast from json
		AST<LineGrammar, String> ast = JsonParserAST.importAST(json);
    	
    	//rexport imported AST
    	String jsonSec= JsonParserAST.exportAST(ast);
    	
    	
    	
    	//print
    	System.out.println("First:"+json);
    	
    	System.out.println("Second:"+jsonSec);
    	
    	//compare json
    	assertTrue(json.equals(jsonSec));
    	    	
    }
    
    
    @Test
    public void TestJsonParserASTtoFile() throws IOException
    {	
    	
    	//export to json file
    	String json = JsonParserAST.exportToFile(exmaplePath, exampleAst);
    	
    	//import ast from file
    	AST<LineGrammar, String> ast = JsonParserAST.importFromFile(exmaplePath);
    	
    	//rexport imported AST
    	String jsonSec= JsonParserAST.exportAST(ast);

    	
    	//print
    	System.out.println("FileFirst:"+json);
    	
    	System.out.println("FileSecond:"+jsonSec);
    	
    	//compare json
    	assertTrue(json.equals(jsonSec));
    	
    	//delete created file
    	Files.delete(exmaplePath);
    }

    
}

