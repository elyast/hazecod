package org.eliot.hazecod.billing.internal;

import java.util.ArrayList;
import java.util.Collections;
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

    User user;
    List<Unit<?>> usedUnits;
    BillType type;
    String context;
    Date eventTime;
    
    /**
     * @param user User to be charged
     */
    public BillingEvent(User user) {
	this.user = user;
	this.usedUnits = new ArrayList<Unit<?>>();
    }

    /**
     * @param <V> Value Type
     * @param unitType Unit Type
     * @param unitValue Unit Value
     * 
     */
    @Override
    public <V> void addUsedUnit(UnitType unitType, V  unitValue) {
	UnitImpl<?> unit = new UnitImpl<V>(unitType, unitValue);
	usedUnits.add(unit);
    }

    /**
     * @return Context of interpretation
     */
    @Override
    public String getContext() {
	return context;
    }

    /** 
     * @return Timestamp of billing
     */
    @Override
    public Date getEventTimestamp() {
	return eventTime;
    }
    
    /** 
     * @return Billing type
     */
    @Override
    public BillType getType() {
	return type;
    }

    /** 
     * @return Used units
     */
    @Override
    public List<Unit<?>> getUsedUnits() {
	return Collections.unmodifiableList(usedUnits);
    }

    /** 
     * @return User
     */
    @Override
    public User getUser() {
	return user;
    }

    /** 
     * @param context Context of interpretation
     */
    @Override
    public void setContext(String context) {
	this.context = context;
    }

    /** 
     * @param event Timestamp
     */
    @Override
    public void setEventTimestamp(Date event) {
	this.eventTime = event;
    }

    /** 
     * @param type Billing type
     */
    @Override
    public void setType(BillType type) {
	this.type = type;
    }

}
