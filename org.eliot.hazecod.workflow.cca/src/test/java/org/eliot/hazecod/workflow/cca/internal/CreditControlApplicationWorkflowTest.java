package org.eliot.hazecod.workflow.cca.internal;

import static org.junit.Assert.*;
import static org.eliot.hazecod.workflow.cca.internal.CreditControlApplicationWorkflow.*;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.BillingEngine;
import org.eliot.hazecod.billing.BillingResult;
import org.eliot.hazecod.management.user.User;
import org.eliot.hazecod.management.user.UserManagementService;
import org.eliot.hazecod.session.UserSession;
import org.eliot.hazecod.session.UserSessionFactory;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class CreditControlApplicationWorkflowTest {

    private CreditControlApplicationWorkflow testObj;
        
    @Mocked Request request;
    @Mocked UserSession userSession;
    @Mocked AvpSet avps;
    @Mocked Avp subscriptionIdAvp;
    @Mocked Avp subscriptionData;
    @Mocked Avp subscriptionType;
    @Mocked AvpSet subscriptionDataAvp;
    
    @Mocked UserManagementService userService;
    @Mocked UserSessionFactory sessionFactory;
    @Mocked BillingEngine billing;
    @Mocked DiameterMessageToBilliableEventConverter converter;
    @Mocked User user;
    @Mocked BillableEvent billableEvent;
    @Mocked BillingResult billingResult;
    @Mocked Answer answer;
    
    @Before
    public void setUp() throws Exception {
	testObj = new CreditControlApplicationWorkflow();
	testObj.userService = userService;
	testObj.sessionFactory = sessionFactory;
	testObj.billingEngine = billing;
	testObj.converter = converter;
    }

    @Test
    public void testHandle_BillingOK() {
	new Expectations() {{
	    request.getSessionId();
	    returns("xx");
	    sessionFactory.getSession("xx");
	    returns(userSession);
	    userSession.getUser();
	    returns(user);
	    billing.createEmptyBillingEvent(user);
	    returns(billableEvent);
	    billing.process(billableEvent);
	    returns(billingResult);
	    billingResult.isSucceded();
	    returns(true);
	    request.createAnswer(ResultCode.SUCCESS);
	    returns(answer);
	}};
	Answer answer = testObj.handle(request);
	assertNotNull(answer);
    }
    
    @Test
    public void testHandle_BillingNOK() {
	new Expectations() {{
	    request.getSessionId();
	    returns("xx");
	    sessionFactory.getSession("xx");
	    returns(userSession);
	    userSession.getUser();
	    returns(user);
	    billing.createEmptyBillingEvent(user);
	    returns(billableEvent);
	    billing.process(billableEvent);
	    returns(billingResult);
	    billingResult.isSucceded();
	    returns(false);
	    request.createAnswer(DIAMETER_RATING_FAILED);
	    returns(answer);
	}};
	Answer answer = testObj.handle(request);
	assertNotNull(answer);
    }

    @Test
    public void testSetupUser() throws AvpDataException {
	
	new Expectations() {{
	    request.getAvps();
	    returns(avps);
	    avps.getAvp(SUBSCRIPTION_ID);
	    returns(subscriptionIdAvp);
	    subscriptionIdAvp.getGrouped();
	    returns(subscriptionDataAvp);
	    subscriptionDataAvp.getAvp(SUBSCRIPTION_DATA);
	    returns(subscriptionData);
	    subscriptionData.getUTF8String();
	    returns("xx");
	    subscriptionDataAvp.getAvp(SUBSCRIPTION_TYPE);
	    returns(subscriptionType);
	    subscriptionType.getInteger32();
	    returns(1);
	    userService.getUser("xx", "1");
	    returns(user);
	    userSession.setUser(user);
	}};
	User user = testObj.setupUser(request, userSession);
	assertNotNull(user);
    }

}
