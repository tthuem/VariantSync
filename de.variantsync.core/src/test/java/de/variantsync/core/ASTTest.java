package de.variantsync.core;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.variantsync.core.LineGrammar;

public class ASTTest {
    AST<LineGrammar,String> ast;

    @Before
    public void setup() {
        ast = new AST<>(LineGrammar.Directory, "src");
    }

    @Test
    public void addTest() {


    }

}
