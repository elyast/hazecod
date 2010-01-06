package org.robotframework.jdiameter;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.robotframework.jdiameter.mapper.AvpPrinter;
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

    AvpPrinter printer;

    /**
     * Asserts expected in comparison with actual message
     * 
     * @param exp
     *            Expected message
     * @param act
     *            Actual message
     */
    @Override
    public void evaluateMessage(Object exp, Object act) {
	try {
	    Message expected = (Message) exp;
	    Message actual = (Message) act;
	    assertEquals(expected.getApplicationId(), 
		    actual.getApplicationId());
	    assertEquals(expected.getCommandCode(), actual.getCommandCode());
	    assertEquals(expected.isError(), actual.isError());
	    assertEquals(expected.isProxiable(), actual.isProxiable());
	    assertEquals(expected.isRequest(), actual.isRequest());
	    assertEquals(expected.isReTransmitted(), actual.isReTransmitted());
	    AvpSet allAvps = actual.getAvps();
	    evaluate(expected.getAvps(), allAvps);
	} catch (AvpDataException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    /**
     * Sets data type resolver
     * 
     * @param avptypeResolver
     *            AVP data type resolver
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
    private void evaluate(AvpSet expected, AvpSet actual)
	    throws AvpDataException {
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
    private void evaluate(Avp expected, Avp actual) throws AvpDataException {
	if (actual == null && expected != null) {
	    throw new RuntimeException("expected different that actual : "
		    + printer.prettyPrint(expected) + ", actual: " + actual);
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
	try {
	    DataType type = avptypeResolver.getType(expected.getCode());
	    if (!DataType.GROUPED.equals(type)) {
		Object expectedValue = avptypeResolver.getValue(expected);
		Object actualValue = avptypeResolver.getValue(actual);
		assertEquals("Comparison of AVPs: expected["
			+ printer.prettyPrint(expected) + "] actual["
			+ printer.prettyPrint(actual) + "]", expectedValue,
			actualValue);
		return;
	    }

	    evaluate(expected.getGrouped(), actual.getGrouped());
	} catch (AvpDataException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    /**
     * Asserts simple type values
     * 
     * @param message
     * @param expected
     * @param actual
     */
    private void assertEquals(String message, Object expected, Object actual) {
	if (expected == actual) {
	    return;
	}
	if ((expected != null && !expected.equals(actual))) {
	    throw new RuntimeException(message
		    + " expected different that actual : " + "expected: "
		    + expected + ", actual: " + actual);
	}
    }

    /**
     * Asserts simple type values
     * 
     * @param expected
     * @param actual
     */
    private void assertEquals(Object expected, Object actual) {
	assertEquals("", expected, actual);
    }

    /**
     * @param printer
     *            AvpPrinter
     */
    public void setPrinter(AvpPrinter printer) {
	this.printer = printer;
    }
}
