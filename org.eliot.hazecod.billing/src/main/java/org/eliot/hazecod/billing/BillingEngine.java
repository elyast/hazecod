package org.eliot.hazecod.billing;


public interface BillingEngine {

    BillingResult process(BillableEvent billableEvent);

}
