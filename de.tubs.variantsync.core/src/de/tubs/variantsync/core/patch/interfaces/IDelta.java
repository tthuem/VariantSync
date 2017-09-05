package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

import org.eclipse.core.resources.IResource;

import de.tubs.variantsync.core.data.CodeLine;

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
    public enum TYPE {
    	/** A change in the original. */
        CHANGE, 
        /** A delete from the original. */
        DELETE, 
        /** An insert into the original. */
        INSERT
    }
    
    
    /**
     * Returns original delta
     * @return T - original delta
     */
    List<CodeLine> getOriginal();
    
    /**
     * Sets orginal delta
     * @param original - original delta
     */
    void setOriginal(List<CodeLine> original);
    
    /**
     * Sets orginal delta
     * @param original - original delta
     */
    void setOriginalFromLines(List<String> original);
    
    /**
     * Returns revised delta
     * @return T - revised delta
     */
    List<CodeLine> getRevised();
    
    /**
     * Sets revised delta
     * @param revised - revised delta
     */
    void setRevised(List<CodeLine> revised);
    
    /**
     * Sets revised delta
     * @param revised - revised delta
     */
    void setRevisedFromLines(List<String> revised);
    
    /**
     * 
     * @return
     */
    T getDelta();
    
    /**
     * 
     * @param delta
     */
    void setDelta(T delta);
    
    /**
     * Returns delta type
     * @return TYPE - type
     */
    TYPE getType();
    
    /**
     * Sets type of delta
     * @param type - type
     */
    void setType(TYPE type);
    
    /**
     * Returns resource
     */
    IResource getResource();
    
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
    
    List<CodeLine> getCodeLines();
}