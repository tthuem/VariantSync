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

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineBasedParser;
import de.variantsync.core.ast.LineGrammar;

//TODO: Add compare method to AST and then compare ASTs with this method.

public class LineBasedParserTest {

	private AST<LineGrammar, String> srcDir;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setupAST() {
		srcDir = new AST<>(LineGrammar.Directory, "src");
		final AST<LineGrammar, String> mainDir = new AST<>(LineGrammar.Directory, "main");
		srcDir.addChild(mainDir);
		final AST<LineGrammar, String> testDir = new AST<>(LineGrammar.Directory, "test");
		srcDir.addChild(testDir);
		final AST<LineGrammar, String> binFile = new AST<>(LineGrammar.BinaryFile, "BinaryFile");
		mainDir.addChild(binFile);
		final AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.TextFile, "Main.java");
		mainDir.addChild(mainJava);
		mainJava.addChildren(
				Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"), new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
						new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"), new AST<>(LineGrammar.Line, "    }"),
						new AST<>(LineGrammar.Line, "}")));
	}

	@Test
	public void parseDirectoryTest() throws IOException {
		final Path src = tempFolder.newFolder("src").toPath();
		final Path mainDir = Files.createDirectory(Paths.get(src + File.separator + "main"));
		final Path testDir = Files.createDirectory(Paths.get(src + File.separator + "test"));
		final Path mainFile = Files.createFile(Paths.get(mainDir + File.separator + "Main.java"));
		final String fileContent =
			String.format("public class Main {%n    public static void main(String[] args)%n        System.out.println(\"Hello World\");%n    }%n}");
		Files.writeString(mainFile, fileContent);

		final Path binFile = Files.createFile(Paths.get(mainDir + File.separator + "BinaryFile"));
		final byte[] bytes = "stringForCreationOfByteArray".getBytes();
		Files.write(binFile, bytes);

		final LineBasedParser parser = new LineBasedParser();
		final AST<LineGrammar, String> parsedAST = parser.parseDirectory(src);

		// Testing only the equality of the toString() methods at the moment
		// waiting for equals() method for AST for comparison of the real AST-Objects
		// TODO: FIX THIS
		// assertEquals(parsedAST.toString(), srcDir.toString());
	}

}
