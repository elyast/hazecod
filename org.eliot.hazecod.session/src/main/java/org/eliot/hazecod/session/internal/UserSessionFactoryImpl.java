/**
 * 
 */
package org.eliot.hazecod.session.internal;

import java.util.HashMap;
import java.util.Map;

import org.eliot.hazecod.session.UserSession;
import org.eliot.hazecod.session.UserSessionFactory;

/**
 * @author Eliot
 *
 */
public class UserSessionFactoryImpl implements UserSessionFactory {

    Map<String, UserSession> sessions;
    
    /**
     * 
     */
    public UserSessionFactoryImpl() {
	sessions = new HashMap<String, UserSession>();
    }
    /**
     *	@param sessionId SessionId
     *  @return User session stored 
     */
    public synchronized UserSession getSession(String sessionId) {
	if (sessions.containsKey(sessionId)) {
	    return sessions.get(sessionId);
	}
	UserSession session = createSession(sessionId);
	sessions.put(sessionId, session);
	return session;
    }
    
    UserSession createSession(String sessionId) {
	return new UserSessionImpl(sessionId);
    }

}
