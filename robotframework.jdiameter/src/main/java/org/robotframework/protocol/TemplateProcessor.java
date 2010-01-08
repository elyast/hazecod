package org.robotframework.protocol;

import nu.xom.Document;

/**
 * Retrieves templates and applies user parameters to build xml document
 */
public interface TemplateProcessor {

    /**
     * Create xml document from template name and user parameters
     * 
     * @param template xml template
     * @param parameters user parameters
     * @return XML Document
     */
    Document processTemplate(String template, String[] parameters);
}
