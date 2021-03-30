package de.variantsync.core.ast;

import de.variantsync.core.grammar.Grammar;

import java.util.Iterator;


public class ASTIterator<G extends Grammar<G>,V> implements Iterator<AST<G,V>> {
    AST<G,V> ast;
    private int subTreeCounter = 0;

    public ASTIterator(AST<G,V> ast) {
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
