package org.robotframework.protocol;

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

    void setSesssion(Object session);

    void setLastRequest(Object lastRequest);

}
