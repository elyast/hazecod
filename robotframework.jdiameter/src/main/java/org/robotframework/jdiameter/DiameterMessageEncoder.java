package org.robotframework.jdiameter;

import java.io.InputStream;

import nu.xom.Document;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.robotframework.jdiameter.mapper.AvpTypeResolver;
import org.robotframework.jdiameter.mapper.GlobalDefaults;

/**
 * Encodes parameters of keywords into diameter message
 * 
 * @author Eliot
 *
 */
public class DiameterMessageEncoder {

    /**
     * Builds diameter message from xml tree
     */
    DiameterMessageBuilder builder;
    /**
     * Applies user parameters to choosen template
     */
    TemplateBuilder transformer;
    
    /**
     * Retrieves global defaults for parameters 
     */
    GlobalDefaults globals;
    /**
     * Gets avp data type depending on avp code
     */
    AvpTypeResolver avptypeResolver;
    /**
     * Current JDiameter client session
     */
    Session session;
    /**
     * Last JDiameter client request
     */
    Request lastRequest;

    /**
     * Encodes into diameter message: user parameter and choosen template
     * @param params template, and name, value pairs
     * @return Diameter Message
     */
    public Object encodeMessage(Object[] params) {
	builder.setSession(session);
	builder.setLastRequest(lastRequest);
	int appId = globals.getDefaultApplicationId();

	Document xmlTemplate = transformer.build(params, appId);
	return builder.encode(xmlTemplate);
    }

    /**
     * Asserts expected and actual diameter messages
     * @param exp expected
     * @param act actual
     * @throws AvpDataException
     */
    public void evaluateMessage(Object exp, Object act) throws AvpDataException {
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
     * Asserts avp sets
     * @param expected
     * @param actual
     * @throws AvpDataException
     */
    private void evaluate(AvpSet expected, AvpSet actual)
	    throws AvpDataException {
	for (Avp exp : expected) {
	    evaluate(exp, actual.getAvp(exp.getCode()));
	}
    }

    /**
     * Asserts two avp
     * @param expected
     * @param actual
     * @throws AvpDataException
     */
    private void evaluate(Avp expected, Avp actual) throws AvpDataException {
	if (actual == null) {
	    assertEquals(expected, actual);
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
    private void evaluateValue(Avp expected, Avp actual)
	    throws AvpDataException {
	DataType type = avptypeResolver.getType(expected.getCode());
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

    /**
     * Asserts simple type values
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
    
    /**
     * Loads JDiameter client configuration from file
     * @param parameters
     * @return Configuration object
     * @throws Exception
     */
    public XMLConfiguration decodeConfiguration(Object[] parameters) throws Exception {
	if (parameters.length < 1) {
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    InputStream istream = cl.getResourceAsStream("configuration.xml");
	    return new XMLConfiguration(istream);
	}
	return new XMLConfiguration((String) parameters[0]);
    }

    /**
     * Sets diameter message builder
     * @param builder
     */
    public void setBuilder(DiameterMessageBuilder builder) {
	this.builder = builder;
    }

    /**
     * Sets global defaults
     * @param globals
     */
    public void setGlobals(GlobalDefaults globals) {
	this.globals = globals;
    }

    /**
     * Sets template builder
     * @param transformer
     */
    public void setTransformer(TemplateBuilder transformer) {
	this.transformer = transformer;
    }
    
    /**
     * Sets data type resolver
     * @param avptypeResolver
     */
    public void setAvptypeResolver(AvpTypeResolver avptypeResolver) {
	this.avptypeResolver = avptypeResolver;
    }
    
    /**
     * Sets current request
     * @param request
     */
    public void setLastRequest(Request request) {
	this.lastRequest = request;
    }
    
    /**
     * Sets current session
     * @param session
     */
    public void setSession(Session session) {
	this.session = session;
    }



}
