package de.variantsync.core;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

class JsonParserAST {
	
	static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	
	
	public static <A,B> String exportAST(AST<A,B> ast){
		
		Type type = new TypeToken<AST<A,B>>() {}.getType();  
		
		return gson.toJson(ast, type);
	}
	
	public static <A,B> AST<A,B> importAST(String json) {
		
		Type type = new TypeToken<AST<A,B>>() {}.getType();  

		return gson.fromJson(json, type);
	}
	
	
	public static <A,B> String exportToFile(Path path, AST<A,B> ast){

		Type type = new TypeToken<AST<A,B>>() {}.getType();  

    	String content = gson.toJson(ast, type);
        try {
			Files.writeString(path, content);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        
        return content;
	}
	
	public static <A,B> AST<A,B> importFromFile(Path path) {
		
        String json = "";
		try {
			json = Files.readString(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		Type type = new TypeToken<AST<A,B>>() {}.getType();  
		
		return gson.fromJson(json, type);
	}

}