package org.robotframework.jdiameter;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.robotframework.jdiameter.mapper.AvpTypeResolver;
import org.robotframework.jdiameter.mapper.DataType;
import org.robotframework.protocol.MessageComparator;

/**
 *
 */
public class DiameterMessageComparator implements MessageComparator {

    /**
     * Gets avp data type depending on avp code
     */
    AvpTypeResolver avptypeResolver;

    /**
     * Asserts expected in comparison with actual message
     * 
     * @param exp Expected message
     * @param act Actual message
     */
    @Override
    public void evaluateMessage(Object exp, Object act) {
	Message expected = (Message) exp;
	Message actual = (Message) act;
	assertEquals(expected.getApplicationId(), actual.getApplicationId());
	assertEquals(expected.getCommandCode(), actual.getCommandCode());
	assertEquals(expected.isError(), actual.isError());
	assertEquals(expected.isProxiable(), actual.isProxiable());
	assertEquals(expected.isRequest(), actual.isRequest());
	assertEquals(expected.isReTransmitted(), actual.isReTransmitted());
	AvpSet allAvps = actual.getAvps();
	evaluate(expected.getAvps(), allAvps);
    }
    
    /**
     * Sets data type resolver
     * 
     * @param avptypeResolver AVP data type resolver
     */
    public void setAvptypeResolver(AvpTypeResolver avptypeResolver) {
	this.avptypeResolver = avptypeResolver;
    }
    
    /**
     * Asserts avp sets
     * 
     * @param expected
     * @param actual
     * @throws AvpDataException
     */
    private void evaluate(AvpSet expected, AvpSet actual) {
	for (Avp avp : expected) {
	    evaluate(avp, actual.getAvp(avp.getCode()));
	}
    }

    /**
     * Asserts two avp
     * 
     * @param expected
     * @param actual
     * @throws AvpDataException
     */
    private void evaluate(Avp expected, Avp actual) {
	if (actual == null) {
	    assertEquals(expected, null);
	}
	assertEquals(expected.getCode(), actual.getCode());
	assertEquals(expected.getVendorId(), actual.getVendorId());

	evaluateValue(expected, actual);
    }

    /**
     * Checks avp values
     * 
     * @param expected
     * @param actual
     * @throws AvpDataException
     */
    private void evaluateValue(Avp expected, Avp actual) {
	DataType type = avptypeResolver.getType(expected.getCode());
	try {
	    switch (type) {
	    case INT_32:
	        assertEquals(expected.getInteger32(), actual.getInteger32());
	        break;
	    case INT_64:
	        assertEquals(expected.getInteger64(), actual.getInteger64());
	        break;
	    case FLOAT_32:
	        assertEquals(expected.getFloat32(), actual.getFloat32());
	        break;
	    case FLOAT_64:
	        assertEquals(expected.getFloat64(), actual.getFloat64());
	        break;
	    case OCTET_STRING:
	        assertEquals(expected.getOctetString(), 
	        	actual.getOctetString());
	        break;
	    case ADDRESS:
	        assertEquals(expected.getAddress(), actual.getAddress());
	        break;
	    case GROUPED:
	        evaluate(expected.getGrouped(), actual.getGrouped());
	        break;
	    case TIME:
	        assertEquals(expected.getTime(), actual.getTime());
	        break;
	    case UTF8_STRING:
	        assertEquals(expected.getUTF8String(), actual.getUTF8String());
	        break;
	    case UNSIGNED_32:
	        assertEquals(expected.getUnsigned32(), actual.getUnsigned32());
	        break;
	    case UNSIGNED_64:
	        assertEquals(expected.getUnsigned64(), actual.getUnsigned64());
	        break;
	    default:
	        throw new IllegalArgumentException("Not handled data type");
	    }
	} catch (AvpDataException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    /**
     * Asserts simple type values
     * 
     * @param expected
     * @param actual
     */
    private void assertEquals(Object expected, Object actual) {
	if (expected == actual) {
	    return;
	}
	if ((expected != null && !expected.equals(actual))) {
	    throw new RuntimeException("expected different that actual : "
		    + "expected: " + expected + ", actual: " + actual);
	}
    }

}
