package de.variantsync.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Test;

import de.variantsync.core.EnumLineGrammar.LineGrammar;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    
    
    @Test
    public void TestJsonParserAST()
    {
    	//init
    	AST<LineGrammar, String> srcDir = new AST<>(LineGrammar.Directory, "src");
    	AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.File, "Main.java");
    	srcDir.children.add(mainJava);
    	mainJava.children.addAll(Arrays.asList(
    	        new AST<>(LineGrammar.Line, "public class Main {"),
    	        new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
    	        new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"),
    	        new AST<>(LineGrammar.Line, "    }"),
    	        new AST<>(LineGrammar.Line, "}")));
    	
    	JsonParserAST parser = new JsonParserAST();
    	
    	
    	
    	//export to json
    	String json = parser.exportAST(srcDir);
    	

    	//import ast from json
    	AST ast = parser.importAST(json);
    	
    	//rexport imported AST
    	String jsonSec= parser.exportAST(ast);
    	
    	
    	
    	//print
    	System.out.println("First:"+json);
    	
    	System.out.println("Second:"+jsonSec);
    	
    	//compare json
    	if(json.equals(jsonSec)) {
            assertTrue( true );	
    	}else {
            assertTrue( false );
    	}
    	    	
    }
    
    
    @Test
    public void TestJsonParserASTtoFile() throws IOException
    {
    	//init
    	AST<LineGrammar, String> srcDir = new AST<>(LineGrammar.Directory, "src");
    	AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.File, "Main.java");
    	srcDir.children.add(mainJava);
    	mainJava.children.addAll(Arrays.asList(
    	        new AST<>(LineGrammar.Line, "public class Main {"),
    	        new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
    	        new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"),
    	        new AST<>(LineGrammar.Line, "    }"),
    	        new AST<>(LineGrammar.Line, "}")));
    	
    	JsonParserAST parser = new JsonParserAST();
    	
    	Path path = Path.of("out.txt");
    	
    	
    	//export to json file
    	String json = parser.exportToFile(path, srcDir);
    	
    	//import ast from file
    	AST ast = parser.importFromFile(path);
    	
    	//rexport imported AST
    	String jsonSec= parser.exportAST(ast);

    	
    	//print
    	System.out.println("FileFirst:"+json);
    	
    	System.out.println("FileSecond:"+jsonSec);
    	
    	//compare json
    	if(json.equals(jsonSec)) {
            assertTrue( true );	
    	}else {
            assertTrue( false );
    	}
    	
    	Files.delete(path);

    }
}
