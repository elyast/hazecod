package org.eliot.hazecod.management.user;

/**
 * @author Eliot
 *
 * Manages Users
 */
public interface UserManagementService {

    /**
     * @param userId Some ID
     * @param identityType Type of Id
     * @return User
     */
    User getUser(String userId, String identityType);

}
