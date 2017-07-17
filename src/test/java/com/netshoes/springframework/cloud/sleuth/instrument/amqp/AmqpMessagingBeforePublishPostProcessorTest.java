package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;

/**
 * Unit tests for {@link AmqpMessagingBeforePublishPostProcessor}.
 *
 * @author André Ignacio
 */
@RunWith(MockitoJUnitRunner.class)
public class AmqpMessagingBeforePublishPostProcessorTest {
  @Mock private AmqpMessagingSpanManager spanManager;

  private AmqpMessagingBeforePublishPostProcessor postProcessor;

  @Before
  public void setup() {
    postProcessor = new AmqpMessagingBeforePublishPostProcessor(spanManager);
  }

  @Test
  public void testPostProcessMessageSuccess() {
    final Message message = Mockito.mock(Message.class);
    postProcessor.postProcessMessage(message);
    Mockito.verify(spanManager).injectCurrentSpan(Matchers.eq(message));
  }
}
