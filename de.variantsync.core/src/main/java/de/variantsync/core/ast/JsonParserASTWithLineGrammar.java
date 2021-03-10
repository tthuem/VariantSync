package de.variantsync.core.ast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.variantsync.core.interfaces.Grammar;

/**
 *
 * Uses gson to import and export ASTs to and from json. A generic use of grammar is not possible, hence for each grammar a different class has to be created.
 * In this case the used grammar is LineGrammar.
 *
 * @author jerem
 *
 */
public class JsonParserASTWithLineGrammar {

	static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

	public static <A extends Grammar, B> String exportAST(AST<A, B> ast) {

		// generic use of A not possible, instead declare used Grammar here
		final Type type = new TypeToken<AST<LineGrammar, B>>() {}.getType();

		return gson.toJson(ast, type);
	}

	public static <A extends Grammar, B> AST<A, B> importAST(String json) {

		// generic use of A not possible, instead declare used Grammar here
		final Type type = new TypeToken<AST<LineGrammar, B>>() {}.getType();

		return gson.fromJson(json, type);
	}

	public static <A extends Grammar, B> String exportToFile(Path path, AST<A, B> ast) {

		// generic use of A not possible, instead declare used Grammar here
		final Type type = new TypeToken<AST<LineGrammar, B>>() {}.getType();

		final String content = gson.toJson(ast, type);
		try {
			Files.writeString(path, content);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		return content;
	}

	public static <A extends Grammar, B> AST<A, B> importFromFile(Path path) {

		String json = "";
		try {
			json = Files.readString(path);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		// generic use of A not possible, instead declare used Grammar here
		final Type type = new TypeToken<AST<LineGrammar, B>>() {}.getType();

		return gson.fromJson(json, type);
	}

}
