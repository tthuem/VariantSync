package de.variantsync.core.ast;

import de.variantsync.core.interfaces.Grammar;

public enum LineGrammar implements Grammar<LineGrammar> {

	Directory, TextFile, BinaryFile, Line;

	@Override
	public boolean isValidChild(LineGrammar child) {
		if (this == LineGrammar.Directory) {
			// Dir can't have line as child
			return child != LineGrammar.Line;
		} else if ((this == LineGrammar.TextFile) || (this == LineGrammar.BinaryFile)) {
			// File can't have dir or file as child
			return child == LineGrammar.Line;
		} else {
			// Line is always leaf node
			return false;
		}

	}

	@Override
	public OptionalType getTypeOf(LineGrammar sym) {
		switch (sym) {
		case Directory:
			return OptionalType.Wrapper;
		case TextFile:
		case BinaryFile:
		case Line:
			return OptionalType.Optional;
		default:
			throw new IllegalArgumentException("[BUG] There is not OptionalType for symbol " + sym + " specified!");
		}
	}
}
