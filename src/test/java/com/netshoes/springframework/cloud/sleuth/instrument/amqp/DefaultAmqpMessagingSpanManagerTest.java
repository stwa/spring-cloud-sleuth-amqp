package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Unit tests for {@link DefaultAmqpMessagingSpanManager}.
 *
 * @author André Ignacio
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultAmqpMessagingSpanManagerTest {
  @Mock private AmqpMessagingSpanInjector injector;
  @Mock private AmqpMessagingSpanExtractor extractor;
  @Mock private Tracer tracer;
  @Captor private ArgumentCaptor<String> queueNameCaptor;

  private AmqpMessagingSpanManager spanManager;

  @Before
  public void setup() {
    spanManager = new DefaultAmqpMessagingSpanManager(injector, extractor, tracer);
  }

  @Test
  public void testExtractAndContinueSpanOneQueuesSuccess() {
    final Span currentSpan = Mockito.mock(Span.class);
    Mockito.when(extractor.joinTrace(Matchers.any(Message.class))).thenReturn(currentSpan);

    final Span newSpan = Mockito.mock(Span.class);
    Mockito.when(tracer.createSpan(Matchers.anyString(), Matchers.eq(currentSpan)))
        .thenReturn(newSpan);

    final Message message = new Message("Test".getBytes(), null);
    final String[] queueNames = new String[] {"queue"};
    final Span span = spanManager.extractAndContinueSpan(message, queueNames);

    Mockito.verify(extractor).joinTrace(Matchers.eq(message));
    Mockito.verify(tracer).createSpan(queueNameCaptor.capture(), Matchers.eq(currentSpan));

    Assert.assertEquals(newSpan, span);
    Assert.assertEquals("queue", queueNameCaptor.getValue());
  }

  @Test
  public void testExtractAndContinueSpanTwoQueuesSuccess() {
    final Span currentSpan = Mockito.mock(Span.class);
    Mockito.when(extractor.joinTrace(Matchers.any(Message.class))).thenReturn(currentSpan);

    final Span newSpan = Mockito.mock(Span.class);
    Mockito.when(tracer.createSpan(Matchers.anyString(), Matchers.eq(currentSpan)))
        .thenReturn(newSpan);

    final Message message = new Message("Test".getBytes(), null);
    final String[] queueNames = new String[] {"queue1", "queue2"};
    final Span span = spanManager.extractAndContinueSpan(message, queueNames);

    Mockito.verify(extractor).joinTrace(Matchers.eq(message));
    Mockito.verify(tracer).createSpan(queueNameCaptor.capture(), Matchers.eq(currentSpan));

    Assert.assertEquals(newSpan, span);
    Assert.assertEquals("queue1,queue2", queueNameCaptor.getValue());
  }

  @Test
  public void testInjectCurrentSpanSuccess() {
    final Span currentSpan = Mockito.mock(Span.class);
    Mockito.when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    final Message message = new Message("Test".getBytes(), null);
    final Span span = spanManager.injectCurrentSpan(message);

    Mockito.verify(injector).inject(Matchers.eq(currentSpan), Matchers.eq(message));
    Assert.assertEquals(currentSpan, span);
  }
}
