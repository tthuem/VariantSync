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
    	String out = "";
    	if(value == null) {
    		return out;
		} else {
    		int[] level = {1};
    		out += value + "\n";

            HashSet<Integer>  levelFinished = new HashSet<>(); // eg. is level 3 finished?
    		out += toString(level,levelFinished,children);

		}

    	return out;
	}

	private String toString(int[] level, HashSet<Integer> levelFinished,List<AST<Grammar,Value>> children )  {
        String out = "";
        if(levelFinished.contains(level[0])) {
            level[0]++;
            out += "  ";
        }
        if (children.size() == 0) {
            return out;
        }
        int index = children.size() -1;
        for(AST<Grammar,Value> child : children) {
            if(index == 0) {
                //last elem
                out += "\u2514\u2500 " + child.value + " l:" + level[0] + "\n";
                out +="\u2502";
                levelFinished.add(--level[0]);
            }
            else {
                out += "\u251C\u2500 " + child.value + " l:" + level[0]  + "\n";
                out +="\u2502";
            }


            out += /*"\u2514\u2500" +*/ toString(level, levelFinished,child.children);
            index--;

        }
        levelFinished.add(level[0]);
        level[0]--;
        return out;
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
	
