package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link AmqpTemplateAspect}.
 *
 * @author André Ignacio
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AmqpTemplateAspectTest {

  @Autowired private AmqpTemplate amqpTemplate;
  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;

  @Test
  public void aspectInvokeSendSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message = new Message("body-send".getBytes(), new MessageProperties());
    amqpTemplate.send(message);

    Mockito.verify(amqpMessagingSpanManager).injectCurrentSpan(Matchers.eq(message));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message = new Message("body-send-rk".getBytes(), new MessageProperties());
    amqpTemplate.send("rk", message);

    Mockito.verify(amqpMessagingSpanManager).injectCurrentSpan(Matchers.eq(message));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.send("exchange", "rk", message);

    Mockito.verify(amqpMessagingSpanManager).injectCurrentSpan(Matchers.eq(message));
  }

  @Test
  public void aspectInvokeSendAndReceiveSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive(message);

    Mockito.verify(amqpMessagingSpanManager).injectCurrentSpan(Matchers.eq(message));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-rk".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive("rk", message);

    Mockito.verify(amqpMessagingSpanManager).injectCurrentSpan(Matchers.eq(message));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive("exchange", "rk", message);

    Mockito.verify(amqpMessagingSpanManager).injectCurrentSpan(Matchers.eq(message));
  }
}
