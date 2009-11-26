package org.robotframework.springdoc;

import org.robotframework.javalib.keyword.DocumentedKeyword;

/**
 * @author fekat
 * 
 */
public interface EnhancedDocumentedKeyword extends DocumentedKeyword {
    void setName(String name);

    String getName();
}
