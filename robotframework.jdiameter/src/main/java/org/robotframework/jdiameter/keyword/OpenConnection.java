package org.robotframework.jdiameter.keyword;

import org.robotframework.javalib.keyword.EnhancedDocumentedKeyword;
import org.robotframework.protocol.Client;

/**
 * RobotFramework keyword used to open connection to System Under Test
 */
public class OpenConnection implements EnhancedDocumentedKeyword {

    static final String DEFAULT_CONFIGURATION = null;

    static final String DOCUMENTATION = "Open connection with server.";

    static final String[] ARGUMENT_NAMES = new String[]{};

    String name;
    Client client;

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
	return ARGUMENT_NAMES;
    }

    @Override
    public Object execute(Object[] arguments) {
	String configuration = getConfiguration(arguments);
	client.openConnection(configuration);
	return null;
    }
    
    public void setClient(Client client) {
	this.client = client;
    }

    String getConfiguration(Object[] arguments) {
	int configurationIndex = 0;
	if (arguments.length < 1) {
	    return DEFAULT_CONFIGURATION;
	}
	return (String) arguments[configurationIndex];
    }

    
}
