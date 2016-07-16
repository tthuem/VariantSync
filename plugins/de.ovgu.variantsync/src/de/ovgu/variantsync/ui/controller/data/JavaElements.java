package de.ovgu.variantsync.ui.controller.data;

public enum JavaElements {
	PROJECT("project"), PACKAGE("package"), CLASS("class"), METHOD("method"), CODE_FRAGMENT(
			"code");

	private String name;

	JavaElements(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
