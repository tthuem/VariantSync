package de.tubs.variantsync.core.patch.interfaces;

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
    TYPE getType();
    
    /**
     * Sets type of delta
     * @param type - type
     */
    void setType(TYPE type);
}