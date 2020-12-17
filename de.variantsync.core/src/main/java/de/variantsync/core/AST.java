package de.variantsync.core;

import java.util.*;


public class AST<Grammar, Value> {
    public static void main(String[] args) {
        AST<LineGrammar, String> srcDir = new AST<>(LineGrammar.Directory, "src");
        AST<LineGrammar, String> mainDir = new AST<>(LineGrammar.Directory, "main");
        AST<LineGrammar, String> testDir = new AST<>(LineGrammar.Directory, "test");
        AST<LineGrammar, String> mainJava = new AST<>(LineGrammar.File, "Main.java");
        AST<LineGrammar, String> emptyJava = new AST<>(LineGrammar.File, "Empty.java");
        AST<LineGrammar, String> emptyTetJava = new AST<>(LineGrammar.File, "EmptyTest.java");
        srcDir.addChild(testDir);
        testDir.addChild(emptyTetJava);
        srcDir.addChild(mainDir);
        mainDir.addChild(mainJava);
        mainDir.addChild(emptyJava);

        mainJava.addChildren(Arrays.asList(
                new AST<>(LineGrammar.Line, "public class Main {"),
                new AST<>(LineGrammar.Line, "    public static void main(String[] args)"),
                new AST<>(LineGrammar.Line, "        System.out.println(\"Hello World\");"),
                new AST<>(LineGrammar.Line, "    }"),
                new AST<>(LineGrammar.Line, "}")));
        System.out.println(srcDir);
    }

    private UUID id;
    private Grammar type;
    private Value value;
    private List<AST<Grammar, Value>> children;

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
            int[] level = {0}; //pointer magic
            result.append(value + "\n");

            HashSet<Integer> levelFinished = new HashSet<>(); // eg. is level 3 finished?
            //levelFinished.add(level[0]);
            //level[0]++;
            toString(result, this, level, levelFinished, false);
        }

        return result.toString();
    }


    private void toString(StringBuilder result, AST<Grammar, Value> parent, int[] level, HashSet<Integer> levelFinished, boolean isLast) {
        for (int i = 0; i < level[0]; i++) {
            String toAppend = INDENT_STRING + "\u2502 ";
            if (levelFinished.contains(i)) {
                // no need of signs like | because of depth
                toAppend = INDENT_STRING + "  ";
            }
            if (i == level[0] - 1) {
                // end of indent make arrow
                toAppend = INDENT_STRING + "\u251C\u2500";
                if (isLast) {
                    // last child of parent, no arrow down needed
                    toAppend = INDENT_STRING + "\u2514\u2500";
                }

            }
            result.append(toAppend);

        }

        result.append(" ").append(parent.type).append(" ").append(parent.value).append(" Depth: ").append(level[0]).append("\n");
        level[0]++;
        for (AST<Grammar, Value> child : parent.children) {
            isLast = false;
            if (parent.children.indexOf(child) == parent.children.size() - 1) {
                // last child of subtree, meaning level finished, needs |
                levelFinished.add(level[0] - 1);
                isLast = true;
            } else if (parent.children.indexOf(child) == 0) {
                // first child of new sub tree with unfinished level needs |
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
    }


    public void addChildren(List<AST<Grammar, Value>> toAdd) {
        if(toAdd != null) {
            children.addAll(toAdd);
        }
    }

    public void addChild(AST<Grammar, Value> toAdd) {
        if(toAdd != null) {
            children.add(toAdd);
        }
    }

    private boolean isValidChild(AST<Grammar, Value> parent, AST<Grammar, Value> child) {
        if (parent.type instanceof LineGrammar) {
            if (parent.type == LineGrammar.Directory) {
                //Dir can't have line as child
                return child.type != LineGrammar.Line;
            } else if (parent.type == LineGrammar.File) {
                //File can't have dir or file as child
                return child.type == LineGrammar.Line;
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
	
