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

			final HashSet<Integer> levelFinished = new HashSet<>(); // eg. is depth 3 finished?
			toString(result, this, depth, levelFinished, false);
		}

		return result.toString();
	}

	private void toString(StringBuilder result, AST<G, Value> parent, int depth, HashSet<Integer> levelFinished, boolean isLast) {
		String nextSeparator = "\u2502";
		String nextActSeparator =  "\u251C\u2500";
		String lastSeparator = "\u2514\u2500";
		for (int i = 0; i < depth; i++) {
			String toAppend = INDENT_STRING + nextSeparator + " ";
			if (levelFinished.contains(i)) {
				// no need of signs like | because of depth
				toAppend = INDENT_STRING + "  ";
			}
			if (i == (depth - 1)) {
				// end of indent make arrow
				toAppend = INDENT_STRING + nextActSeparator;
				if (isLast) {
					// last child of parent, no arrow down needed
					toAppend = INDENT_STRING + lastSeparator;
				}

			}
			result.append(toAppend);
			if (i != 0) {
				result.append(" ");
			}

		}

		result.append(parent.type).append(" ").append(parent.value).append(" Depth: ").append(depth).append("\n");
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
