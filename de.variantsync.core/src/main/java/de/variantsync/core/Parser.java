package de.variantsync.core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.variantsync.core.LineGrammar;



public class Parser 
{
    public static void main( String[] args )
    {
    	Path path = Paths.get("src");
    	try {
			AST<LineGrammar, String> tryAndError = parseDirectory(path);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static AST<LineGrammar, String> parseDirectory(Path folder) throws IOException {
    	AST<LineGrammar, String> result = null;
	    if (Files.isDirectory(folder)) {
	    	result = new AST<>(LineGrammar.Directory, folder.getName(folder.getNameCount()-1).toString());
	    	DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder);
		    for (Path entry : directoryStream) {
			    result.addChild(parseDirectory(entry));
		    }
	    } else {
			if (!isBinaryFile(folder)) {
				result = new AST<>(LineGrammar.File, folder.getName(folder.getNameCount()-1).toString());
		        List<String> fileStream = Files.readAllLines(folder);
		        for (String line : fileStream) {
		        	result.addChild(new AST<>(LineGrammar.Line, line));
		        }
			}
		}
	    return result;
    }
    
    public static boolean isBinaryFile(Path file) {
		try {
			String type = Files.probeContentType(file);
			if (type == null) {
				//type couldn't be determined => assume binary
				return true;
			} else if (type.startsWith("text")) {
				//non-binary
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//type isn't text => assume binary
		return true;
    }
}
