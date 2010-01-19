package org.eliot.hazecod.billing;

public interface Unit {

    public enum UnitType {
	TIME, MONEY, BYTES_SENT, BYTES_RECEIVED, BYTES, OTHER 
    }
    
    UnitType getType();
    long getValue();
}
