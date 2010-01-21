package org.eliot.hazecod.billing;

import java.util.Date;
import java.util.List;

import org.eliot.hazecod.billing.Unit.UnitType;
import org.eliot.hazecod.management.user.User;

/**
 * @author Eliot
 *
 */
public interface BillableEvent {

    /**
     * @author Eliot
     *
     */
    public enum BillType {
	CHARGE_ONLY, GRANT_ONLY, CHARGE_GRANT, CALCULATION_ONLY, GRANT_CHECK;

	/**
	 * @return If this event will be charged
	 */
	public boolean isChargeable() {
	    return CHARGE_ONLY.equals(this) || CHARGE_GRANT.equals(this);
	}
    }
    
    /**
     * @return User
     */
    User getUser();
    
    /**
     * @return Timestamp of charge
     */
    Date getEventTimestamp();
    
    /**
     * @param event EventTimeStamp
     */
    void setEventTimestamp(Date event);
    
    /**
     * @return Used Units
     */
    List<Unit<?>> getUsedUnits();
    
    /**
     * @param <V> Value Type
     * @param unitType type
     * @param unitValue value
     */
    <V> void addUsedUnit(UnitType unitType, V unitValue);
    
    /**
     * @return Billing Type
     */
    BillType getType();
    
    /**
     * @param type Billing Type
     */
    void setType(BillType type);
    
    /**
     * @return Interpretation context
     */
    String getContext();

    /**
     * @param context Interpretation context
     */
    void setContext(String context);
}
