package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.RabbitListenerMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link RabbitListenerAspect}.
 *
 * @author André Ignacio
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitListenerAspectTest {

  @Autowired private RabbitListenerMock rabbitListenerMock;
  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;
  @Captor private ArgumentCaptor<String[]> captor;

  @Test
  public void aspectInvokeSuccess() throws Throwable {
    Assert.assertNotNull(rabbitListenerMock);
    final Message message = new Message("body".getBytes(), new MessageProperties());
    rabbitListenerMock.onMessage(message);

    Mockito.verify(amqpMessagingSpanManager)
        .extractAndContinueSpan(Matchers.eq(message), captor.capture());

    final String[] queues = captor.getValue();
    Assert.assertEquals("test-queue", queues[0]);
  }
}
