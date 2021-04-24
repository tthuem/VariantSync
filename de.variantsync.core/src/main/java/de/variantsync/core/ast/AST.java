package de.variantsync.core.ast;

import java.util.*;

import de.variantsync.core.grammar.Grammar;

import org.prop4j.Literal;
import org.prop4j.Node;

/**
 * This class represents the Abstract Syntax Tree data structure.
 *
 * @param <G> a generic which needs to extend the Grammar class, defining the type of the AST
 * @param <V> a generic which defines the value of the actual AST
 * @author eric
 */
public class AST<G extends Grammar<G>, V> implements Iterable<AST<G, V>> {

    private UUID id;
    private G type;
    private V value;
    private List<AST<G, V>> subtrees;
    //TODO: PAUL DO REFACTOR HERE, PLZ
    //public Node featureMapping = null; //eg This AST which represents a Line belongs to That Eclipse Marker
    private String featureMapping = "";
    
    // all attributes which should not be visible to the GSON parser need to be at least transient
    public static transient final String INDENT_STRING = "    ";
    public static transient final String NEXT_SEPARATOR = "\u2502 ";
    public static transient final String NEXT_ACT_SEPARATOR = "\u251C\u2500 ";
    public static transient final String LAST_SEPARATOR = "\u2514\u2500 ";

    public AST(UUID id, G type, V value) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.subtrees = new ArrayList<>();
    }

    public AST(G type, V value) {
        this(UUID.randomUUID(), type, value);
    }
    
    public AST(G type, V value, String context) {
        this(UUID.randomUUID(), type, value);
    	this.featureMapping = context;
    }



    /**
     * Empty AST is forbidden at the moment.
     */
    private AST() {

    }

    public UUID getId() {
        return id;
    }

    public V getValue() {
        return value;
    }

    public G getType() {
        return type;
    }
    
    public void setValue(V v) {
    	this.value = v;
    }
    
    public String getFeatureMapping() {
    	return featureMapping;
    }

    /**
     * @return the subtrees as an unmodifiable List
     */
    public List<AST<G, V>> getSubtrees() {
    	if(subtrees.size() < 1) {
    		return Collections.emptyList();
    	}
        //return Collections.unmodifiableList(subtrees);
    	return subtrees;
    }
    
    

    public int getDepth() {
        int maxDepth = 1;
        if (subtrees.isEmpty()) {
            return maxDepth;
        }

        for (final AST<G, V> node : subtrees) {
            maxDepth = Math.max(node.getDepth(), maxDepth);
        }
        return ++maxDepth;
    }


    /**
     * This method calls the addChild method for each element of the given list. ASTs which would be rejected by the addChild method are skipped.
     *
     * @param toAdd List of AST which should be added as subtrees
     * @return true if all items where successfully added
     */
    public boolean addChildren(List<AST<G, V>> toAdd) {
        boolean out = toAdd != null;
        if (out) {
            for (final AST<G, V> elem : toAdd) {
                if (!addChild(elem)) {
                    out = false;
                }
            }
        }
        return out;
    }

    /**
     * This method adds an element to the AST by checking its validity through the the Grammar G and assuring that the element is not null.
     *
     * @param toAdd Single AST which should be added as subtree
     * @return true if the item was successfully added
     */
    public boolean addChild(AST<G, V> toAdd) {
        if ((toAdd != null) && type.isValidChild(toAdd.type)) {
            return subtrees.add(toAdd);
        }
        return false;
    }

    public int size() {
        int tmpSize = 1;
        for (final AST<G, V> act : subtrees) {
            tmpSize += act.size();
        }
        return tmpSize;
    }

    public List<AST<G, V>> toListPreorder() {
        List<AST<G, V>> preorderList = new ArrayList<>();
        toListPreorder(this, preorderList);
        return preorderList;
    }

    private void toListPreorder(AST<G, V> act, List<AST<G, V>> asts) {
        if (asts != null) {
            asts.add(act);
            act.getSubtrees().forEach(ast -> {
                toListPreorder(ast, asts);
            });
        }
    }

    @Override
    public Iterator<AST<G, V>> iterator() {
        return new ASTSubtreeRootIterator<>(this);
    }

    /**
     * This method returns only the most significant bits of the UUID, the type, value and subtree size as a String of the actual AST.
     *
     * @return UUID, Type, Value, subtree size as String
     */
    @Override
    public String toString() {
        return String.format("[ Id: %d, Type: %s, Value: %s, Subtree-size: %d ]", id.getMostSignificantBits(), type.toString(), value.toString(),
                subtrees.size());
    }

    /**
     * This recursive method prints for each tree element the Grammar type, the Value and (for the sake of readability) only the most significant bits of the
     * UUID. It returns the whole AST as human readable tree.
     *
     * @return AST as readable String
     */
    public String printTree() {
        final StringBuilder result = new StringBuilder();
        if (value != null) {
            final int depth = 0;

            final HashSet<Integer> levelFinished = new HashSet<>(); // determines if all subtrees of the actual tree on this depth have been drawn or not
            final boolean isActualElementLastElement = false;
            printTree(result, this, depth, levelFinished, isActualElementLastElement);
        }
        return result.toString();
    }

    private void printTree(StringBuilder result, AST<G, V> parent, int depth, HashSet<Integer> levelFinished, boolean isLast) {
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
        for (final AST<G, V> child : parent.subtrees) {
            isLast = false;
            if (parent.subtrees.indexOf(child) == (parent.subtrees.size() - 1)) {
                // reached last child of parent
                levelFinished.add(depth - 1);
                isLast = true;
            } else if (parent.subtrees.indexOf(child) == 0) {
                // first child of new sub tree with unfinished depth so it needs NEXT_SEPARATOR later
                levelFinished.remove(depth - 1);
            }
            printTree(result, child, depth, levelFinished, isLast);
        }
    }
    


}
