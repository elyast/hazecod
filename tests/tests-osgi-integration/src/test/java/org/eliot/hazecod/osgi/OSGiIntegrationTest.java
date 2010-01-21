package org.eliot.hazecod.osgi;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.eliot.hazecod.billing.BillingEngine;
import org.eliot.hazecod.integration.diameter.ServiceWorkflow;
import org.eliot.hazecod.management.user.UserManagementService;
import org.eliot.hazecod.session.UserSessionFactory;
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
	
	ServiceReference[] sr2 = waitForServices(1,
		UserManagementService.class.getName(), 10000);
	assertNotNull(sr2);
	assertEquals(1, sr2.length);
	for (ServiceReference serviceReference : sr2) {
	    System.out.println("Serivce References: " + serviceReference);
	}
	
	ServiceReference[] sr3 = waitForServices(1,
		BillingEngine.class.getName(), 10000);
	assertNotNull(sr3);
	assertEquals(1, sr3.length);
	for (ServiceReference serviceReference : sr3) {
	    System.out.println("Serivce References: " + serviceReference);
	}
	
	ServiceReference[] sr4 = waitForServices(1,
		UserSessionFactory.class.getName(), 10000);
	assertNotNull(sr4);
	assertEquals(1, sr4.length);
	for (ServiceReference serviceReference : sr4) {
	    System.out.println("Serivce References: " + serviceReference);
	}
	
	ServiceReference[] sr5 = waitForServices(1,
		ServiceWorkflow.class.getName(), 10000);
	assertNotNull(sr5);
	assertEquals(1, sr5.length);
	for (ServiceReference serviceReference : sr5) {
	    System.out.println("Serivce References: " + serviceReference);
	}
	ServiceReference[] sr = waitForServices(5, 
		ConfigurableOsgiBundleApplicationContext.class.getName(), 10000);
	
	assertNotNull(sr);
	assertEquals(5, sr.length);
	for (ServiceReference serviceReference : sr) {
	    System.out.println("Serivce References: " + serviceReference);
	}	
    }

    private ServiceReference[] waitForServices(int count, String name, long timeout)
	    throws InvalidSyntaxException, InterruptedException {
	if (timeout <= 0) {
	    throw new RuntimeException("Timeout must be greater than 0");
	}
	long timea = System.currentTimeMillis();
	System.out.println(timea);
	long timer = timeout;
	int increment = 10;
	while (!isProperCountOfServices(name, count) && timer > 0) {
	    Thread.sleep(increment);
	    timer -= increment;
	}
	System.out.println("TimeToWait" + (System.currentTimeMillis() - timea));
	return bundleContext.getAllServiceReferences(name, null);
    }

    private boolean isProperCountOfServices(String name, int count) throws InvalidSyntaxException {
	ServiceReference[] srt = bundleContext.getAllServiceReferences(name, null);
	return srt != null && srt.length >= count;
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
