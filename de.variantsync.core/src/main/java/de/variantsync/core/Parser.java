package de.variantsync.core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Parser {

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
				result = new AST<>(LineGrammar.TextFile, folder.getName(folder.getNameCount()-1).toString());
				List<String> fileStream = Files.readAllLines(folder);
				for (String line : fileStream) {
					result.addChild(new AST<>(LineGrammar.Line, line));
				}
			} else {
				result = new AST<>(LineGrammar.BinaryFile, folder.getName(folder.getNameCount()-1).toString());
			}
		}
	    return result;
    }
    
    private static boolean isBinaryFile(Path file) {
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
