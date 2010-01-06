package org.robotframework.jdiameter.mapper;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper used to retrieve data type for given AVP code
 */
public class AvpTypeResolver extends CodeResolver {

    private static final String AVPTYPE_PROPERTIES = "avptype.properties";

    private static Logger logger = LoggerFactory
	    .getLogger(AvpTypeResolver.class);

    /**
     * 
     */
    public AvpTypeResolver() {
	super(AVPTYPE_PROPERTIES);
    }

    /**
     * gets DataType for given AVP code
     * 
     * @param code
     *            diameter AVP code
     * @return DataType of given AVP code
     */
    public DataType getType(int code) {
	initialize();
	String name = String.valueOf(code);
	logger.debug("Codec:" + code);
	String property = props.getProperty(name);
	if (property == null) {	    
	    throw new RuntimeException("Not found property : " + code);
	}
	return DataType.valueOf(property.trim());
    }

    /**
     * @param expected Expected avp
     * @return Value from AVP
     */
    public Object getValue(Avp expected) {
	DataType type = getType(expected.getCode());
	try {
	 switch (type) {
	    case INT_32:
	        return expected.getInteger32();
	    case INT_64:
		return expected.getInteger64();
	    case FLOAT_32:
		return expected.getFloat32();
	    case FLOAT_64:
		return expected.getFloat64();
	    case OCTET_STRING:
		return expected.getOctetString();
	    case ADDRESS:
		return expected.getAddress();
	    case GROUPED:
		return expected.getGrouped();
	    case TIME:
		return expected.getTime();
	    case UTF8_STRING:
		return expected.getUTF8String();
	    case UNSIGNED_32:
		return expected.getUnsigned32();
	    case UNSIGNED_64:
		return expected.getUnsigned64();
	    default:
	        throw new IllegalArgumentException("Not handled data type");
	    }
	} catch (AvpDataException e) {
	    throw new IllegalArgumentException(e);
	}
    }

}
