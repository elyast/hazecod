package org.eliot.hazecod.session.internal;

import static org.junit.Assert.*;

import org.eliot.hazecod.session.UserSession;
import org.junit.Before;
import org.junit.Test;

public class UserSessionFactoryImplTest {

    private UserSessionFactoryImpl testObj;
    @Before
    public void setUp() throws Exception {
	testObj = new UserSessionFactoryImpl();
    }

    @Test
    public void testGetSession_NotNull() {
	UserSession session1 = testObj.getSession("xyz");
	assertNotNull(session1);
    }
    
    @Test
    public void testGetSession_TheSame() {
	UserSession session1 = testObj.getSession("xyz");
	UserSession session2 = testObj.getSession("xyz");
	assertEquals(session1, session2);
    }
    
    @Test
    public void testGetSession_Different() {
	UserSession session1 = testObj.getSession("xyz1");
	UserSession session2 = testObj.getSession("xyz2");
	assertNotSame(session1, session2);
    }

}
