package org.eliot.hazecod.camel.jdiameter;

import static org.eliot.hazecod.camel.jdiameter.JDiameterConfiguration.*;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.util.ExchangeHelper;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;

/**
 * @author Eliot
 *
 */
public class JDiameterConsumer extends DefaultConsumer {

    private final class NetworkListener implements NetworkReqListener {
	public Answer processRequest(Request request) {
	    Exchange exchange = endpoint.createExchange();
	    exchange.getIn().setBody(request);
	    try {
		getProcessor().process(exchange);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
            Object body;
            if (ExchangeHelper.isOutCapable(exchange)) {
                body = exchange.getOut().getBody();
            } else {
                body = exchange.getIn().getBody();
            }
            return (Answer) body;
	}
    }

    Stack stack;
    Configuration configuration;
    Network newtwork;
    Endpoint endpoint;

    /**
     * @param endpoint Endpoint
     * @param processor processor
     * @param configuration Jdiameter configuration
     */
    public JDiameterConsumer(Endpoint endpoint, Processor processor,
	    JDiameterConfiguration configuration) {
	super(endpoint, processor);
	this.endpoint = endpoint;
	this.configuration = configuration;
    }

    protected void doStart() throws Exception {
	super.doStart();
	stack = new StackImpl();
	stack.init(configuration);

	stack.start(Mode.ANY_PEER, TEN, TimeUnit.SECONDS);

	newtwork = ((PeerTable) stack.unwrap(PeerTable.class))
		.unwrap(Network.class);
	newtwork.addNetworkReqListener(new NetworkListener(),
		new ApplicationId[] {org.jdiameter.api.ApplicationId
			.createByAccAppId(VENDOR_ID, ACC_APP_ID) });
    }

    protected void doStop() throws Exception {
	super.doStop();
	stack.stop(TEN, TimeUnit.SECONDS);
	newtwork.removeNetworkReqListener(org.jdiameter.api.ApplicationId
		.createByAccAppId(VENDOR_ID, ACC_APP_ID));
    }

}
