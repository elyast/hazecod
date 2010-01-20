package org.eliot.hazecod.billing.internal;

import java.util.Date;
import java.util.List;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.Unit;
import org.eliot.hazecod.billing.Unit.UnitType;
import org.eliot.hazecod.management.user.User;

public class BillingEvent implements BillableEvent {

    @Override
    public void addUsedUnit(UnitType unitType, Object unitValue) {
	// TODO Auto-generated method stub

    }

    @Override
    public String getContext() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Date getEventTimestamp() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public BillType getType() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Unit<?>> getUsedUnits() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public User getUser() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setContext(String context) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setEventTimestamp(Date event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setType(BillType type) {
	// TODO Auto-generated method stub

    }

}
