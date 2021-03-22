package de.variantsync.core.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.variantsync.core.interfaces.Grammar;

/**
 * This class represents the Abstract Syntax Tree data structure.
 *
 * @param <G>
 * @param <Value>
 */
public class AST<G extends Grammar, Value> {

    private UUID id;
    private Value value;
    private G type;
    private List<AST<G, Value>> subtree;

    // all attributes which should not be visible to the GSON parser need to be at least transient
    public static transient final String INDENT_STRING = "    ";
    public static transient final String NEXT_SEPARATOR = "\u2502 ";
    public static transient final String NEXT_ACT_SEPARATOR = "\u251C\u2500 ";
    public static transient final String LAST_SEPARATOR = "\u2514\u2500 ";

    public AST(G type, Value value) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.value = value;
        this.subtree = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public G getType() {
        return type;
    }

    public void setType(G type) {
        this.type = type;
    }

    public List<AST<G, Value>> getSubtree() {
        return subtree;
    }

    public void setSubtree(List<AST<G, Value>> subtree) {
        this.subtree = subtree;
    }

    public void addChildren(List<AST<G, Value>> toAdd) {
        if (toAdd != null) {
            subtree.addAll(toAdd);
        }
    }

    public void addChild(AST<G, Value> toAdd) {
        if (toAdd != null) {
            subtree.add(toAdd);
        }
    }

    public int size() {
        int tmpSize = 1;
        for (final AST<G, Value> act : subtree) {
            tmpSize += act.size();
        }
        return tmpSize;
    }

    /**
     * Empty AST is forbidden at the moment.
     */
    private AST() {

    }

    public int getMaxDepth() {
        if (subtree.size() == 0) {
            return 0;
        }

        int maxDepth = 0;
        for (AST<G, Value> node : subtree) {
            maxDepth = Math.max(node.getMaxDepth(), maxDepth);
        }
        return ++maxDepth;
    }

    /**
     * This recursive method prints for each tree element the Grammar type, the Value and (for the sake of readability)
     * only the most significant bits of the UUID. It returns the AST as human readable tree.
     *
     * @return AST as readable String
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        if (value == null) {
            return result.toString();
        } else {
            final int depth = 0;

            final HashSet<Integer> levelFinished = new HashSet<>(); // determines if all subtrees of the actual tree on this depth have been drawn or not
            final boolean isActualElementLastElement = false;
            toString(result, this, depth, levelFinished, isActualElementLastElement);
        }
        return result.toString();
    }

    private void toString(StringBuilder result, AST<G, Value> parent, int depth, HashSet<Integer> levelFinished, boolean isLast) {
        // print enough INDENT_STRINGS and choose separator according to whether or not there are subtrees left
        for (int i = 0; i < depth; i++) {
            StringBuilder line = new StringBuilder(INDENT_STRING).append(NEXT_SEPARATOR);
            if (levelFinished.contains(i)) {
                // new depth indent
                line = new StringBuilder(INDENT_STRING);
            }
            if (i == (depth - 1)) {
                // make separator for next subtree and printing actual subtree fields
                line = new StringBuilder(INDENT_STRING).append(NEXT_ACT_SEPARATOR);
                if (isLast) {
                    // last subtree of parent tree, only print last separator for last subtree fields
                    line = new StringBuilder(INDENT_STRING).append(LAST_SEPARATOR);
                }
            }
            result.append(line.toString());
        }
        result.append(String.format("%s %s uuid: %d%n", parent.type, parent.value, parent.getId().getMostSignificantBits()));
        depth++;
        for (final AST<G, Value> child : parent.subtree) {
            isLast = false;
            if (parent.subtree.indexOf(child) == (parent.subtree.size() - 1)) {
                // reached last child of parent
                levelFinished.add(depth - 1);
                isLast = true;
            } else if (parent.subtree.indexOf(child) == 0) {
                // first child of new sub tree with unfinished depth so it needs NEXT_SEPARATOR later
                levelFinished.remove(depth - 1);
            }
            toString(result, child, depth, levelFinished, isLast);
        }
    }
}
