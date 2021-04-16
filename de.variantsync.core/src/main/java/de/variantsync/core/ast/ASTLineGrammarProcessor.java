package de.variantsync.core.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import de.variantsync.core.marker.AMarkerInformation;
import de.variantsync.core.marker.IVariantSyncMarker;

public class ASTLineGrammarProcessor {
	
	public static List<AMarkerInformation> getMarkers(AST<LineGrammar, String> ast){
		//AST -> AMarkerINformations machen
		List<AMarkerInformation> out = new ArrayList<>();

		for(AST<?,?> subtree : ast.getSubtrees()) {
			System.out.println("childs "+ subtree.getValue());
			
			//final IVariantSyncMarker variantSyncMarker = new AMarkerInformation(revised.getPosition() - 1, revised.getLines().size() - 1, true);

		}
	
		return out;
		
	}
	
	
    /**
     * @return subtree with same name and LineGrammar
     */
	public static AST<LineGrammar, String> getSubtree(String name, LineGrammar textfile, AST<LineGrammar, String> ast) {
		
		if(ast.getValue().equals(name) && ast.getType() == textfile) {
			return ast;
		}
		
		AST<LineGrammar, String> back = null;
		for(AST<LineGrammar, String>  subtree : ast) {

			back = getSubtree(name,textfile,subtree);
			if(back != null){

				return back;
			}
			
		}
		
		return back;		
	}
	
	
	


}
