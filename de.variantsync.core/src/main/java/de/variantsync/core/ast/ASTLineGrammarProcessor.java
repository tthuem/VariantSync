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
		int size = ast.getSubtrees().size();
		String featureContext = "";
		for(AST<LineGrammar, String>  subtree : ast.getSubtrees()) {
			count++;
			if(!subtree.getFeatureMapping().isEmpty() && ( featureContext.isEmpty() ||  (!featureContext.equals(subtree.getFeatureMapping()) &&   !featureContext.isEmpty()))){	
				
				if(!featureContext.equals(subtree.getFeatureMapping()) &&   !featureContext.isEmpty()) {
					IVariantSyncMarker aMarker = new AMarkerInformation(start, count-start , true, featureContext, count);
					out.add(aMarker);
				}
				
				//Marker start
				featureContext = subtree.getFeatureMapping();
				start = count;
			}
			
			if(!featureContext.isEmpty() && (subtree.getFeatureMapping().isEmpty() || count == size)) {
				//Marker end
				IVariantSyncMarker aMarker = null;
				if(count == size) {
					// case: Marker at the end of file
					aMarker = new AMarkerInformation(start, count-start+1 , true, featureContext, count);
				}else {
					aMarker = new AMarkerInformation(start, count-start , true, featureContext, count);
				}
				out.add(aMarker);
				featureContext = "";
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
