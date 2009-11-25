package robotframework.jdiameter;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DiameterLibraryTest {

    private ApplicationContext applicationContext;

    @Before
    public void setup() {
	if (applicationContext == null) {
	    applicationContext = new ClassPathXmlApplicationContext(
		    "com/nsn/hydra/robotframework/diameter/keywords.xml");
	}
    }

    @Test
    public void testContext() throws Exception {
	assertTrue(applicationContext.containsBean("sendAVPs"));
	assertTrue(applicationContext.containsBean("receiveAVPs"));
	assertTrue(applicationContext.containsBean("connect"));
	assertTrue(applicationContext.containsBean("disconnect"));
    }

    @Test
    public void testLibrary() throws Exception {
	DiameterLibrary diameterLibrary = new DiameterLibrary();
	String[] keywordNames = diameterLibrary.getKeywordNames();
	assertTrue(Arrays.equals(new String[] { "Send AVPs", "Receive AVPs",
		"Connect", "Disconnect" }, keywordNames));
    }
}
