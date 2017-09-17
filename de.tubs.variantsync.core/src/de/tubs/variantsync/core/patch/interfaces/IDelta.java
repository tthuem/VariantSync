package de.tubs.variantsync.core.patch.interfaces;

import java.io.Serializable;
import java.util.List;

import org.eclipse.core.resources.IResource;

import de.tubs.variantsync.core.data.CodeLine;
import de.tubs.variantsync.core.data.FeatureExpression;

/**
 * 
 * 
 * @author Christopher Sontag
 * @version 1.0
 * @since 18.08.2017
 * @param <T> file element, e.g. line, ast element, ...
 */
public interface IDelta<T> {
	
	/**
     * Specifies the type of the delta.
     *
     */
    public enum DELTATYPE {
    	/** An addition was made */
    	ADDED, 
        /** An deletion was made */
        REMOVED, 
        /** A change was made */
        CHANGED
    }
    
    
    /**
     * Returns original delta
     * @return T - original delta
     */
    T getOriginal();
    
    /**
     * Sets orginal delta
     * @param original - original delta
     */
    void setOriginal(T original);
    
    /**
     * Returns revised delta
     * @return T - revised delta
     */
    T getRevised();
    
    /**
     * Sets revised delta
     * @param revised - revised delta
     */
    void setRevised(T revised);
    
    /**
     * Returns delta type
     * @return TYPE - type
     */
    DELTATYPE getType();
    
    /**
     * Sets type of delta
     * @param type - type
     */
    void setType(DELTATYPE type);
    
    /**
     * Returns feature
     * @return FeatureExpression - feature
     */
    String getFeature();
    
    /**
     * Sets feature expression of delta
     * @param feature - FeatureExpression
     */
    void setFeature(String feature);
    
    /**
     * Returns resource
     */
    IResource getResource();
    
    IPatch<?> getPatch();
    
    void setPatch(IPatch<?> patch);
    
    /**
     * 
     * @return
     */
    boolean isSynchronized();
    
    /**
     * 
     * @param isSynchronized
     */
    void setSynchronized(boolean isSynchronized);
    
    /**
     * 
     * @return
     */
    long getTimestamp();
    
    Object getProperty(String key);
    
    void addProperty(String key, Serializable obj);

	static DELTATYPE DELTATYPE(int i) {
		switch(i) {
		case 1: return DELTATYPE.ADDED;
		case 2: return DELTATYPE.REMOVED;
		case 4: return DELTATYPE.CHANGED;
		default: return null;
		}
	}
}