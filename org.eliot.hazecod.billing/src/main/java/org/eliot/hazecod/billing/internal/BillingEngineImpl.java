package org.eliot.hazecod.billing.internal;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.BillingEngine;
import org.eliot.hazecod.billing.BillingResult;
import org.eliot.hazecod.management.user.User;

/**
 * @author Eliot
 *
 */
public class BillingEngineImpl implements BillingEngine {

    /** 
     * @param user User
     * @return Event to be billable
     */
    @Override
    public BillableEvent createEmptyBillingEvent(User user) {
	return new BillingEvent(user);
    }

    /**
     * @param billableEvent event to be billed
     * @return result of do the billing
     */
    @Override
    public BillingResult process(BillableEvent billableEvent) {
	// TODO Auto-generated method stub
	return null;
    }

}
