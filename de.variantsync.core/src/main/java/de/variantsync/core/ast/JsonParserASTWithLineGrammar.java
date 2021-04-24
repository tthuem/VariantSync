package de.variantsync.core.ast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 *
 * Uses gson to import and export ASTs to and from json. A generic use of grammar is not possible, hence for each grammar a different class has to be created.
 * In this case the used grammar is LineGrammar.
 *
 * @author Jeremia Heinle
 *
 */
public class JsonParserASTWithLineGrammar {

	// json parser uses a custom policy to define the name of the field
	// inside of the json
	static FieldNamingStrategy customPolicy = new FieldNamingStrategy() {

		@Override
		public String translateName(Field f) {

			switch (f.getName()) {
			case "id":
				return "uuid";
			case "type":
				return "grammar_type";
			default:
				return f.getName();
			}
		}

	};

	static Gson prettyStringGsonBuilder = new GsonBuilder().setPrettyPrinting().setFieldNamingStrategy(customPolicy).create(); //

	public static <B> String toJson(AST<LineGrammar, B> ast) {

		final Type type = new TypeToken<AST<LineGrammar, String>>() {}.getType();

		return prettyStringGsonBuilder.toJson(ast, type);
	}

	public static <B> AST<LineGrammar, B> toAST(String json) {

		final Type type = new TypeToken<AST<LineGrammar, String>>() {}.getType();

		return prettyStringGsonBuilder.fromJson(json, type);
	}

	public static <B> String exportAST(Path path, AST<LineGrammar, B> ast) throws IOException {

		final String content = toJson(ast);

		Files.writeString(path, content);

		return content;
	}

	public static <B> AST<LineGrammar, B> importAST(Path path) throws IOException {

		String json = "";

		json = Files.readString(path);

		return toAST(json);
	}

	public static String getString() {

		return "da srtring";
	}

}
