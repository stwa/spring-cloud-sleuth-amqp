package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.RabbitHandlerAspect;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitAspectMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitHandlerMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link RabbitHandlerAspect}.
 *
 * @author André Ignacio
 * @author Dominik Bartholdi
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(
  classes = {SpringSimpleTestConfiguration.class, SpringMockTestConfiguration.class}
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RabbitHandlerAspectTest {

  @Autowired private RabbitHandlerMock rabbitHandlerMock;
  @Autowired private RabbitAspectMockManager mockManager;
  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;

  @Test
  public void aspectInvokeSuccess() {
    assertNotNull(rabbitHandlerMock);
    final Message message = new Message("body1".getBytes(), new MessageProperties());
    rabbitHandlerMock.onMessage(message);

    verify(amqpMessagingSpanManager).beforeHandle(eq(message));
    verify(amqpMessagingSpanManager).afterHandle(eq(null));
  }

  @Test
  public void aspectInvokeError() {
    assertNotNull(rabbitHandlerMock);
    mockManager.throwExceptionInNextMessage(new NullPointerException());

    final Message message = new Message("body2".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> rabbitHandlerMock.onMessage(message))
        .isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeHandle(eq(message));
    verify(amqpMessagingSpanManager).afterHandle(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSuccessWithReply() {
    assertNotNull(rabbitHandlerMock);
    final Message message = new Message("body3".getBytes(), new MessageProperties());
    final Message replyMessage = rabbitHandlerMock.onMessageWithReply(message);
    assertEquals(replyMessage, message);
  }
}
