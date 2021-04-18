package de.variantsync.core.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import de.variantsync.core.marker.AMarkerInformation;
import de.variantsync.core.marker.IVariantSyncMarker;

public class ASTLineGrammarProcessor {
	
	public static List<IVariantSyncMarker> getMarkers(AST<LineGrammar, String> ast){
		//AST -> AMarkerINformations machen
		List<IVariantSyncMarker> out = new ArrayList<>();

		int count = 0;
		int start = 0;
		String featureContext = "";
		for(AST<LineGrammar, String>  subtree : ast.getSubtrees()) {
			count++;
			System.out.println("childs "+ subtree.getValue());
			if((!subtree.getFeatureMapping().isEmpty()) && (!featureContext.equals(subtree.getFeatureMapping()) || (featureContext.isEmpty()))) {	
				
				if((!featureContext.equals(subtree.getFeatureMapping()))){
					//Marker end
					IVariantSyncMarker aMarker = new AMarkerInformation(start, count - 1, true, featureContext);
					out.add(aMarker);
				}
				
				//Marker start
				featureContext = subtree.getFeatureMapping();
				start = count;
			}
			
			if(!featureContext.isEmpty() && subtree.getFeatureMapping().isEmpty()) {
				//Marker end
				featureContext = "";
				IVariantSyncMarker aMarker = new AMarkerInformation(start, count - 1, true, featureContext);
				out.add(aMarker);
			}

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
