package org.robotframework.jdiameter.mapper;

import org.robotframework.jdiameter.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper used to retrieve data type for given AVP code
 */
public class AvpTypeResolver extends CodeResolver {

    private static final String AVPTYPE_PROPERTIES = "avptype.properties";

    private static Logger logger = LoggerFactory
	    .getLogger(AvpTypeResolver.class);

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
	String name = String.valueOf(code);
	logger.info("Codec:" + code);
	return DataType.valueOf(props.getProperty(name).trim());
    }

}
