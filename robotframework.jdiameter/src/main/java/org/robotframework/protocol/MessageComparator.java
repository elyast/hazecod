package org.robotframework.protocol;

/**
 * 
 * message comparator, used to compare messages
 */
public interface MessageComparator {

    /**
     * evaluates if expected Message is equal to received one
     * 
     * @param expected
     *            expected Message
     * @param actual
     *            received Message
     * 
     */
    void evaluateMessage(Object expected, Object actual);
}
