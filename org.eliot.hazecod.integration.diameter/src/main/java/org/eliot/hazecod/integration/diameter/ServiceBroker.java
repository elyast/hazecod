package org.eliot.hazecod.integration.diameter;

/**
 * Workflow executor
 * @author Eliot
 *
 */
public interface ServiceBroker {

    /**
     * @param parameter Request
     * @return Answer
     */
    Object handle(Object parameter);
}
