package org.robotframework.jdiameter.mapper;

/**
 * 
 * Avp mnemonic name -> avp code number mapping
 * @author Eliot
 *
 */
public class AvpCodeResolver extends CodeResolver {

    public AvpCodeResolver() {
	super("avpcodes.properties");
    }

}
