package org.robotframework.jdiameter;

import nu.xom.Document;

/**
 *
 */
public interface ProtocolCodec {

    /**
     * @param xmlDocument
     * @return
     */
    Object encode(Document xmlDocument);

}
