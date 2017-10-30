package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
  public void testExtractAndContinueSpanOneQueueSuccess() {
    final Span currentSpan = mock(Span.class);
    when(extractor.joinTrace(any(Message.class))).thenReturn(currentSpan);

    final Span newSpan = mock(Span.class);
    when(tracer.continueSpan(eq(currentSpan))).thenReturn(newSpan);

    final Message message = new Message("Test".getBytes(), null);
    final String[] queueNames = new String[] {"queue"};
    final Span span = spanManager.beforeHandle(message, queueNames);

    verify(currentSpan).logEvent(eq(Span.SERVER_RECV));
    verify(extractor).joinTrace(eq(message));
    verify(tracer).continueSpan(eq(currentSpan));

    assertEquals(newSpan, span);
  }

  @Test
  public void testExtractAndContinueSpanTwoQueuesSuccess() {
    final Span currentSpan = mock(Span.class);
    when(extractor.joinTrace(any(Message.class))).thenReturn(currentSpan);

    final Span newSpan = mock(Span.class);
    when(tracer.continueSpan(eq(currentSpan))).thenReturn(newSpan);

    final Message message = new Message("Test".getBytes(), null);
    final String[] queueNames = new String[] {"queue1", "queue2"};
    final Span span = spanManager.beforeHandle(message, queueNames);

    verify(currentSpan).logEvent(eq(Span.SERVER_RECV));
    verify(extractor).joinTrace(eq(message));
    verify(tracer).continueSpan(eq(currentSpan));

    assertEquals(newSpan, span);
  }

  @Test
  public void testInjectCurrentSpanOnClientSendFromMessage() {
    when(tracer.isTracing()).thenReturn(false);

    final Span parentSpan = mock(Span.class, "parentSpan");
    final Message message = new Message("Test".getBytes(), new MessageProperties());
    when(extractor.joinTrace(eq(message))).thenReturn(parentSpan);

    final Span newSpan = mock(Span.class, "newSpan");
    when(tracer.createSpan(anyString(), eq(parentSpan))).thenReturn(newSpan);

    final Span span = spanManager.beforeSend(message, "exchange");

    verify(injector).inject(eq(newSpan), eq(message));
    assertEquals(newSpan, span);

    verify(newSpan).logEvent(eq(Span.CLIENT_SEND));
  }

  @Test
  public void testInjectCurrentSpanOnServerReceiveFromMessage() {
    when(tracer.isTracing()).thenReturn(false);

    final Span parentSpan = mock(Span.class, "parentSpan");
    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader("messageSent", Boolean.TRUE.toString());

    final Message message = new Message("Test".getBytes(), messageProperties);
    when(extractor.joinTrace(eq(message))).thenReturn(parentSpan);

    final Span newSpan = mock(Span.class, "newSpan");
    when(tracer.createSpan(anyString(), eq(parentSpan))).thenReturn(newSpan);

    final Span span = spanManager.beforeSend(message, "exchange");

    verify(injector).inject(eq(newSpan), eq(message));
    assertEquals(newSpan, span);

    verify(newSpan).logEvent(eq(Span.SERVER_RECV));
  }

  @Test
  public void testInjectCurrentSpanOnServerReceiveFromTracer() {
    final Span currentSpan = mock(Span.class, "currentSpan");
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);
    when(tracer.isTracing()).thenReturn(true);

    final Span newSpan = mock(Span.class, "newSpan");
    when(tracer.createSpan(anyString(), eq(currentSpan))).thenReturn(newSpan);

    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader("messageSent", Boolean.TRUE.toString());

    final Message message = new Message("Test".getBytes(), messageProperties);
    final Span span = spanManager.beforeSend(message, "exchange");

    verify(injector).inject(eq(newSpan), eq(message));
    assertEquals(newSpan, span);

    verify(newSpan).logEvent(eq(Span.SERVER_RECV));
  }
}
