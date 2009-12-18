package org.eliot.hazecod.integration.diameter.internal;

import org.eliot.hazecod.integration.diameter.ServiceBroker;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;

/**
 * @author Eliot
 *
 */
public class DiameterServiceBroker implements ServiceBroker {

    /**
     * @param parameter Diameter Request
     * @return Diameter Answer
     */
    @Override
    public Object handle(Object parameter) {
	Request request = (Request) parameter;
	return request.createAnswer(ResultCode.SUCCESS);
    }

}
