package org.eliot.hazecod.billing.internal;

import org.eliot.hazecod.billing.Unit;

/**
 * @author Eliot
 *
 * @param <V> Value
 */
public class UnitImpl<V> implements Unit<V> {

    UnitType unitType;
    V unitValue;
    /**
     * @param unitType Unit Type
     * @param unitValue Unit Value
     */
    public UnitImpl(UnitType unitType, V unitValue) {
	this.unitType = unitType;
	this.unitValue = unitValue;
    }

    /**
     * @return unit type
     */
    @Override
    public org.eliot.hazecod.billing.Unit.UnitType getType() {
	return unitType;
    }

    /**
     * @return value
     */
    @Override
    public V getValue() {
	return unitValue;
    }

}
