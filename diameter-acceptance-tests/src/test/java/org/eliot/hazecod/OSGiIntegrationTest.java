package org.eliot.hazecod;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.jdiameter.client.impl.helpers.Parameters.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Eliot
 * 
 */
@RunWith(JUnit4TestRunner.class)
public class OSGiIntegrationTest {

    private final class ClientConfiguration extends EmptyConfiguration {
	public ClientConfiguration() {
	    super();
	    add(Assembler, Assembler.defValue());
	    add(OwnDiameterURI, "aaa://localhost:1812");
	    add(OwnRealm, "diacl.cca");
	    add(OwnVendorID, 193L);
	    add(ApplicationId, getInstance().add(VendorId, 193L).add(
		    AuthApplId, 4L).add(AcctApplId, 0L));
	    add(PeerTable, getInstance().add(PeerRating, 1).add(PeerName,
		    "aaa://localhost:3868"));
	    add(RealmTable, getInstance().add(RealmEntry,
		    "eliot.cca.org:" + "localhost"));
	}
    }

    static final int CCAPP = 4;
    static final int VENDOR = 193;
    static final int CCR = 272;
    static final int TEN = 10;
    static final int ACTIVE = 32;
    
    @Inject
    private BundleContext bundleContext;

    @Configuration
    public static Option[] configuration() {
	return options(
		wrappedBundle(mavenBundle(maven("aopalliance", "aopalliance", "1.0"))).instructions("Export-Package=*;version=1.0"),
		mavenConfiguration()		
		);
    }

    @Test
    public void testCamelIntegration() throws Exception {
	assertThat(bundleContext, is(notNullValue()));
	for (Bundle b : bundleContext.getBundles()) {
	    System.out.println("Bundle " + b.getBundleId() + ":"
		    + b.getSymbolicName() + " is " + getState(b));
	    assertEquals(ACTIVE, b.getState());
	}

	Stack stack = new StackImpl();
	org.jdiameter.api.Configuration configuration = new ClientConfiguration();
	SessionFactory factory = stack.init(configuration);
	// Waits for handshake at most 10 seconds
	stack.start(Mode.ANY_PEER, TEN, TimeUnit.SECONDS);
	Session session = factory.getNewSession();
	Request msg = session.createRequest(CCR,
		org.jdiameter.api.ApplicationId
			.createByAuthAppId(VENDOR, CCAPP), "eliot.cca.org");
	Future<Message> lock = session.send(msg);
	Answer answer = (Answer) lock.get();
	assertEquals(ResultCode.SUCCESS, answer.getAvps().getAvp(
		Avp.RESULT_CODE).getInteger32());
	session.release();
	stack.stop(TEN, TimeUnit.SECONDS);
	stack.destroy();
    }

    private String getState(Bundle b) {
	int state = b.getState();
	switch (state) {
	case ACTIVE:
	    return "ACTIVE";
	case 2:
	    return "INSTALLED";
	default:
	    return "" + state;
	}
    }
}
