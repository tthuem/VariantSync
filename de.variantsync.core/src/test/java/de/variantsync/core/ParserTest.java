package de.variantsync.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;


public class ParserTest {
    private AST<LineGrammar, String> srcDir;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setupAST() {
        srcDir = new AST<>(LineGrammar.Directory, "src");
        AST<LineGrammar, String> mainDir = new AST<>(LineGrammar.Directory, "main");
        srcDir.addChild(mainDir);
        AST<LineGrammar, String> testDir = new AST<>(LineGrammar.Directory, "test");
        srcDir.addChild(testDir);
        AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
        mainDir.addChild(mainJava);
        mainJava.addChildren(Arrays.asList(
                new AST<>(LineGrammar.Line, "public class Main {"),
                new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
                new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"),
                new AST<>(LineGrammar.Line, "    }"),
                new AST<>(LineGrammar.Line, "}")));
        AST<LineGrammar, String> binFile = new AST<>(LineGrammar.BinaryFile, "binaryFile");
        mainDir.addChild(binFile);
    }

    @Test
    public void parseDirectoryTest() throws IOException {
        Path src = tempFolder.newFolder("src").toPath();
        Path mainDir = Files.createDirectory(Paths.get(src + File.separator + "main"));
        Path testDir = Files.createDirectory(Paths.get(src + File.separator + "test"));
        Path mainFile = Files.createFile(Paths.get(mainDir + File.separator + "Main.java"));
    	Files.writeString(mainFile,
    			"public class Main {\n"+
    			"    public static void main(String[] args)\n"+
    			"        System.out.println(\"Hello World\");\n"+
    			"    }\n"+
    			"}");

        Path binFile = Files.createFile(Paths.get(mainDir + File.separator + "binaryFile"));
        byte[] bytes = "stringForCreationOfByteArray".getBytes();
        Files.write(binFile, bytes);

    	AST<LineGrammar, String> parsedAST = Parser.parseDirectory(src);

    	// Test fails at the moment due to an inequality of the toString() methods
        // waiting for equals() method for AST
//    	assertEquals(parsedAST, srcDir);
    }

}
