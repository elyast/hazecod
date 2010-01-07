package org.eliot.hazecod.integration.diameter.internal;

import org.eliot.hazecod.integration.diameter.ServiceBroker;
import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eliot
 * 
 */
public class DiameterServiceBroker implements ServiceBroker {

    static Logger logger = LoggerFactory.getLogger(DiameterServiceBroker.class);
    static final int EVENT_REQUEST = 4;
    static final int CC_REQUEST_NO = 415;
    static final int CC_REQUEST_TYPE = 416;

    /**
     * @param parameter
     *            Diameter Request
     * @return Diameter Answer
     */
    @Override
    public Object handle(Object parameter) {
	Request request = (Request) parameter;
	logger.error("DiameterServiceBroker - fatal error");
	logger.info("DiameterServiceBroker - fatal error");
	Answer answer = request.createAnswer(ResultCode.SUCCESS);
	AvpSet avpSet = answer.getAvps();
	avpSet.addAvp(CC_REQUEST_TYPE, EVENT_REQUEST);
	avpSet.addAvp(CC_REQUEST_NO, 0L, 0L, true, false, true);
	return answer;
    }

}
