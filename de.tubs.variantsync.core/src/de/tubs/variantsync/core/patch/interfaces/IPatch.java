package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

public interface IPatch<T> {

	List<IChange<T>> getChanges(); 
	
	String toString();
	
	int compareTo(IPatch<T> other);
}
