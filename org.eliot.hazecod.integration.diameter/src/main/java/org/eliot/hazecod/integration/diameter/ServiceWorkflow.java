package org.eliot.hazecod.integration.diameter;

/**
 * Workflow interface
 * @author Eliot
 *
 * @param <R> Request Message Type
 * @param <A> Answer Message Type
 */
public interface ServiceWorkflow<R, A> {

    /**
     * @param parameter Request
     * @return Answer
     */
    A handle(R parameter);
}
