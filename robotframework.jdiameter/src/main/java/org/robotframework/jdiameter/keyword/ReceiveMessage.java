package org.robotframework.jdiameter.keyword;

import java.util.Arrays;

import nu.xom.Document;

import org.robotframework.jdiameter.Client;
import org.robotframework.jdiameter.MessageComparator;
import org.robotframework.jdiameter.ProtocolCodec;
import org.robotframework.jdiameter.TemplateProcessor;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to receive messages from System Under Test
 */
public class ReceiveMessage implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Waits for message and check if given arguments equal received arguments.\n"
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
    MessageComparator msgComparator;

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String[] getArgumentNames() {
	return Argument.getArgumentNames();
    }

    @Override
    public String getDocumentation() {
	return DOCUMENTATION;
    }

    @Override
    public Object execute(Object[] arguments) {
	String template = (String) arguments[Argument.TEMPLATE_NAME.ordinal()];
	String[] avps = Arrays.copyOfRange(arguments,
		Argument.AVPS_WITH_EXPECTED_VALUES.ordinal(), arguments.length,
		String[].class);
	Document xmlDocument = templateProcessor
		.processTemplate(template, avps);
	Object expectedMsg = protocolCodec.encode(xmlDocument);
	Object receivedMsg = client.receiveMessage();
	msgComparator.evaluateMessage(expectedMsg, receivedMsg);
	return null;
    }

    public void setClient(Client client) {
	this.client = client;
    }

    public void setTemplateProcessor(TemplateProcessor templateProcessor) {
	this.templateProcessor = templateProcessor;
    }

    public void setProtocolCodec(ProtocolCodec protocolCodec) {
	this.protocolCodec = protocolCodec;
    }

    public void setMessageComparator(MessageComparator msgComparator) {
	this.msgComparator = msgComparator;
    }
}
