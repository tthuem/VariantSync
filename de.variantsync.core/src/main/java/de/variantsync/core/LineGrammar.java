package de.variantsync.core;

import java.util.ArrayList;
import java.util.List;

public enum LineGrammar {
	Directory, TextFile, BinaryFile, Line;

	private List<Object> addAttributes;

	LineGrammar() {
		addAttributes = new ArrayList<>();
	}

	LineGrammar(List<Object> addAttributes) {
		this.addAttributes = addAttributes;
	}

	public List<Object> getAttributes() {
		return addAttributes;
	}

	private boolean isValidChild(LineGrammar parent, LineGrammar child) {
		if (parent == LineGrammar.Directory) {
			//Dir can't have line as child
			return child != LineGrammar.Line;
		} else if (parent == LineGrammar.TextFile || parent == LineGrammar.BinaryFile) {
			//File can't have dir or file as child
			return child == LineGrammar.Line;
		} else {
			//Line is always leaf node
			return false;
		}

	}

}
