package org.eliot.hazecod.workflow.cca.internal;

import java.util.Date;

import org.eliot.hazecod.billing.BillableEvent;
import org.eliot.hazecod.billing.BillableEvent.BillType;
import org.eliot.hazecod.billing.Unit.UnitType;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;

/**
 * @author Eliot
 * 
 */
public class DiameterMessageToBilliableEventConverter {

    /**
     * @author Eliot
     * 
     */
    public enum CCRequestType {
	INITIAL_REQUEST(1), 
	UPDATE_REQUEST(2), 
	TERMINATION_REQUEST(3), 
	EVENT_REQUEST(4);

	static final int TERMINATION_ID = 3;
	static final int EVENT_ID = 4;
	int id;

	CCRequestType(int ordinal) {
	    id = ordinal;
	}

	/**
	 * @return Id of element
	 */
	public int id() {
	    return id;
	}

	/**
	 * @param requestType
	 *            RequestType ID
	 * @return classified enum
	 */
	public static CCRequestType valueOf(int requestType) {
	    switch (requestType) {
	    case 1:
		return INITIAL_REQUEST;
	    case 2:
		return UPDATE_REQUEST;
	    case TERMINATION_ID:
		return TERMINATION_REQUEST;
	    case EVENT_ID:
		return EVENT_REQUEST;
	    default:
		throw new IllegalArgumentException("Not valid type: "
			+ requestType);
	    }
	}
    }

    static final int SERVICE_CONTEXT_ID = 461;
    static final int CC_REQUEST_TYPE = 416;
    static final int CC_REQUEST_NUMBER = 415;
    static final int EVENT_TIMESTAMP = Avp.EVENT_TIMESTAMP;
    static final int USED_SERVICE_UNIT = 446;
    static final int TARIFF_CHANGE_USAGE = 452;
    static final int CC_TIME = 420;
    static final int CC_MONEY = 413;
    static final int UNIT_VALUE = 445;
    static final int CURRENCY_CODE = 425;
    static final int CC_TOTAL_OCTETS = 421;
    static final int CC_INPUT_OCTETS = 412;
    static final int CC_OUTPUT_OCTETS = 414;
    static final int CC_SERVICE_SPECIFIC_UNITS = 417;

    /**
     * @param event BillableEvent
     * @param request
     *            DiameterRequest
     */
    public void fillBillableEvent(BillableEvent event, 
	    Request request) {
	try {
	    AvpSet avps = request.getAvps();
	    String serviceContextId = getServiceContextId(avps);
	    Date eventTimestamp = getEventTimeStamp(avps);
	    CCRequestType requestType = getRequestType(avps);
	    //long requestNo = getRequestNo(avps);
	    BillType billType = convert(requestType);
	    event.setContext(serviceContextId);
	    event.setEventTimestamp(eventTimestamp);
	    event.setType(billType);
	    addUsedUnits(event, avps);

	} catch (AvpDataException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    void addUsedUnits(BillableEvent event, AvpSet avps) 
    	throws AvpDataException {
	if (!event.getType().isChargeable()) {
	    return;
	}
	AvpSet usedUnitsAvps = avps.getAvps(USED_SERVICE_UNIT);
	for (Avp avp : usedUnitsAvps) {
	    AvpSet usedUnitAvp = avp.getGrouped();
	    Avp unitAvp = usedUnitAvp.getAvpByIndex(0);
	    Object unitValue = getUnitValue(unitAvp);
	    UnitType unitType = getUnitType(unitAvp);
	    event.addUsedUnit(unitType, unitValue);
	}
    }

    UnitType getUnitType(Avp unitAvp) {
	int code = unitAvp.getCode();
	switch(code) {
	case CC_TIME : return UnitType.TIME;
	case CC_SERVICE_SPECIFIC_UNITS : return UnitType.SPECIFIC_UNIT;
	default : throw new UnsupportedOperationException("Avp code: " + code);
	}
    }

    Object getUnitValue(Avp unitAvp) throws AvpDataException {
	int code = unitAvp.getCode();
	switch(code) {
	case CC_TIME : return unitAvp.getUnsigned32();
	case CC_SERVICE_SPECIFIC_UNITS : return unitAvp.getUnsigned64();
	default : throw new UnsupportedOperationException("Avp code: " + code);
	}
    }

    BillType convert(CCRequestType requestType) {
	switch (requestType) {
	case EVENT_REQUEST:
	    return BillType.CHARGE_ONLY;
	case INITIAL_REQUEST:
	    return BillType.GRANT_ONLY;
	case TERMINATION_REQUEST:
	    return BillType.CHARGE_ONLY;
	case UPDATE_REQUEST:
	    return BillType.CHARGE_GRANT;
	default:
	    throw new IllegalArgumentException();
	}
    }

    long getRequestNo(AvpSet avps) throws AvpDataException {
	Avp requestNoAvp = avps.getAvp(CC_REQUEST_NUMBER);
	return requestNoAvp.getUnsigned32();
    }

    CCRequestType getRequestType(AvpSet avps) throws AvpDataException {
	Avp requestTypeAvp = avps.getAvp(CC_REQUEST_TYPE);
	int requestType = requestTypeAvp.getInteger32();
	return CCRequestType.valueOf(requestType);
    }

    Date getEventTimeStamp(AvpSet avps) throws AvpDataException {
	Avp eventTimeStampAvp = avps.getAvp(EVENT_TIMESTAMP);
	return eventTimeStampAvp.getTime();
    }

    String getServiceContextId(AvpSet avps) throws AvpDataException {
	Avp serviceContextAvp = avps.getAvp(SERVICE_CONTEXT_ID);
	return serviceContextAvp.getUTF8String();
    }
}
