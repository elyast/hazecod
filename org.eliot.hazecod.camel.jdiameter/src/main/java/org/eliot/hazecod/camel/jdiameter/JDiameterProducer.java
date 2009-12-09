package org.eliot.hazecod.camel.jdiameter;

import static org.eliot.hazecod.camel.jdiameter.JDiameterConfiguration.*;

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
import org.jdiameter.server.impl.StackImpl;

/**
 *
 *
 */
public class JDiameterProducer extends DefaultProducer {

    Endpoint endpoint;
    Stack stack;
    Configuration configuration;
    Session session;
    
    /**
     * @param endpoint Endpoint
     * @param configuration Jdiameter configuration
     */
    public JDiameterProducer(Endpoint endpoint, Configuration configuration) {
	super(endpoint);
	this.endpoint = endpoint;
	this.configuration = configuration;
    }

    /**
     * @param exchange Exchange
     * @throws Exception JDiameter exception
     */
    public void process(Exchange exchange) throws Exception {
	session = openConnection();
	Request body = (Request) exchange.getIn().getBody();
	Future<Message> future = session.send(body);
	Answer response = (Answer) future.get(); 
	if (ExchangeHelper.isOutCapable(exchange)) {
	    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
            exchange.getOut().setBody(response);
        } else {
            exchange.getIn().setBody(response);
        }

    }

    private Session openConnection() throws IllegalDiameterStateException,
	    InternalException {
	stack = new StackImpl();
	SessionFactory factory = stack.init(configuration);
	stack.start(Mode.ANY_PEER, TEN, TimeUnit.SECONDS);
	return factory.getNewSession();
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        openConnection();
    }

    @Override
    protected void doStop() throws Exception {

	session.release();
	stack.stop(TEN, TimeUnit.SECONDS);
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

}
