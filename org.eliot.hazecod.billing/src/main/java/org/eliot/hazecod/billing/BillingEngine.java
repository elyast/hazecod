package org.eliot.hazecod.billing;

import org.eliot.hazecod.management.user.User;


/**
 * @author Eliot
 *
 */
public interface BillingEngine {

    /**
     * @param billableEvent Event to evaluate
     * @return result of evaluation
     */
    BillingResult process(BillableEvent billableEvent);

    /**
     * @param user User
     * @return object billable
     */
    BillableEvent createEmptyBillingEvent(User user);

}
