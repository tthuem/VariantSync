package de.variantsync.core;

import java.util.*;


public class AST<Grammar, Value> {
    public static void main(String[] args) {
        AST<EnumLineGrammar.LineGrammar, String> srcDir = new AST<>(EnumLineGrammar.LineGrammar.Directory, "src");
        AST<EnumLineGrammar.LineGrammar, String> mainDir = new AST<>(EnumLineGrammar.LineGrammar.Directory, "main");
        AST<EnumLineGrammar.LineGrammar, String> testDir = new AST<>(EnumLineGrammar.LineGrammar.Directory, "test");
        AST<EnumLineGrammar.LineGrammar, String> mainJava = new AST<>(EnumLineGrammar.LineGrammar.File, "Main.java");
        AST<EnumLineGrammar.LineGrammar, String> emptyJava = new AST<>(EnumLineGrammar.LineGrammar.File, "Empty.java");
        AST<EnumLineGrammar.LineGrammar, String> emptyTetJava = new AST<>(EnumLineGrammar.LineGrammar.File, "EmptyTest.java");
        srcDir.children.add(testDir);
        testDir.children.add(emptyTetJava);
        srcDir.children.add(mainDir);
        mainDir.children.add(mainJava);
        mainDir.children.add(emptyJava);


        mainJava.children.addAll(Arrays.asList(
                new AST<>(EnumLineGrammar.LineGrammar.Line, "public class Main {"),
                new AST<>(EnumLineGrammar.LineGrammar.Line, "    public static void main(String[] args)"),
                new AST<>(EnumLineGrammar.LineGrammar.Line, "        System.out.println(\"Hello World\");"),
                new AST<>(EnumLineGrammar.LineGrammar.Line, "    }"),
                new AST<>(EnumLineGrammar.LineGrammar.Line, "}")));
        System.out.println(srcDir.size);
        System.out.println(srcDir);
    }

    UUID id;
    Grammar type;
    Value value;
    int size;
    List<AST<Grammar, Value>> children;

    private final String INDENT_STRING = "    ";


    /**
     * TODO:
     * - sanity check like: A Directory can't have direct child Line or Lines are always leaf nodes
     * - add
     * - toString
     * - equals
     * - compareTo ?
     * - hash ?
     * - toList ?
     * - size
     * - get
     * - contains
     * - remove
     * <p>
     * - testing
     */

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (value == null) {
            return result.toString();
        } else {
            int[] level = {1};
            result.append(value + "\n");

            HashSet<Integer> levelFinished = new HashSet<>(); // eg. is level 3 finished?
            for (AST<Grammar, Value> child : children) {
                toString(result, child, level, levelFinished, false);
            }
        }

        return result.toString();
    }


    private void toString(StringBuilder result, AST<Grammar, Value> parent, int[] level, HashSet<Integer> levelFinished, boolean isLast) {
        for (int i = 0; i < level[0]; i++) {
            String toAppend = INDENT_STRING + "\u2502 ";
            if (i == level[0] - 1) {
                toAppend = INDENT_STRING + "\u251C\u2500 ";
                if (isLast) {
                    toAppend = INDENT_STRING + "\u2514\u2500 ";
                }

            }
            result.append(toAppend);

        }
        result.append(parent.value + " Depth: " + level[0] + "\n");
        level[0]++;
        for (AST<Grammar, Value> child : parent.children) {
            isLast = false;
            if (parent.children.indexOf(child) == parent.children.size() - 1) {
                //last elem
                levelFinished.add(level[0] - 1);
                isLast = true;
            } else if (parent.children.indexOf(child) == 0) {
                levelFinished.remove(level[0] - 1);
            }
            toString(result, child, level, levelFinished, isLast);

        }
        level[0]--;
    }

    public AST(Grammar type, Value value) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
        size = 1;
    }

    public void add(Grammar gram, Value val) {
        if (value == null) {
            id = UUID.randomUUID();
            type = gram;
            value = val;
            children = new ArrayList<>();
            size = 1;
        } else {
            AST<Grammar, Value> toAdd = new AST<>(gram, val);
            if (children.size() == 0) {
                children.add(toAdd);
            } else {
                for (AST<Grammar, Value> act : children) {
                    if (isValidChild(act, toAdd)) {
                        act.children.add(toAdd);
                        size++;
                        break;
                    }

                }
                System.err.println("AST-WARNING: Child could not be added!");
            }
        }
    }

    private boolean isValidChild(AST<Grammar, Value> parent, AST<Grammar, Value> child) {
        if (parent.type instanceof EnumLineGrammar.LineGrammar) {
            if (parent.type == EnumLineGrammar.LineGrammar.Directory) {
                //Dir can't have line as child
                return child.type != EnumLineGrammar.LineGrammar.Line;
            } else if (parent.type == EnumLineGrammar.LineGrammar.File) {
                //File can't have dir or file as child
                return child.type == EnumLineGrammar.LineGrammar.Line;
            } else {
                //Line is always leaf node
                return false;
            }
        } else {
            throw new IllegalArgumentException("Grammar has wrong type: " + parent.type.getClass());
        }


    }

    private AST() {
        /**
         * Empty AST is forbidden at the moment.
         */

    }
}
	
