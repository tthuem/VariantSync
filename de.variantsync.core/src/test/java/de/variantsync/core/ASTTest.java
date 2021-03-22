package de.variantsync.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.ast.AST;
import de.variantsync.core.ast.LineGrammar;

//TODO. Add compare method to AST and then compare ASTs with this method.

public class ASTTest {
    AST<LineGrammar, String> root;
    AST<LineGrammar, String> mainDir;
    AST<LineGrammar, String> testDir;
    AST<LineGrammar, String> mainJava;
    int lineIndex = 0; // only for toString testing

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
                Arrays.asList(new AST<>(LineGrammar.Line, "public class Main {"),
                        new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
                        new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"),
                        new AST<>(LineGrammar.Line, "    }"),
                        new AST<>(LineGrammar.Line, "}"))
        );
    }

    @Test
    public void sizeTest() {
        assertEquals(11, root.size());
        root = new AST<>(null, null);
        assertEquals(1, root.size());
    }

    @Test
    public void toStringTest() {
        String[] lines = root.toString().split(String.format("%n"));
        assertEquals(11, lines.length);
        // test root values
        String[] rootAttributes = lines[0].split(" ");
        assertEquals(4, rootAttributes.length);
        // in general checking like this is not possible due to the nature of AST.INDENT_STRING
        // but root line does not contain AST.INDENT_STRING
        assertEquals(rootAttributes[0], root.getType().toString()); //Type
        assertEquals(rootAttributes[1], root.getValue()); // Value
        assertEquals(rootAttributes[2], "uuid:");
        assertEquals(rootAttributes[3], ((Long) root.getId().getMostSignificantBits()).toString()); // UUID

        // check subtrees
        printSubTree(root, lines);
    }

    private void printSubTree(AST<LineGrammar, String> node, String[] lines) {
        lineIndex++;
        for (final AST<LineGrammar, String> child : node.getSubtree()) {
            System.out.printf("%s %s %3$d%n", child.getType().toString(), child.getValue(), child.getId().getMostSignificantBits());
            String line = lines[lineIndex];
            assertTrue(line.contains(AST.INDENT_STRING));
            assertTrue(line.contains(AST.NEXT_SEPARATOR)
                    || line.contains(AST.NEXT_ACT_SEPARATOR)
                    || line.contains(AST.LAST_SEPARATOR)
            );
            assertTrue(line.contains(child.getType().toString()));
            assertTrue(line.contains(child.getValue()));
            assertTrue(line.contains(((Long) child.getId().getMostSignificantBits()).toString()));

            printSubTree(child, lines);
        }
    }
}
