package org.eliot.hazecod.session;

/**
 * @author Eliot
 *
 */
public interface UserSessionFactory {

    /**
     * @param sessionId Session Id
     * @return Newly created if not exists or return existing one
     */
    UserSession getSession(String sessionId);

}
