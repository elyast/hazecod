package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to close connection to System Under Test
 */
public class CloseConnection implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Close currently opened connection.";
    
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
    public String getDocumentation() {
	return DOCUMENTATION;
    }

    @Override
    public String[] getArgumentNames() {
	return new String[] {};
    }

    @Override
    public Object execute(Object[] arguments) {
	return JDiameterClient.getInstance().closeConnection(arguments);
    }
}
