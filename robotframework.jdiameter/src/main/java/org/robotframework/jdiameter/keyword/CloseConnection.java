package org.robotframework.jdiameter.keyword;

import org.robotframework.springdoc.EnhancedDocumentedKeyword;


public class CloseConnection implements EnhancedDocumentedKeyword {

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
	return "Close currently opened connection.";
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
