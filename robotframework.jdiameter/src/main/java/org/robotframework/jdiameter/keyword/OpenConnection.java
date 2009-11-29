package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to open connection to System Under Test
 */
public class OpenConnection implements EnhancedDocumentedKeyword {

    static final int DEFAULT_TIMEOUT = -1;

    static final String DEFAULT_CONFIGURATION = "";

    private static final String DOCUMENTATION = "Open connection with server.";

    private enum Argument {
	CONFIGURATION, TIMEOUT;

	public static String[] getArgumentNames() {
	    String[] argumentNames = new String[Argument.values().length];
	    for (int i = 0; i < Argument.values().length; i++) {
		argumentNames[i] = Argument.values()[i].name();
	    }
	    return argumentNames;
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

    public String getDocumentation() {
	return DOCUMENTATION;
    }

    @Override
    public String[] getArgumentNames() {
	return Argument.getArgumentNames();
    }

    @Override
    public Object execute(Object[] arguments) {
	String configuration = getConfiguration(arguments);
	long timeout = getTimeout(arguments);
	return JDiameterClient.getInstance().openConnection(configuration,
		timeout);
    }

    /**
     * retrieves timeout value from arguments table
     * 
     * @param arguments
     * @return
     */
    public long getTimeout(Object[] arguments) {
	int timeoutIndex = Argument.TIMEOUT.ordinal();
	if (arguments.length < timeoutIndex + 1) {
	    // TODO how it should get default timeout
	    return DEFAULT_TIMEOUT;
	}
	return Long.parseLong(String.valueOf(arguments[timeoutIndex]));
    }

    public String getConfiguration(Object[] arguments) {
	int configurationIndex = Argument.CONFIGURATION.ordinal();
	if (arguments.length < Argument.CONFIGURATION.ordinal() + 1) {
	    // TODO how it should get default configuration ??
	    return DEFAULT_CONFIGURATION;
	}
	return (String) arguments[configurationIndex];
    }

}
