package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.RabbitListenerAspect;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitAspectMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitListenerMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link RabbitListenerAspect}.
 *
 * @author André Ignacio
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RabbitListenerAspectTest {

  @Autowired private RabbitListenerMock rabbitListenerMock;
  @Autowired private RabbitAspectMockManager mockManager;
  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;

  @Test
  public void aspectInvokeSuccess() {
    assertNotNull(rabbitListenerMock);
    final Message message = new Message("body1".getBytes(), new MessageProperties());
    rabbitListenerMock.onMessage(message);

    verify(amqpMessagingSpanManager).beforeHandle(eq(message));
    verify(amqpMessagingSpanManager).afterHandle(eq(null));
  }

  @Test
  public void aspectInvokeError() {
    assertNotNull(rabbitListenerMock);
    mockManager.throwExceptionInNextMessage(new NullPointerException());

    final Message message = new Message("body2".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> rabbitListenerMock.onMessage(message))
        .isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeHandle(eq(message));
    verify(amqpMessagingSpanManager).afterHandle(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSuccessWithReply() {
    assertNotNull(rabbitListenerMock);
    final Message message = new Message("body3".getBytes(), new MessageProperties());
    final Message replyMessage = rabbitListenerMock.onMessageWithReply(message);
    assertEquals(replyMessage, message);
  }
}
