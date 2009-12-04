package org.robotframework.jdiameter;

import nu.xom.Document;

/**
 * Retrieves templates and applies user parameters to build xml document
 */
public interface TemplateProcessor {

    /**
     * Create xml document from template name and user parameters
     * 
     * @param template
     * @param parameters
     * @return
     */
    Document processTemplate(String template, String[] parameters);
}
