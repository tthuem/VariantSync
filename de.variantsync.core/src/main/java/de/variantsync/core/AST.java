package de.variantsync.core;

import java.util.ArrayList;
import java.util.List;

public class AST<Grammar, Value> {
    static int count;
	int UUID;
    Grammar type;
    Value value;
    List<AST<Grammar, Value>> children = new ArrayList<>();
    
	public AST(Grammar type, Value value) {
		super();
		this.UUID = count++;
		this.type = type;
		this.value = value;
	}
	
}