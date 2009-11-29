package org.robotframework.jdiameter;

import org.robotframework.springdoc.SpringDocumentedLibrary;

public class JDiameterLibrary extends SpringDocumentedLibrary {

    private static final String DIAMETER_LIBRARY_CONFIG = "robotframework/jdiameter/keywords.xml";

    public JDiameterLibrary() {
	super(DIAMETER_LIBRARY_CONFIG);
    }
}
