package de.tubs.variantsync.core.patch.interfaces;


public interface IPatchFactory {

	IPatch<IDelta<?>> createPatch(String context);
	
}
