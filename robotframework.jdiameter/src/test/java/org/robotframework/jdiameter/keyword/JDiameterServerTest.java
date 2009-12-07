package org.robotframework.jdiameter.keyword;

import static org.junit.Assert.*;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
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
