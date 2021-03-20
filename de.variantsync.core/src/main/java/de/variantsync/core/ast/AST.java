package de.variantsync.core.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.variantsync.core.interfaces.Grammar;

public class AST<G extends Grammar, Value> {

	private UUID id;
	private Value value;
	private G type;
	private List<AST<G, Value>> children;

	private static transient final String INDENT_STRING = "    ";
	public static transient final String NEXT_SEPARATOR = "\u2502";
	public static transient final String NEXT_ACT_SEPARATOR = "\u251C\u2500";
	public static transient final String LAST_SEPARATOR = "\u2514\u2500";

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
		for (final AST<G, Value> act : children) {
			tmpSize += act.size();
		}
		return tmpSize;
	}

	private AST() {
		/**
		 * Empty AST is forbidden at the moment.
		 */

	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		if (value == null) {
			return result.toString();
		} else {
			final int depth = 0;

			final HashSet<Integer> levelFinished = new HashSet<>(); // determines if all child nodes of the actual node on this depth have been drawn or not
			final boolean isActualElementLastElement = false;
			toString(result, this, depth, levelFinished, isActualElementLastElement);
		}

		return result.toString();
	}

	private void toString(StringBuilder result, AST<G, Value> parent, int depth, HashSet<Integer> levelFinished, boolean isLast) {


		// choose separator according to whether or not there are child nodes left
		for (int i = 0; i < depth; i++) {
			StringBuilder line = new StringBuilder(INDENT_STRING).append(NEXT_SEPARATOR);
			if (levelFinished.contains(i)) {
				// no need of signs like | because of depth
				line = new StringBuilder(INDENT_STRING);
			}
			if (i == (depth - 1)) {
				// end of indent make arrow
				line = new StringBuilder(INDENT_STRING).append(NEXT_ACT_SEPARATOR);
				if (isLast) {
					// last child of parent, no arrow down needed
					line = new StringBuilder(INDENT_STRING).append(LAST_SEPARATOR);
				}

			}
			result.append(line.toString());

		}
		result.append(String.format("%s %s Depth: %d%n",parent.type,parent.value,depth));
		//result.append(parent.type).append(" ").append(parent.value).append(" Depth: ").append(depth).append("\n");
		depth++;
		for (final AST<G, Value> child : parent.children) {
			isLast = false;
			if (parent.children.indexOf(child) == (parent.children.size() - 1)) {
				// last child of subtree, meaning depth finished, needs |
				levelFinished.add(depth - 1);
				isLast = true;
			} else if (parent.children.indexOf(child) == 0) {
				// first child of new sub tree with unfinished depth needs |
				levelFinished.remove(depth - 1);
			}
			toString(result, child, depth, levelFinished, isLast);

		}
	}
}
