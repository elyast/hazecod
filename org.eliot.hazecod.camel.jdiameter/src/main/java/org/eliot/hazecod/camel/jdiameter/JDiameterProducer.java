package org.eliot.hazecod.camel.jdiameter;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ExchangeHelper;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.StackImpl;

/**
 *
 *
 */
public class JDiameterProducer extends DefaultProducer {

    static final int TEN = 10;
    Endpoint endpoint;
    Stack stack;
    Configuration configuration;
    Session session;
    boolean testingConnection;

    /**
     * @param endpoint
     *            Endpoint
     * @param configuration
     *            Jdiameter configuration
     */
    public JDiameterProducer(Endpoint endpoint, Configuration configuration) {
	super(endpoint);
	this.endpoint = endpoint;
	this.configuration = configuration;
    }

    /**
     * @param exchange
     *            Exchange
     * @throws Exception
     *             JDiameter exception
     */
    public void process(Exchange exchange) throws Exception {
	Request request = (Request) exchange.getIn().getBody();
	Future<Message> future = session.send(request);
	Answer response = (Answer) future.get();
	if (ExchangeHelper.isOutCapable(exchange)) {
	    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
	    exchange.getOut().setBody(response);
	} else {
	    exchange.getIn().setBody(response);
	}
    }

    /**
     * Opens connection 
     * and waits for handshake at most 10 secnods.
     * @throws IllegalDiameterStateException
     * @throws InternalException
     */
    void openConnection() throws IllegalDiameterStateException,
	    InternalException {
	stack = new StackImpl();
	SessionFactory factory = stack.init(configuration);
	if (testingConnection) {
	    stack.start();
	} else {
	    //Waits for handshake at most 10 seconds
	    stack.start(Mode.ANY_PEER, TEN, TimeUnit.SECONDS);
	}
	session = factory.getNewSession();
    }

    @Override
    protected void doStart() throws Exception {
	super.doStart();
	openConnection();
    }

    @Override
    protected void doStop() throws Exception {
	session.release();
	stack.stop(1, TimeUnit.SECONDS);
	stack.destroy();
	super.doStop();
    }

    /**
     * @return false
     */
    @Override
    public boolean isSingleton() {
	// the producer should not be singleton otherwise
	// cannot use concurrent producers and safely
	// use request/reply with correct correlation
	return false;
    }
    
    /**
     * @param testingConnection If shouldn't wait for server
     */
    public void setTestingConnection(boolean testingConnection) {
	this.testingConnection = testingConnection;
    }

}
