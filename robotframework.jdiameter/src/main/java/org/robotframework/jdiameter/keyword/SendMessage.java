package org.robotframework.jdiameter.keyword;

import java.util.Arrays;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to send messages from System Under Test.
 */
public class SendMessage implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Send message with given arguments.\n"
	    + "Arguments are in format: avp_name=value, where putting AVP value will overwrite existing from template (if defined)\n"
	    + "AVP names are describes in xml templates.\n"
	    + "templateName is without *.xml extension";

    private enum Argument {
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
	if (arguments.length == 0) {
	    throw new RuntimeException("Template argument missing");
	}
	String template = (String) arguments[Argument.TEMPLATE_NAME.ordinal()];
	String[] avps = Arrays.copyOfRange(arguments,
		Argument.AVPS_WITH_VALUES_TO_BE_OVERWRITTEN.ordinal(),
		arguments.length, String[].class);
	return JDiameterClient.getInstance().sendMessage(template, avps);
    }
}
