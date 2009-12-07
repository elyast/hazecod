package org.robotframework.jdiameter.keyword;

import org.junit.Before;
import org.junit.Test;

public class JDiameterServerTest {

    private JDiameterServer testObj;

    @Before
    public void setup() {
	testObj = new JDiameterServer();
    }

    @Test
    public void testStart() throws Exception {
	testObj.start();
    }

    @Test
    public void testStop() throws Exception {
	testObj.stop();
    }

}
