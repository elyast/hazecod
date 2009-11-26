package org.robotframework.jdiameter.keyword;

import org.robotframework.jdiameter.JDiameterClient;
import org.robotframework.springdoc.EnhancedDocumentedKeyword;


public class SendMessage implements EnhancedDocumentedKeyword {

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
	return new String[] { "templateName",
		"*avps_with_values_to_be_overwritten" };
    }

    @Override
    public String getDocumentation() {
	return "Send message with given arguments.\n"
		+ "Arguments are in format: avp_name=value, where putting AVP value will overwrite existing from template (if defined)\n"
		+ "AVP names are describes in xml templates.\n"
		+ "templateName is without *.xml extension";
    }

    @Override
    public Object execute(Object[] arguments) {
	return JDiameterClient.getInstance().sendMessage(arguments);
    }
}
