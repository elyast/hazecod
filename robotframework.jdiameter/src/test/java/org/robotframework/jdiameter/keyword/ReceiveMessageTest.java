package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nu.xom.Document;

import org.jdiameter.api.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.protocol.Client;
import org.robotframework.protocol.MessageComparator;
import org.robotframework.protocol.ProtocolCodec;
import org.robotframework.protocol.TemplateProcessor;

@RunWith(JMockit.class)
public class ReceiveMessageTest {

    private static final String AVP_AND_VALUE1 = "avp1=value1";
    private static final String AVP_AND_VALUE2 = "avp2=value2";
    private static final String TEMPLATE_FILE_PATH = "templateFilePath";
    private ReceiveMessage testObj;

    @Mocked
    TemplateProcessor templateProcessor;
    @Mocked
    ProtocolCodec protocolCodec;
    @Mocked
    Client client;
    @Mocked
    MessageComparator msgComparator;
    @Mocked
    Request lastRequest;

    @Before
    public void setUp() throws Exception {

	testObj = new ReceiveMessage();
	testObj.setTemplateProcessor(templateProcessor);
	testObj.setProtocolCodec(protocolCodec);
	testObj.setClient(client);
	testObj.setMessageComparator(msgComparator);
    }

    @Test
    public void testExecute_correct() {
	Object[] arguments = new Object[] { TEMPLATE_FILE_PATH, AVP_AND_VALUE1,
		AVP_AND_VALUE2 };
	final String[] expectedAVPs = new String[] { AVP_AND_VALUE1,
		AVP_AND_VALUE2 };

	new Expectations() {
	    @Mocked
	    Document xmlDocument;
	    {
		Object expectedMessage = new Object();
		Object receivedMessage = new Object();

		templateProcessor.processTemplate(
			withEqual(TEMPLATE_FILE_PATH), withEqual(expectedAVPs));
		returns(xmlDocument);

		client.getLastRequest();
		returns(lastRequest);
		
		protocolCodec.setLastRequest(lastRequest);
		
		protocolCodec.encode(xmlDocument);
		returns(expectedMessage);

		client.receiveMessage();
		returns(receivedMessage);

		msgComparator.evaluateMessage(expectedMessage, receivedMessage);
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testExecute_noAvps() {
	Object[] arguments = new Object[] { TEMPLATE_FILE_PATH };
	final String[] avps = new String[0];

	new Expectations() {
	    @Mocked
	    Document xmlDocument;
	    {
		Object expectedMessage = new Object();
		Object receivedMessage = new Object();

		templateProcessor.processTemplate(
			withEqual(TEMPLATE_FILE_PATH), withEqual(avps));
		returns(xmlDocument);
		
		client.getLastRequest();
		returns(lastRequest);
		
		protocolCodec.setLastRequest(lastRequest);

		protocolCodec.encode(xmlDocument);
		returns(expectedMessage);

		client.receiveMessage();
		returns(receivedMessage);

		msgComparator.evaluateMessage(expectedMessage, receivedMessage);
	    }
	};

	testObj.execute(arguments);
    }

    @Test(expected = RuntimeException.class)
    public void testExecute_noTemplate() {
	Object[] arguments = new Object[0];

	testObj.execute(arguments);
    }
}
