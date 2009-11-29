package org.robotframework.jdiameter.keyword;

import java.util.Arrays;

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

    private String name;

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
	return JDiameterClient.getInstance().receiveMessage(template, avps);
    }
}
