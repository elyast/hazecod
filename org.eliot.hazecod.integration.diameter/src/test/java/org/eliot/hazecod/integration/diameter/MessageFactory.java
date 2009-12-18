package org.eliot.hazecod.integration.diameter;

import java.util.ArrayList;
import java.util.List;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.XMLConfiguration;

/**
 * @author Eliot
 *
 */
public final class MessageFactory {
    
    private static final int CCR_CCA = 272;
    static final long VENDOR = 193L;
    static final long DEF_APP_ID = 4;
    static final long GPP = 10415L;
    
    private MessageFactory() {
    }    

    public static Request createCER() throws Exception {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	Configuration config = new XMLConfiguration(cl.getResourceAsStream("configuration.xml"));
	Session df = new StackImpl().init(config).getNewSession();
	Request msg = df.createRequest(CCR_CCA,
		ApplicationId.createByAuthAppId(VENDOR, DEF_APP_ID), "eliot.cca.org");
	return msg;
    }

    public static List<Object> createValue(Object data) {
	List<Object> list1 = new ArrayList<Object>();
	list1.add(data);
	return list1;
    }

    public static Message createCEA() throws Exception {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	Configuration config = new XMLConfiguration(cl.getResourceAsStream("configuration.xml"));
	Session df = new StackImpl().init(config).getNewSession();
	Message msg = df.createRequest(CCR_CCA,
		ApplicationId.createByAuthAppId(VENDOR, DEF_APP_ID), "eliot.cca.org");
	msg = ((Request)msg).createAnswer(ResultCode.SUCCESS);
	return msg;
    }

}
