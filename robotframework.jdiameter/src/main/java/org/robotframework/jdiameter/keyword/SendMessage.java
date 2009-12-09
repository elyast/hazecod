package org.robotframework.jdiameter.keyword;

import java.util.Arrays;

import nu.xom.Document;

import org.robotframework.javalib.keyword.EnhancedDocumentedKeyword;
import org.robotframework.protocol.Client;
import org.robotframework.protocol.ProtocolCodec;
import org.robotframework.protocol.TemplateProcessor;

/**
 * RobotFramework keyword used to send messages from System Under Test.
 */
public class SendMessage implements EnhancedDocumentedKeyword {

    static final String DOCUMENTATION = "Send message with given arguments.\n"
	    + "Arguments are in format: avp_name=value, where putting AVP "
	    + "value will overwrite existing from template (if defined)\n"
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
     * @return Description
     */
    @Override
    public String getDocumentation() {
	return DOCUMENTATION;
    }

    /**
     * @param client Protocol client
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
     * @param protocolCodec Message codec
     */
    public void setProtocolCodec(ProtocolCodec protocolCodec) {
	this.protocolCodec = protocolCodec;
    }

    /**
     * @param arguments test param
     * @return null
     */
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
