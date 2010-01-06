package org.eliot.hazecod.integration.diameter.internal;

import org.eliot.hazecod.integration.diameter.ServiceBroker;
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
    /**
     * @param parameter Diameter Request
     * @return Diameter Answer
     */
    @Override
    public Object handle(Object parameter) {
	Request request = (Request) parameter;
	logger.error("DiameterServiceBroker - fatal error");
	return request.createAnswer(ResultCode.SUCCESS);
    }

}
