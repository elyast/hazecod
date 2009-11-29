package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;

/**
 * RobotFramework keyword used to send messages from System Under Test.
 */
public class SendMessage implements EnhancedDocumentedKeyword {

    private static final String DOCUMENTATION = "Send message with given arguments.\n"
	    + "Arguments are in format: avp_name=value, where putting AVP value will overwrite existing from template (if defined)\n"
	    + "AVP names are describes in xml templates.\n"
	    + "templateName is without *.xml extension";

    private static final String AVPS_WITH_VALUES_TO_BE_OVERWRITTEN = "*avps_with_values_to_be_overwritten";
    private static final String TEMPLATE_NAME = "templateName";
    private static final String[] ARGUMENTS = { TEMPLATE_NAME,
	    AVPS_WITH_VALUES_TO_BE_OVERWRITTEN };

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
	return JDiameterClient.getInstance().sendMessage(arguments);
    }
}
