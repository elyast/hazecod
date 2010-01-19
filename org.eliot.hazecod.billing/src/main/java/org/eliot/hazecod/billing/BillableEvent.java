package org.eliot.hazecod.billing;

import java.util.List;

import org.eliot.hazecod.management.user.User;

public interface BillableEvent {

    User getUser();
    
    long getEventTimestamp();
    
    List<Unit> getUsedUnits();
    
    List<Unit> getRequestedUnits();
}
