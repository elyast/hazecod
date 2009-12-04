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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes parameters of keywords into diameter message
 * 
 * @author Eliot
 * 
 */
public class DiameterMessageEncoder implements TemplateProcessor {

    private static final String CONFIGURATION_XML = "configuration.xml";

    private static Logger logger = LoggerFactory
	    .getLogger(DiameterMessageEncoder.class);

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
     * encodes Message with given parameters
     * 
     * @param params
     *            parameters table
     * @return
     */
    public Message encodeMessage(String template, String[] params) {
	builder.session = session;
	builder.setLastRequest(lastRequest);
	int appId = globals.getDefaultApplicationId();

	return builder.encode(transformer.build(template, params, appId));
    }

    /**
     * evaluates if expected Message is equal to received one
     * 
     * @param exp
     *            expected Message
     * @param act
     *            received Message
     * @throws AvpDataException
     */
    public void evaluateMessage(Message expected, Message actual)
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

    /**
     * Sets diameter message builder
     * 
     * @param builder
     */
    public void setBuilder(DiameterMessageBuilder builder) {
	this.builder = builder;
    }

    /**
     * Sets global defaults
     * 
     * @param globals
     */
    public void setGlobals(GlobalDefaults globals) {
	this.globals = globals;
    }

    /**
     * Sets template builder
     * 
     * @param transformer
     */
    public void setTransformer(TemplateBuilder transformer) {
	this.transformer = transformer;
    }

    /**
     * Sets data type resolver
     * 
     * @param avptypeResolver
     */
    public void setAvptypeResolver(AvpTypeResolver avptypeResolver) {
	this.avptypeResolver = avptypeResolver;
    }

    /**
     * Loads JDiameter client configuration from file
     * 
     * @param parameters
     * @return Configuration object
     * @throws Exception
     */
    public XMLConfiguration decodeConfiguration(String configuration)
	    throws Exception {
	if (configuration.isEmpty()) {
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    InputStream istream = cl.getResourceAsStream(CONFIGURATION_XML);
	    return new XMLConfiguration(istream);
	}
	return new XMLConfiguration(configuration);
    }

    public void setLastRequest(Request lastRequest) {
	this.lastRequest = lastRequest;
    }

    public void setSession(Session session) {
	this.session = session;
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

    @Override
    public Document processTemplate(String template, String[] parameters) {
	int defaultApplicationId = globals.getDefaultApplicationId();
	transformer.build(template, parameters, defaultApplicationId);
	return null;
    }

}
