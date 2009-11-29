package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to receive messages from System Under Test
 */
public class ReceiveMessage implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Waits for message and check if given arguments equal received arguments.\n"
	    + "Arguments are in format: avp_name=expected_value\n"
	    + "AVP names are describes in xml templates.\n"
	    + "templateName is without *.xml extension";

    private static final String AVPS_WITH_EXPECTED_VALUES = "*avps_with_expected_values";
    private static final String TEMPLATE_NAME = "templateName";
    private static final String[] ARGUMENTS = { TEMPLATE_NAME,
	    AVPS_WITH_EXPECTED_VALUES };

    private String name;

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String[] getArgumentNames() {
	return ARGUMENTS;
    }

    @Override
    public String getDocumentation() {
	return DOCUMENTATION;
    }

    @Override
    public Object execute(Object[] arguments) {
	return JDiameterClient.getInstance().receiveMessage(arguments);
    }
}
