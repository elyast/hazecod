package org.robotframework.protocol;

import nu.xom.Document;

/**
 * Codes from xml document to protocol specific messages
 */
public interface ProtocolCodec {

    /**
     * @param xmlDocument xml document
     * @return Protocol specific message
     */
    Object encode(Document xmlDocument);

    /**
     * @param session Protocol Message (Request) factory
     */
    void setSesssion(Object session);

    /**
     * @param lastRequest Protocol Message (Answer) factory
     */
    void setLastRequest(Object lastRequest);

}
