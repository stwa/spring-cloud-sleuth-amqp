package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;

/**
 * Unit tests for {@link SpanManagerMessagePostProcessor}.
 *
 * @author André Ignacio
 */
@RunWith(MockitoJUnitRunner.class)
public class SpanManagerMessagePostProcessorTest {
  @Mock private AmqpMessagingSpanManager spanManager;

  private SpanManagerMessagePostProcessor postProcessor;

  @Before
  public void setup() {
    postProcessor = new SpanManagerMessagePostProcessor(spanManager, "my-exchange");
  }

  @Test
  public void testPostProcessMessageSuccess() {
    final Message message = Mockito.mock(Message.class);
    postProcessor.postProcessMessage(message);
    verify(spanManager).beforeSend(Matchers.eq(message), anyString());
  }
}
