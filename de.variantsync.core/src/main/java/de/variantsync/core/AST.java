package de.variantsync.core;

import java.util.ArrayList;
import java.util.List;

public class AST<Grammar, Value> {
    int UUID;
    Grammar type;
    Value value;
    List<AST<Grammar, Value>> children = new ArrayList<>();
    
	public AST(Grammar type, Value value) {
		super();
		this.type = type;
		this.value = value;
	}
	
    public enum LineGrammar {
        Directory, File, Line
    }

}