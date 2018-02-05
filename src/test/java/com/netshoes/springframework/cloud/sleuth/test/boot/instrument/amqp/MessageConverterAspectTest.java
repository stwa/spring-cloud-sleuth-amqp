package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitAspectMockManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link MessageConverterAspectTest}.
 *
 * @author André Ignacio
 * @since 0.9
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(
  classes = {SpringSimpleTestConfiguration.class, SpringMockTestConfiguration.class}
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MessageConverterAspectTest {

  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;
  @Autowired private RabbitAspectMockManager mockManager;
  @Autowired private MessageConverter messageConverter;

  @Test
  public void aspectInvokeSuccess() {
    final Message message = new Message("body1".getBytes(), new MessageProperties());

    messageConverter.fromMessage(message);

    verify(amqpMessagingSpanManager).beforeHandle(eq(message));
    verify(amqpMessagingSpanManager).afterHandle(eq(null));
  }

  @Test
  public void aspectInvokeError() {
    mockManager.throwExceptionInNextMessage(new NullPointerException());

    final Message message = new Message("body2".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> messageConverter.fromMessage(message))
        .isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeHandle(eq(message));
    verify(amqpMessagingSpanManager).afterHandle(any(NullPointerException.class));
  }
}
