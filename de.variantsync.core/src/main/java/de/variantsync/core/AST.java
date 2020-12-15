package de.variantsync.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AST<Grammar, Value> {
	UUID id;
    Grammar type;
    Value value;
    List<AST<Grammar, Value>> children = new ArrayList<>();
    
	public AST(Grammar type, Value value) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.value = value;
	}
	
}