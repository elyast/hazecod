package org.eliot.hazecod.workflow.cca.internal;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.BillingEngine;
import org.eliot.hazecod.billing.BillingResult;
import org.eliot.hazecod.integration.diameter.ServiceWorkflow;
import org.eliot.hazecod.management.user.User;
import org.eliot.hazecod.management.user.UserManagementService;
import org.eliot.hazecod.session.UserSession;
import org.eliot.hazecod.session.UserSessionFactory;
import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;

/**
 * Credit Control Application workflow
 * 
 * @author Eliot
 * 
 */
public class CreditControlApplicationWorkflow implements
	ServiceWorkflow<Request, Answer> {

    static final int SUBSCRIPTION_ID = 443;
    static final int SUBSCRIPTION_DATA = 444;
    static final int SUBSCRIPTION_TYPE = 450;
    static final int CC_REQUEST_TYPE = 416;

    static final long DIAMETER_RATING_FAILED = 5031;
    static final long DIAMETER_END_USER_SERVICE_DENIED = 4010;
    static final long DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE = 4011;
    static final long DIAMETER_CREDIT_LIMIT_REACHED = 4012;
    static final long DIAMETER_USER_UNKNOWN = 5030;

    UserSessionFactory sessionFactory;
    BillingEngine billingEngine;
    UserManagementService userService;
    DiameterMessageToBilliableEventConverter converter;

    /**
     * @see org.eliot.hazecod.integration.
     *      diameter.ServiceWorkflow#handle(java.lang.Object)
     * @param request
     *            Diameter Request
     * @return Diameter Answer
     */
    public Answer handle(Request request) {
	UserSession userSession = sessionFactory.getSession(request
		.getSessionId());

	User user = userSession.getUser();
	if (user == null) {
	    user = setupUser(request, userSession);
	}

	BillableEvent billableEvent = billingEngine
		.createEmptyBillingEvent(user);
	converter.fillBillableEvent(billableEvent, request);
	BillingResult billingResult = billingEngine.process(billableEvent);

	boolean isSuccess = billingResult.isSucceded();
	long resultCode = ResultCode.SUCCESS;
	if (!isSuccess) {
	    resultCode = DIAMETER_RATING_FAILED;
	}
	Answer answer = request.createAnswer(resultCode);
	return answer;
    }

    User setupUser(Request request, UserSession userSession) {
	try {
	    AvpSet avps = request.getAvps();
	    AvpSet subscriptionId = avps.getAvp(SUBSCRIPTION_ID).getGrouped();

	    String userId = subscriptionId.getAvp(SUBSCRIPTION_DATA)
		    .getUTF8String();
	    int userIdType = subscriptionId.getAvp(SUBSCRIPTION_TYPE)
		    .getInteger32();
	    User user = userService.getUser(userId, String.valueOf(userIdType));
	    userSession.setUser(user);
	    return user;
	} catch (AvpDataException e) {
	    throw new IllegalArgumentException(e);
	}
    }
    
    /**
     * @param billingEngine BillingEngine
     */
    public void setBillingEngine(BillingEngine billingEngine) {
	this.billingEngine = billingEngine;
    }
    
    /**
     * @param converter Converter
     */
    public void setConverter(
	    DiameterMessageToBilliableEventConverter converter) {
	this.converter = converter;
    }
    
    /**
     * @param sessionFactory SessionFactory
     */
    public void setSessionFactory(UserSessionFactory sessionFactory) {
	this.sessionFactory = sessionFactory;
    }
    
    /**
     * @param userService User Service
     */
    public void setUserService(UserManagementService userService) {
	this.userService = userService;
    }

}
