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
}
