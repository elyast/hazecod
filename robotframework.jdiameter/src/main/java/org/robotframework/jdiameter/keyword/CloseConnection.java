package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.Client;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to close connection to System Under Test
 */
public class CloseConnection implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Close currently opened connection.";

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

    public void setClient(Client client) {
        this.client = client;
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
	client.closeConnection();
	return null;
    }
}
