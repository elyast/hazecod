package org.eliot.hazecod.workflow.cca.internal;

import static org.eliot.hazecod.workflow.cca.internal.DiameterMessageToBilliableEventConverter.CC_REQUEST_TYPE;
import static org.eliot.hazecod.workflow.cca.internal.DiameterMessageToBilliableEventConverter.CC_TIME;
import static org.eliot.hazecod.workflow.cca.internal.DiameterMessageToBilliableEventConverter.SERVICE_CONTEXT_ID;
import static org.eliot.hazecod.workflow.cca.internal.DiameterMessageToBilliableEventConverter.USED_SERVICE_UNIT;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrict;
import mockit.integration.junit4.JMockit;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.BillableEvent.BillType;
import org.eliot.hazecod.billing.Unit.UnitType;
import org.eliot.hazecod.workflow.cca.internal.DiameterMessageToBilliableEventConverter.CCRequestType;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class DiameterMessageToBilliableEventConverterTest {

    private DiameterMessageToBilliableEventConverter testObj;
    
    @Mocked BillableEvent event;
    @NonStrict Request request;
    @NonStrict AvpSet avpSet;
    @NonStrict Avp serviceContextAvp;
    @NonStrict Avp requestTypeAvp;
    @NonStrict Avp eventTimeAvp;
    @NonStrict AvpSet usedUnitsAvps;
    @NonStrict Avp usedUnitAvpGroup;
    @NonStrict AvpSet usedUnitsAvp;
    @Mocked Iterator<Avp> iteratorUsedUnitsAvps;
    @NonStrict Avp timeAvp;
    
    @Before
    public void setUp() throws Exception {
	testObj = new DiameterMessageToBilliableEventConverter();
    }

    @Test
    public void testfillBillableEvent() throws Exception {
	new Expectations() {{	    
	    request.getAvps();
	    returns(avpSet);
	    avpSet.getAvp(SERVICE_CONTEXT_ID);
	    returns(serviceContextAvp);
	    avpSet.getAvp(CC_REQUEST_TYPE);
	    returns(requestTypeAvp);
	    requestTypeAvp.getInteger32();
	    returns(CCRequestType.EVENT_REQUEST.id());
	    avpSet.getAvp(Avp.EVENT_TIMESTAMP);
	    returns(eventTimeAvp);
	    avpSet.getAvps(USED_SERVICE_UNIT);
	    returns(usedUnitsAvps);

	    event.setContext(null);
	    event.setEventTimestamp(null);
	    event.setType(BillType.CHARGE_ONLY);
	    event.getType();
	    returns(BillType.CHARGE_ONLY);

	    usedUnitsAvps.iterator();
	    returns(iteratorUsedUnitsAvps);
	    iteratorUsedUnitsAvps.hasNext();
	    returns(true);
	    iteratorUsedUnitsAvps.next();	    
	    returns(usedUnitAvpGroup);
	    usedUnitAvpGroup.getGrouped();
	    returns(usedUnitsAvp);
	    usedUnitsAvp.getAvpByIndex(0);
	    returns(timeAvp);
	    timeAvp.getCode();
	    returns(CC_TIME);
	    
	    event.addUsedUnit(UnitType.TIME, 0L);
	    iteratorUsedUnitsAvps.hasNext();
	    returns(false);

	}};
	testObj.fillBillableEvent(event, request);
    }
    
    @Test
    public void testConvert_Event() throws Exception {
	BillType billType = testObj.convert(CCRequestType.EVENT_REQUEST);
	assertEquals(BillType.CHARGE_ONLY, billType);
    }
    
    @Test
    public void testConvert_Initial() throws Exception {
	BillType billType = testObj.convert(CCRequestType.INITIAL_REQUEST);
	assertEquals(BillType.GRANT_ONLY, billType);
    }
    
    @Test
    public void testConvert_Termination() throws Exception {
	BillType billType = testObj.convert(CCRequestType.TERMINATION_REQUEST);
	assertEquals(BillType.CHARGE_ONLY, billType);
    }
    
    @Test
    public void testConvert_Update() throws Exception {
	BillType billType = testObj.convert(CCRequestType.UPDATE_REQUEST);
	assertEquals(BillType.CHARGE_GRANT, billType);
    }    

    @Test(expected = IllegalArgumentException.class)
    public void testCCRequestValueOf_NotValid() throws Exception {
	CCRequestType.valueOf(0);
    }
    
    @Test
    public void testCCRequestValueOf_1() throws Exception {
	assertEquals(CCRequestType.INITIAL_REQUEST, CCRequestType.valueOf(1));
    }
    
    @Test
    public void testCCRequestValueOf_2() throws Exception {
	assertEquals(CCRequestType.UPDATE_REQUEST, CCRequestType.valueOf(2));
    }
    @Test
    public void testCCRequestValueOf_3() throws Exception {
	assertEquals(CCRequestType.TERMINATION_REQUEST, CCRequestType.valueOf(3));
    }    
    @Test
    public void testCCRequestValueOf_4() throws Exception {
	assertEquals(CCRequestType.EVENT_REQUEST, CCRequestType.valueOf(4));
    }      
}
