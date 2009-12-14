package org.eliot.hazecod.camel.jdiameter;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.util.ExchangeHelper;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.Parameters;

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

    private static final long VENDOR_ID = 193;

    Stack stack;
    Configuration configuration;
    Network network;
    Endpoint endpoint;

    /**
     * @param endpoint
     *            Endpoint
     * @param processor
     *            processor
     * @param configuration
     *            Jdiameter configuration
     */
    public JDiameterConsumer(Endpoint endpoint, Processor processor,
	    Configuration configuration) {
	super(endpoint, processor);
	this.endpoint = endpoint;
	this.configuration = configuration;
    }

    protected void doStart() throws Exception {
	super.doStart();
	stack = new StackImpl();
	stack.init(configuration);

	stack.start();

	network = ((PeerTable) stack.unwrap(PeerTable.class))
		.unwrap(Network.class);
	network.addNetworkReqListener(new NetworkListener(),
		getFromConfiguration(stack.getMetaData().getConfiguration()));
    }

    ApplicationId[] getFromConfiguration(Configuration config) {
	Configuration[] appIds = config.getChildren(Parameters.ApplicationId
		.ordinal());
	ApplicationId[] result = new ApplicationId[appIds.length];
	int i = 0;
	for (Configuration c : appIds) {
	    long vendorId = c.getLongValue(Parameters.VendorId.ordinal(),
		    VENDOR_ID);
	    long authAppId = c.getLongValue(Parameters.AuthApplId.ordinal(), 0);
	    long acctAppId = c.getLongValue(Parameters.AcctApplId.ordinal(), 0);
	    if (authAppId != 0) {
		result[i++] = ApplicationId.createByAuthAppId(vendorId,
			authAppId);
	    } else {
		result[i++] = ApplicationId.createByAccAppId(vendorId,
			acctAppId);
	    }
	}
	return result;
    }

    protected void doStop() throws Exception {
	super.doStop();
	stack.stop(1, TimeUnit.SECONDS);
	stack.destroy();
    }

}
