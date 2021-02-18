package de.tubs.variantsync.core.exceptions;

import java.util.NoSuchElementException;

public class ProjectNotFoundException extends NoSuchElementException {

	private static final long serialVersionUID = 1L;

	public enum Type {
		CONFIGURATION, VARIANT
	};

	private final Type type;

	public ProjectNotFoundException(Type type) {
		super("");
		this.type = type;

	}

	public ProjectNotFoundException(Type type, String message) {
		super(message);
		this.type = type;
	}

	@Override
	public String toString() {
		return "A " + (type == Type.CONFIGURATION ? "configuration project" : "variant") + " does not exist in the workspace. " + getMessage();
	}

}
