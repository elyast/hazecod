package robotframework.jdiameter.keyword;

import robotframework.jdiameter.JDiameterClient;
import robotframework.keyword.EnhancedDocumentedKeyword;

public class ReceiveMessage implements EnhancedDocumentedKeyword {

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
	return new String[] { "templateName", "*avps_with_expected_values" };
    }

    @Override
    public String getDocumentation() {
	return "Waits for message and check if given arguments equal received arguments.\n"
		+ "Arguments are in format: avp_name=expected_value\n"
		+ "AVP names are describes in xml templates.\n"
		+ "templateName is without *.xml extension";
    }

    @Override
    public Object execute(Object[] arguments) {
	return JDiameterClient.getInstance().receiveMessage(arguments);
    }
}
