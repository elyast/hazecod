package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;


public class OpenConnection implements EnhancedDocumentedKeyword {

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
	return "Open connection with server.";
    }

    @Override
    public String[] getArgumentNames() {
	return new String[] { "host", "port", "timeout" };
    }

    @Override
    public Object execute(Object[] arguments) {
	return JDiameterClient.getInstance().openConnection(arguments);
    }
}
