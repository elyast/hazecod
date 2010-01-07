package org.robotframework.jdiameter.keyword;

import org.robotframework.javalib.keyword.EnhancedDocumentedKeyword;
import org.robotframework.protocol.Client;

/**
 * RobotFramework keyword used to close connection to System Under Test
 */
public class CloseConnection implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = 
	"Close currently opened connection.";

    String name;
    Client client;

    /**
     * @return keyword name
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * @param name keyword name
     */
    @Override
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @param client Protocol client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return Description
     */
    @Override
    public String getDocumentation() {
	return DOCUMENTATION;
    }

    /**
     * @return Arguments
     */
    @Override
    public String[] getArgumentNames() {
	return new String[] {};
    }

    /**
     * @param arguments test parameters
     * @return null
     */
    @Override
    public Object execute(Object[] arguments) {
	client.closeConnection();
	return null;
    }
}
