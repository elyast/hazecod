package org.eliot.hazecod.billing.internal;

import java.util.Date;
import java.util.List;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.Unit;
import org.eliot.hazecod.billing.Unit.UnitType;
import org.eliot.hazecod.management.user.User;

/**
 * @author Eliot
 *
 */
public class BillingEvent implements BillableEvent {

    /**
     * @param unitType Unit Type
     * @param unitValue Unit Value
     * 
     */
    @Override
    public void addUsedUnit(UnitType unitType, Object unitValue) {
	// TODO Auto-generated method stub

    }

    /**
     * @return Context of interpretation
     */
    @Override
    public String getContext() {
	// TODO Auto-generated method stub
	return null;
    }

    /** 
     * @return Timestamp of billing
     */
    @Override
    public Date getEventTimestamp() {
	// TODO Auto-generated method stub
	return null;
    }
    
    /** 
     * @return Billing type
     */
    @Override
    public BillType getType() {
	// TODO Auto-generated method stub
	return null;
    }

    /** 
     * @return Used units
     */
    @Override
    public List<Unit<?>> getUsedUnits() {
	// TODO Auto-generated method stub
	return null;
    }

    /** 
     * @return User
     */
    @Override
    public User getUser() {
	// TODO Auto-generated method stub
	return null;
    }

    /** 
     * @param context Context of interpretation
     */
    @Override
    public void setContext(String context) {
	// TODO Auto-generated method stub

    }

    /** 
     * @param event Timestamp
     */
    @Override
    public void setEventTimestamp(Date event) {
	// TODO Auto-generated method stub

    }

    /** 
     * @param type Billing type
     */
    @Override
    public void setType(BillType type) {
	// TODO Auto-generated method stub

    }

}
