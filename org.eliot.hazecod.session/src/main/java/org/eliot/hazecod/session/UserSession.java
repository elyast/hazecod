package org.eliot.hazecod.session;

import org.eliot.hazecod.management.user.User;

/**
 * @author Eliot
 * User session during diameter chat
 */
public interface UserSession {

    /**
     * @return User if stored in user session object
     */
    User getUser();

    /**
     * @param user User to be stored in user session
     */
    void setUser(User user);

}
