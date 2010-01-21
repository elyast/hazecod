package org.eliot.hazecod.billing.internal;

import org.eliot.hazecod.billing.BillingResult;

/**
 * @author Eliot
 *
 */
public class BillingResultImpl implements BillingResult {

    boolean succeeded;
    /**
     * @return <code>true</code> if succeded
     */
    @Override
    public boolean isSucceded() {
	return succeeded;
    }
    
    /**
     * @param succeeded if success
     */
    public void setSucceeded(boolean succeeded) {
	this.succeeded = succeeded;
    }

}
