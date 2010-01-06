package org.robotframework.jdiameter.mapper;

/**
 * 
 * Diameter data types
 * 
 * @author Eliot
 * 
 */
public enum DataType {
    /**
     * String that starts with 0x
     */
    OCTET_STRING,

    /**
     * Other strings
     */
    UTF8_STRING,

    /**
     * InetAddress type
     */
    ADDRESS,

    /**
     * Grouped of AVPs
     */
    GROUPED,

    /**
     * URI
     */
    URI,
    /**
     * float type
     */
    FLOAT_32,
    /**
     * double type
     */
    FLOAT_64,
    /**
     * integer
     */
    INT_32,
    /**
     * long
     */
    INT_64,
    /**
     * Date
     */
    TIME,
    /**
     * long
     */
    UNSIGNED_32,
    /**
     * long
     */
    UNSIGNED_64

}
