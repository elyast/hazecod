package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nu.xom.Document;

import org.jdiameter.api.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.protocol.Client;
import org.robotframework.protocol.ProtocolCodec;
import org.robotframework.protocol.TemplateProcessor;

@RunWith(JMockit.class)
public class SendMessageTest {

    private static final String AVP_AND_VALUE1 = "avp1=value1";
    private static final String AVP_AND_VALUE2 = "avp2=value2";
    private static final String[] AVPs = new String[] { AVP_AND_VALUE1,
	    AVP_AND_VALUE2 };
    private static final String TEMPLATE_FILE_PATH = "templateFilePath";
    private SendMessage testObj;

    @Mocked
    TemplateProcessor templateProcessor;
    @Mocked
    ProtocolCodec protocolCodec;
    @Mocked
    Client client;
    @Mocked
    Session session;

    @Before
    public void setUp() throws Exception {

	testObj = new SendMessage();
	testObj.setTemplateProcessor(templateProcessor);
	testObj.setProtocolCodec(protocolCodec);
	testObj.setClient(client);
    }

    @Test
    public void testExecute_correct() {
	Object[] arguments = new Object[] { TEMPLATE_FILE_PATH, AVP_AND_VALUE1,
		AVP_AND_VALUE2 };

	new Expectations() {
	    @Mocked
	    Document xmlDocument;
	    {
		Object message = new Object();

		templateProcessor.processTemplate(
			withEqual(TEMPLATE_FILE_PATH), withEqual(AVPs));
		returns(xmlDocument);
		
		client.getSession();
		returns(session);
		
		protocolCodec.setSesssion(session);

		protocolCodec.encode(xmlDocument);
		returns(message);

		client.sendMessage(message);
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
		Object message = new Object();

		templateProcessor.processTemplate(
			withEqual(TEMPLATE_FILE_PATH), withEqual(avps));
		returns(xmlDocument);
		client.getSession();
		returns(session);
		
		protocolCodec.setSesssion(session);
		
		protocolCodec.encode(xmlDocument);
		returns(message);

		client.sendMessage(message);
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
