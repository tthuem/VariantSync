package de.variantsync.core.grammar;

public interface Grammar<T> {

	enum OptionalType {
		Wrapper, Optional, Mandatory
	}

	public boolean isValidChild(T child);

	public OptionalType getTypeOf(T sym);
}
