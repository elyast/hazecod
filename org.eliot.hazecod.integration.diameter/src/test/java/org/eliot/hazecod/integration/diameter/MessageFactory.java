package org.eliot.hazecod.integration.diameter;

import java.util.ArrayList;
import java.util.List;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;

/**
 * @author Eliot
 *
 */
public final class MessageFactory {
    
    static final long SOME_VENDOR = 4329L;
    static final long DEF_APP_ID = 4;
    static final long GPP = 10415L;
    
    private MessageFactory() {
    }    

    public static Message createCER() throws InternalException, IllegalDiameterStateException {
	Session df = new StackImpl().init(EmptyConfiguration.getInstance()).getNewSession();
	Message msg = df.createRequest(Message.CAPABILITIES_EXCHANGE_REQUEST, 
		ApplicationId.createByAccAppId(DEF_APP_ID), "eliot.org");

	msg.getAvps().addAvp(Avp.ORIGIN_HOST, "diacl", true);
	msg.getAvps().addAvp(Avp.ORIGIN_REALM, "bln1.siemens.de", true);
	msg.getAvps().addAvp(Avp.HOST_IP_ADDRESS, "10.154.39.92", false);
	msg.getAvps().addAvp(Avp.VENDOR_ID, GPP);
	msg.getAvps().addAvp(Avp.PRODUCT_NAME, "@vantage", false);
	return msg;
    }

    public static List<Object> createValue(Object data) {
	List<Object> list1 = new ArrayList<Object>();
	list1.add(data);
	return list1;
    }

    public static Message createCEA() throws InternalException, IllegalDiameterStateException {
	Session df = new StackImpl().init(EmptyConfiguration.getInstance()).getNewSession();
	Message msg = df.createRequest(Message.CAPABILITIES_EXCHANGE_REQUEST,
		ApplicationId.createByAccAppId(DEF_APP_ID), "eliot.org");
	msg = ((Request)msg).createAnswer(ResultCode.SUCCESS);
	msg.getAvps().addAvp(Avp.ORIGIN_HOST, "advantage.siemens.com", false);
	msg.getAvps().addAvp(Avp.ORIGIN_REALM, "siemens.com", false);
	msg.getAvps().addAvp(Avp.AUTH_APPLICATION_ID, DEF_APP_ID);
	msg.getAvps().addAvp(Avp.VENDOR_ID, SOME_VENDOR);
	msg.getAvps().addAvp(Avp.PRODUCT_NAME, "@vantage", false);
	return msg;
    }

}
