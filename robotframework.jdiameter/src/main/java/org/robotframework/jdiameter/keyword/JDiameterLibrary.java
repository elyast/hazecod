package org.robotframework.jdiameter.keyword;

import org.robotframework.javalib.library.SpringDocumentedLibrary;

public class JDiameterLibrary extends SpringDocumentedLibrary {

    private static final String DIAMETER_LIBRARY_CONFIG = "robotframework/jdiameter/keywords.xml";

    public JDiameterLibrary() {
	super(DIAMETER_LIBRARY_CONFIG);
    }
}
