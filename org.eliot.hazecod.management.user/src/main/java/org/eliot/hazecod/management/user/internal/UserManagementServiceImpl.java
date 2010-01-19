/**
 * 
 */
package org.eliot.hazecod.management.user.internal;

import org.eliot.hazecod.management.user.User;
import org.eliot.hazecod.management.user.UserManagementService;

/**
 * @author Eliot
 *
 */
public class UserManagementServiceImpl implements UserManagementService {

    /** 
     * @param userId UserId
     * @param identityType Identity type
     * @return User
     */
    public User getUser(String userId, String identityType) {
	return new UserImpl(userId);
    }

}
