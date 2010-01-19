package org.eliot.hazecod.management.user.internal;

import org.eliot.hazecod.management.user.User;

/**
 * @author Eliot
 *
 */
public class UserImpl implements User {

    String userId;
    
    /**
     * @param userId UserId
     */
    public UserImpl(String userId) {
	this.userId = userId;
    }

}
