package org.eliot.hazecod.integration.diameter;

import org.eliot.hazecod.integration.diameter.ServiceWorkflow;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceUnavailableWorkflow implements ServiceWorkflow<Request, Answer> {

    static Logger logger = LoggerFactory.getLogger(ServiceUnavailableWorkflow.class);
    
    @Override
    public Answer handle(Request request) {
	return request.createAnswer(ResultCode.UNABLE_TO_COMPLY);
    }

}
