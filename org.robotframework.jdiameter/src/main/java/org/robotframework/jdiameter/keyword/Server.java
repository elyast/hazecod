package org.robotframework.jdiameter.keyword;

import org.robotframework.javalib.keyword.EnhancedDocumentedKeyword;

/**
 * JDiameter server keyword
 * @author Eliot
 *
 */
public class Server implements EnhancedDocumentedKeyword  {

    private static final String DOCUMENTATION = 
	"Starts or stops internal jdiameter server";

    private static final String OPERATION = "Operation";

    private static final String START = "START";
    
    private String name;
    JDiameterServer server;

    /**
     * @return Keyword name
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * @param name Keyword name
     */
    @Override
    public void setName(String name) {
	this.name = name;
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
	return new String[] {OPERATION};
    }


    /**
     * @param arguments test parameters
     * @return null
     */
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
    
    /**
     * @param server Server
     */
    public void setServer(JDiameterServer server) {
	this.server = server;
    }

}
