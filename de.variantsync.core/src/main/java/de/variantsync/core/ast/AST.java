package de.variantsync.core.ast;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.variantsync.core.grammar.Grammar;

public class AST<G extends Grammar, Value> {

	@Expose
	@SerializedName(value = "uuid")
	private UUID id;
	@Expose
	@SerializedName(value = "value")
	private Value value;
	@Expose
	@SerializedName(value = "grammar_type")
	private G type;
	@Expose
	@SerializedName(value = "children")
	private List<AST<G, Value>> children;

	private final String INDENT_STRING = "    ";

	/**
	 * TODO: - sanity check like: A Directory can't have direct child Line or Lines are always leaf nodes - add - toString - equals - compareTo ? - hash ? -
	 * toList ? - size - get - contains - remove <p> - testing
	 */

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		if (value == null) {
			return result.toString();
		} else {
			int[] level = { 0 }; // pointer magic
			// result.append(value + "\n");

			HashSet<Integer> levelFinished = new HashSet<>(); // eg. is level 3 finished?
			// levelFinished.add(level[0]);
			// level[0]++;
			toString(result, this, level, levelFinished, false);
		}

		return result.toString();
	}

	private void toString(StringBuilder result, AST<G, Value> parent, int[] level, HashSet<Integer> levelFinished, boolean isLast) {
		for (int i = 0; i < level[0]; i++) {
			String toAppend = INDENT_STRING + "\u2502 ";
			if (levelFinished.contains(i)) {
				// no need of signs like | because of depth
				toAppend = INDENT_STRING + "  ";
			}
			if (i == level[0] - 1) {
				// end of indent make arrow
				toAppend = INDENT_STRING + "\u251C\u2500";
				if (isLast) {
					// last child of parent, no arrow down needed
					toAppend = INDENT_STRING + "\u2514\u2500";
				}

			}
			result.append(toAppend);
			if (i != 0) result.append(" ");

		}

		result.append(parent.type).append(" ").append(parent.value).append(" Depth: ").append(level[0]).append("\n");
		level[0]++;
		for (AST<G, Value> child : parent.children) {
			isLast = false;
			if (parent.children.indexOf(child) == parent.children.size() - 1) {
				// last child of subtree, meaning level finished, needs |
				levelFinished.add(level[0] - 1);
				isLast = true;
			} else if (parent.children.indexOf(child) == 0) {
				// first child of new sub tree with unfinished level needs |
				levelFinished.remove(level[0] - 1);
			}
			toString(result, child, level, levelFinished, isLast);

		}
		level[0]--;
	}

	public AST(G type, Value value) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.value = value;
		this.children = new ArrayList<>();
	}

	public void addChildren(List<AST<G, Value>> toAdd) {
		if (toAdd != null) {
			children.addAll(toAdd);
		}
	}

	public void addChild(AST<G, Value> toAdd) {
		if (toAdd != null) {
			children.add(toAdd);
		}
	}

	public int size() {
		int tmpSize = 1;
		for (AST<G, Value> act : children) {
			tmpSize += act.size();
		}
		return tmpSize;
	}

	private AST() {
		/**
		 * Empty AST is forbidden at the moment.
		 */

	}
}
