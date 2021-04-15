package de.variantsync.core.ast;

import java.util.List;

import de.variantsync.core.marker.AMarkerInformation;

public class ASTLineGrammarProcessor {
	
	public static List<AMarkerInformation> getMarker(AST<LineGrammar,String> ast) {
	
		return null;
		
	}
	
	
    /**
     * @return subtree with same name and LineGrammar
     */
	private static AST<LineGrammar, String> getSubtree(String name, LineGrammar textfile, AST<LineGrammar, String> ast) {
		
		if(ast.getValue() == name && ast.getType() == textfile) {
			return ast;
		}
		
		AST<LineGrammar, String> back = null;
		for(AST<LineGrammar, String>  subtree : ast.getSubtrees()) {
			
			if(getSubtree(name,textfile,subtree) != null) {
				back = subtree;
			}
			
		}
		
		return back;		
	}


}
