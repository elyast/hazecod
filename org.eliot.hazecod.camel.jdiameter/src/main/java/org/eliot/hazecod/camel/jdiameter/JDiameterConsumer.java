package org.eliot.hazecod.camel.jdiameter;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.ObjectHelper;
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

    public static final String APPLICATION_HEADER_NAME = "Application";

    public static final String HOP_BY_HOP_ID_HEADER_NAME = "HopByHopId";

    public static final String END_TO_END_ID_HEADER_NAME = "EndToEndId";

    public static final String COMMAND_CODE_HEADER_NAME = "CommandCode";

    public static final String SESSION_ID_HEADER_NAME = "SessionId";

    public static final String APPLICATION_ID_HEADER_NAME = "ApplicationId";

    static final int CCR_CCA_CODE = 272;

    static final int CCA_APP_ID = 4;

    class NetworkListener implements NetworkReqListener {
	static final String ANSWER = "Answer";

	public Answer processRequest(Request request) {
	    Exchange exchange = endpoint.createExchange();
	    Message inMsg = exchange.getIn();
	    inMsg.setBody(request);
	    setHeader(inMsg, request);
	    try {
		getProcessor().process(exchange);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	    Object body;
	    if (ExchangeHelper.isOutCapable(exchange)) {
		body = exchange.getOut().getBody();
	    } else {
		body = inMsg.getBody();
	    }
	    ObjectHelper.notNull(body, ANSWER);
	    return (Answer) body;
	}
    }

    static final long VENDOR_ID = 193;

    Stack stack;
    Configuration configuration;
    Network network;
    Endpoint endpoint;

    NetworkListener netListener;

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

    void setHeader(Message msg, Request request) {
	long appId = request.getApplicationId();
	int commandCode = request.getCommandCode();
	String sessionId = request.getSessionId();
	long e2eId = request.getEndToEndIdentifier();
	long hbhId = request.getHopByHopIdentifier();
	msg.setHeader(APPLICATION_ID_HEADER_NAME, appId);
	msg.setHeader(SESSION_ID_HEADER_NAME, sessionId);
	msg.setHeader(COMMAND_CODE_HEADER_NAME, commandCode);
	msg.setHeader(END_TO_END_ID_HEADER_NAME, e2eId);
	msg.setHeader(HOP_BY_HOP_ID_HEADER_NAME, hbhId);
	Application app = Application.UNKNOWN;
	if (appId == CCA_APP_ID && commandCode == CCR_CCA_CODE) {
	    app = Application.CCA;
	}
	msg.setHeader(APPLICATION_HEADER_NAME, app.toString());
    }

    protected void doStart() throws Exception {
	super.doStart();
	stack = new StackImpl();
	stack.init(configuration);

	stack.start();

	network = ((PeerTable) stack.unwrap(PeerTable.class))
		.unwrap(Network.class);
	Configuration config = stack.getMetaData().getConfiguration();
	ApplicationId[] appIds = getFromConfiguration(config);
	netListener = new NetworkListener();
	network.addNetworkReqListener(netListener, appIds);
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
