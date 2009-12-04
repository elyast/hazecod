package org.robotframework.jdiameter;

/**
 * 
 * message comparator, used to compare messages
 */
public interface MessageComparator {

    /**
     * evaluates if expected Message is equal to received one
     * 
     * @param exp
     *            expected Message
     * @param act
     *            received Message
     * 
     */
    public void evaluateMessage(Object expected, Object actual);
}
