package org.robotframework.protocol;

/**
 * Protocol client 
 */
public interface Client {
    
    /**
     * Connects to server
     * 
     * @param configuration Configuration of connection
     */
    void openConnection(String configuration);
    
    /**
     * Waits for message
     * 
     * @return Message retrieved from network
     */
    Object receiveMessage();
    
    /**
     * Sends connection
     * 
     * @param msg Message to be send across network
     */
    void sendMessage(Object msg);
    
    /**
     * Close connection
     */
    void closeConnection();

    Object getSession();

    Object getLastRequest();

}
