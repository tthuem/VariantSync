package de.variantsync.core.ast;

import de.variantsync.core.grammar.Grammar;

import java.util.Iterator;

/**
 * This iterator only iterates over root nodes of the direct subtrees of the AST without iterating over its root.
 * @param <G> A extended form the Grammar Interface
 * @param <V> V defines the type of the values which are stored in the AST
 */
public class ASTSubtreeRootIterator<G extends Grammar<G>,V> implements Iterator<AST<G,V>> {
    AST<G,V> ast;
    private int subTreeCounter = 0;

    public ASTSubtreeRootIterator(AST<G,V> ast) {
        this.ast = ast;
    }

    @Override
    public boolean hasNext() {
        return subTreeCounter < ast.getSubtrees().size();
    }

    @Override
    //depth first
    public AST<G, V> next() {
        return ast.getSubtrees().get(subTreeCounter++);
    }
}
