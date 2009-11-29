package org.robotframework.jdiameter.mapper;

import org.robotframework.jdiameter.DataType;

/**
 * 
 * Avp code -> data type mapping
 * @author Eliot
 *
 */
public class AvpTypeResolver extends CodeResolver {

    public AvpTypeResolver() {
	super("avptype.properties");
    }

    public DataType getType(int code) {
	String name = String.valueOf(code);
	System.out.println("Codec:" + code);
	return DataType.valueOf(props.getProperty(name).trim());
    }

}
