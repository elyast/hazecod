package org.robotframework.jdiameter.keyword;

import org.robotframework.springdoc.EnhancedDocumentedKeyword;

public class Server implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Starts or stops internal jdiameter server";

    private static final String OPERATION = "Operation";

    private static final String START = "START";

    private String name;
    JDiameterServer server;

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
	return new String[] { OPERATION };
    }

    @Override
    public Object execute(Object[] arguments) {
	if (arguments.length != 1) {
	    throw new IllegalArgumentException();
	}
	try {
	    if (START.equals(arguments[0])) {
		server.start();
	    } else {
		server.stop();
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return null;
    }

    public void setServer(JDiameterServer server) {
	this.server = server;
    }

}
