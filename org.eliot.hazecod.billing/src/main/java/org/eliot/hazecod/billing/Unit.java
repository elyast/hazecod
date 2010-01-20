package org.eliot.hazecod.billing;

/**
 * @author Eliot
 * @param <V> Value Type
 *
 */
public interface Unit<V> {

    /**
     * @author Eliot
     *
     */
    public enum UnitType {
	TIME, MONEY, BYTES_SENT, BYTES_RECEIVED, BYTES, SPECIFIC_UNIT 
    }
    
    /**
     * @return Type of unit
     */
    UnitType getType();
    
    /**
     * @return Value of units
     */
    V getValue();
}
