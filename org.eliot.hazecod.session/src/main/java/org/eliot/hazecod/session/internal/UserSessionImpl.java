package org.eliot.hazecod.session.internal;

import org.eliot.hazecod.management.user.User;
import org.eliot.hazecod.session.UserSession;

/**
 * @author Eliot
 * UserSession of diameter dialog
 */
public class UserSessionImpl implements UserSession {

    String sessionId;
    User user;

    /**
     * @param sessionId SessionId
     */
    public UserSessionImpl(String sessionId) {
	this.sessionId = sessionId;
    }

    /**
     * @param user User
     */
    @Override
    public void setUser(User user) {
	this.user = user;
    }
    
    /** 
     * @return User
     */
    @Override
    public User getUser() {
        return user;
    }

}
