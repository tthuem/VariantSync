package de.variantsync.core.interfaces;

public interface Grammar<T> {

	enum OptionalType {
		NodeOptional, TreeOptional, Mandatory
	}

	public boolean isValidChild(T child);

	public OptionalType getTypeOf(T sym);
}
