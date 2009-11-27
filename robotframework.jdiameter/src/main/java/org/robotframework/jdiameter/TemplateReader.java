package org.robotframework.jdiameter;

import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;

/**
 * Reads xml template using XOM library
 * 
 */
public class TemplateReader {

    Builder builder = new Builder();

    /**
     * Reads from input stream using XOM parser
     * 
     * @param input
     *            Input Stream
     * @return XOM Document
     */
    public Document read(InputStream input) {
	try {
	    return builder.build(input);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
