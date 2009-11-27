package org.robotframework.jdiameter;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiameterRobotCodec {

    private static Logger logger = LoggerFactory
	    .getLogger(DiameterRobotCodec.class);

    DiameterMessageBuilder builder;
    TemplateBuilder transformer;
    GlobalDefaults globals;
    AvpTypeResolver avptypeResolver;
    Session session;
    Request request;

    public long decodeTimeout(Object[] parameters) {
	if (parameters.length < 3) {
	    return globals.getDefaultTimeout();
	}
	return Long.parseLong(String.valueOf(parameters[2]));
    }

    public Object encodeMessage(Object[] params) {
	builder.session = session;
	builder.request = request;
	int e2eid = globals.getDefaultEndToEndId();
	int appId = globals.getDefaultApplicationId();
	int hbhid = globals.getDefaultHopByHopId();

	return builder.encode(transformer.build(params, appId, e2eid, hbhid));
    }

    public void evaluateMessage(Object exp, Object act) throws AvpDataException {
	Message expected = (Message) exp;
	Message actual = (Message) act;
	evaluate(expected, actual);
    }

    private void evaluate(Message expected, Message actual)
	    throws AvpDataException {
	assertEquals(expected.getApplicationId(), actual.getApplicationId());
	assertEquals(expected.getCommandCode(), actual.getCommandCode());
	assertEquals(expected.isError(), actual.isError());
	assertEquals(expected.isProxiable(), actual.isProxiable());
	assertEquals(expected.isRequest(), actual.isRequest());
	assertEquals(expected.isReTransmitted(), actual.isReTransmitted());
	AvpSet allAvps = actual.getAvps();
	logger.info("Actual message: " + allAvps);
	evaluate(expected.getAvps(), allAvps);
    }

    private void evaluate(AvpSet expected, AvpSet actual)
	    throws AvpDataException {

	for (Avp avp : expected) {
	    evaluate(avp, findInActual(avp, actual));
	}
    }

    private void evaluate(Avp expected, Avp actual) throws AvpDataException {
	if (actual == null) {
	    assertEquals(expected, actual);
	}
	assertEquals(expected.getCode(), actual.getCode());
	assertEquals(expected.getVendorId(), actual.getVendorId());

	evaluateValue(expected, actual);

    }

    private void evaluateValue(Avp expected, Avp actual)
	    throws AvpDataException {
	DataType type = getType(expected.getCode());
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
	    assertEquals(expected.getOctetString(), actual.getOctetString());
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
	    throw new AvpDataException("Not handled data type");
	}
    }

    private DataType getType(int code) {
	return avptypeResolver.getType(code);
    }

    private Avp findInActual(Avp avp, AvpSet ac) {
	for (Avp avp2 : ac) {
	    if (avp2.getCode() == avp.getCode()) {
		return avp2;
	    }
	}
	return null;
    }

    private void assertEquals(Object expected, Object actual) {
	if (expected == actual) {
	    return;
	}
	if ((expected != null && !expected.equals(actual))) {
	    throw new RuntimeException("expected different that actual : "
		    + "expected: " + expected + ", actual: " + actual);

	}
    }

    public void setBuilder(DiameterMessageBuilder builder) {
	this.builder = builder;
    }

    public void setGlobals(GlobalDefaults globals) {
	this.globals = globals;
    }

    public void setTransformer(TemplateBuilder transformer) {
	this.transformer = transformer;
    }
    
    public void setAvptypeResolver(AvpTypeResolver avptypeResolver) {
	this.avptypeResolver = avptypeResolver;
    }

}
