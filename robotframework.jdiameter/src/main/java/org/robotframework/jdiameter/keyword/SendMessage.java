package org.robotframework.jdiameter.keyword;

import java.util.Arrays;

import nu.xom.Document;

import org.robotframework.protocol.Client;
import org.robotframework.protocol.ProtocolCodec;
import org.robotframework.protocol.TemplateProcessor;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to send messages from System Under Test.
 */
public class SendMessage implements EnhancedDocumentedKeyword {

    static final String DOCUMENTATION = "Send message with given arguments.\n"
	    + "Arguments are in format: avp_name=value, where putting AVP value will overwrite existing from template (if defined)\n"
	    + "AVP names are describes in xml templates.\n"
	    + "templateName is without *.xml extension";

    enum Argument {
	TEMPLATE_NAME("templateName"), AVPS_WITH_VALUES_TO_BE_OVERWRITTEN(
		"*avps_with_values_to_be_overwritten");

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

    public void setClient(Client client) {
	this.client = client;
    }

    public void setTemplateProcessor(TemplateProcessor templateProcessor) {
	this.templateProcessor = templateProcessor;
    }

    public void setProtocolCodec(ProtocolCodec protocolCodec) {
	this.protocolCodec = protocolCodec;
    }

    @Override
    public Object execute(Object[] arguments) {
	if (arguments.length == 0) {
	    throw new RuntimeException("Template argument missing");
	}
	String template = (String) arguments[Argument.TEMPLATE_NAME.ordinal()];
	String[] avps = Arrays.copyOfRange(arguments,
		Argument.AVPS_WITH_VALUES_TO_BE_OVERWRITTEN.ordinal(),
		arguments.length, String[].class);
	Document xmlDocument = templateProcessor
		.processTemplate(template, avps);
	protocolCodec.setSesssion(client.getSession());
	Object message = protocolCodec.encode(xmlDocument);
	client.sendMessage(message);
	return null;
    }
}
