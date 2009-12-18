package org.eliot.hazecod.integration.diameter;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

/**
 * @author Eliot
 *
 */
@ContextConfiguration
public class DiameterRoutingTest extends AbstractJUnit38SpringContextTests {

    static final int FOUR = 4;

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(ref = "diameterEndpoint")
    protected ProducerTemplate template;

    @Test
    @DirtiesContext
    public void testRouting() throws Exception {
	Message expected = MessageFactory.createCEA();

	Request sent = MessageFactory.createCER();

	// resultEndpoint.expectedBodiesReceived(expected);
	template.sendBody(sent);

	int receivedMessagesCount = resultEndpoint.getReceivedCounter();
	List<Exchange> receivedExchanges = resultEndpoint
		.getReceivedExchanges();

	Exchange exchange = receivedExchanges.get(0);
	Message msg = (Message) exchange.getIn().getBody();

	assertEquals(1, receivedMessagesCount);
	assertEquals(expected.getCommandCode(), msg.getCommandCode());
	assertEquals(expected.getApplicationId(), msg.getApplicationId());
	Avp resultCode = msg.getAvps().getAvp(Avp.RESULT_CODE);
	assertEquals(ResultCode.SUCCESS, resultCode.getInteger32());
	resultEndpoint.assertIsSatisfied();
    }
}
