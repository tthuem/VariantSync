package de.variantsync.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

class JsonParserAST {
	
	Gson gson = new Gson();
	
	public JsonParserAST() {
	}
	
	public String exportAST(AST ast){
		
		return gson.toJson(ast);
	}
	
	public AST importAST(String json) {
		
		return gson.fromJson(json, AST.class);
	}
	
	
	public String exportToFile(Path path, AST ast){

    	String content = gson.toJson(ast);
        try {
			Files.writeString(path, content);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        
        return content;
	}
	
	public AST importFromFile(Path path) {
		
        String json = "";
		try {
			json = Files.readString(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return gson.fromJson(json, AST.class);
	}

}
