package org.robotframework.jdiameter.keyword;

import java.util.Arrays;

import nu.xom.Document;

import org.robotframework.javalib.keyword.EnhancedDocumentedKeyword;
import org.robotframework.protocol.Client;
import org.robotframework.protocol.MessageComparator;
import org.robotframework.protocol.ProtocolCodec;
import org.robotframework.protocol.TemplateProcessor;

/**
 * RobotFramework keyword used to receive messages from System Under Test
 */
public class ReceiveMessage implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Waits for message and check "
    	    + "if given arguments equal received arguments.\n"
	    + "Arguments are in format: avp_name=expected_value\n"
	    + "AVP names are describes in xml templates.\n"
	    + "templateName is without *.xml extension";

    private enum Argument {
	TEMPLATE_NAME("templateName"), AVPS_WITH_EXPECTED_VALUES(
		"*avps_with_expected_values");

	private String argName;

	Argument(String argName) {
	    this.argName = argName;
	}

	public static String[] getArgumentNames() {
	    String[] argumentNames = new String[Argument.values().length];
	    for (int i = 0; i < Argument.values().length; i++) {
		argumentNames[i] = Argument.values()[i].getName();
	    }
	    return argumentNames;
	}

	public String getName() {
	    return argName;
	}
    }

    String name;
    Client client;
    TemplateProcessor templateProcessor;
    ProtocolCodec protocolCodec;
    MessageComparator messageComparator;

    /**
     * @return Keyword name
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * @param name Keyword name
     */
    @Override
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return Arguments
     */
    @Override
    public String[] getArgumentNames() {
	return Argument.getArgumentNames();
    }

    /**
     * @return Documentation
     */
    @Override
    public String getDocumentation() {
	return DOCUMENTATION;
    }

    /**
     * @param arguments test parameters
     * @return null
     */
    @Override
    public Object execute(Object[] arguments) {
	String template = (String) arguments[Argument.TEMPLATE_NAME.ordinal()];
	String[] avps = Arrays.copyOfRange(arguments,
		Argument.AVPS_WITH_EXPECTED_VALUES.ordinal(), arguments.length,
		String[].class);
	Document xmlDocument = templateProcessor
		.processTemplate(template, avps);
	protocolCodec.setLastRequest(client.getLastRequest());
	Object expectedMsg = protocolCodec.encode(xmlDocument);
	Object receivedMsg = client.receiveMessage();
	messageComparator.evaluateMessage(expectedMsg, receivedMsg);
	return null;
    }

    /**
     * @param client Protocol Client
     */
    public void setClient(Client client) {
	this.client = client;
    }

    /**
     * @param templateProcessor Template processor
     */
    public void setTemplateProcessor(TemplateProcessor templateProcessor) {
	this.templateProcessor = templateProcessor;
    }

    /**
     * @param protocolCodec Protocol codec
     */
    public void setProtocolCodec(ProtocolCodec protocolCodec) {
	this.protocolCodec = protocolCodec;
    }

    /**
     * @param msgComparator Message comparator
     */
    public void setMessageComparator(MessageComparator msgComparator) {
	this.messageComparator = msgComparator;
    }
}
