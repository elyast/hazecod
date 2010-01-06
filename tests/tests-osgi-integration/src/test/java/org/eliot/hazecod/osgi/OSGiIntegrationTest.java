package org.eliot.hazecod.osgi;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.ConfigurableOsgiBundleApplicationContext;

/**
 * @author Eliot
 * 
 */
@RunWith(MavenConfiguredJUnit4TestRunner.class)
public class OSGiIntegrationTest {

    @Inject
    private BundleContext bundleContext;

    @Test
    public void testCamelIntegration() throws Exception {	
	assertThat(bundleContext, is(notNullValue()));
	for (Bundle b : bundleContext.getBundles()) {
	    System.out.println("Bundle " + b.getBundleId() + ":"
		    + b.getSymbolicName() + " is " + getState(b));
	    assertEquals(Bundle.ACTIVE, b.getState());
	}		
	ServiceReference[] sr = waitForServices(
		ConfigurableOsgiBundleApplicationContext.class.getName(), 10000);
	assertNotNull(sr);
	assertEquals(1, sr.length);
	for (ServiceReference serviceReference : sr) {
	    System.out.println(serviceReference.getBundle().getSymbolicName());
	}
    }

    private ServiceReference[] waitForServices(String name, long timeout)
	    throws InvalidSyntaxException, InterruptedException {
	if (timeout <= 0) {
	    throw new RuntimeException("Timeout must be greater than 0");
	}
	long timea = System.currentTimeMillis();
	System.out.println(timea);
	long timer = timeout;
	int increment = 10;
	while (bundleContext.getAllServiceReferences(name, null) == null && timer > 0) {
	    Thread.sleep(increment);
	    timer -= increment;
	}
	System.out.println("TimeToWait" + (System.currentTimeMillis() - timea));
	return bundleContext.getAllServiceReferences(name, null);
    }

    private String getState(Bundle b) {
	int state = b.getState();
	switch (state) {
	case Bundle.ACTIVE:
	    return "ACTIVE";
	case Bundle.INSTALLED:
	    return "INSTALLED";
	case Bundle.RESOLVED:
	    return "RESOLVED";
	default:
	    return "" + state;
	}
    }
}
