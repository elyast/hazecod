package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to open connection to System Under Test
 */
public class OpenConnection implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Open connection with server.";
    private static final String TIMEOUT = "timeout";
    private static final String PORT = "port";
    private static final String HOST = "host";
    private static final String[] ARGUMENTS = { HOST, PORT, TIMEOUT };

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
	return ARGUMENTS;
    }

    @Override
    public Object execute(Object[] arguments) {
	return JDiameterClient.getInstance().openConnection(arguments);
    }
}
