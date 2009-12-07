package org.robotframework.jdiameter.keyword;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JDiameterLibraryTest {

    private ApplicationContext applicationContext;

    @Before
    public void setup() {
	if (applicationContext == null) {
	    applicationContext = new ClassPathXmlApplicationContext(
		    "robotframework/jdiameter/keywords.xml");
	}
    }

    @Test
    public void testContext() throws Exception {
	assertTrue(applicationContext.containsBean("sendAVPs"));
	assertTrue(applicationContext.containsBean("receiveAVPs"));
	assertTrue(applicationContext.containsBean("connect"));
	assertTrue(applicationContext.containsBean("disconnect"));
    }

    // @Test
    // public void testLibrary() throws Exception {
    // JDiameterLibrary diameterLibrary = new JDiameterLibrary();
    // String[] keywordNames = diameterLibrary.getKeywordNames();
    // assertArrayEquals(new String[] { "Send AVPs", "Server", "Receive AVPs",
    // "Connect", "Disconnect" }, keywordNames);
    //	
    // JDiameterServer server = new JDiameterServer();
    // server.start();
    //	
    // OpenConnection open =
    // (OpenConnection)applicationContext.getBean("connect");
    // open.execute(new Object[] {});
    //	
    // SendMessage sendMsg =
    // (SendMessage)applicationContext.getBean("sendAVPs");
    //	
    // sendMsg.execute(new Object[] {"MMS-IEC-CCR", "From=486018020793",
    // "From-Location=262200",
    // "To_0=486018020793", "To_0-Location=262200",
    // "CurrentTime=2009-08-06 10:38:00",
    // "MMS-Size=150", "MMS-SendTime=2009-08-06 10:38:00"});
    //	
    // ReceiveMessage rcvMsg =
    // (ReceiveMessage)applicationContext.getBean("receiveAVPs");
    //	
    // rcvMsg.execute(new Object[] {"MMS-IEC-CCA", "Result=SUCCESS"});
    //	
    // CloseConnection close =
    // (CloseConnection)applicationContext.getBean("disconnect");
    // close.execute(new Object[] {});
    // server.stop();
    // }
}
