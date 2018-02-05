package com.netshoes.springframework.cloud.sleuth.test.unit.instrument.amqp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanExtractor;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanInjector;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.DefaultAmqpMessagingSpanManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.cloud.sleuth.Log;
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
  public void testBeforeHandleWithOneQueue() {
    final Span currentSpan = mock(Span.class);
    when(extractor.joinTrace(any(Message.class))).thenReturn(currentSpan);

    final Span newSpan = mock(Span.class);
    when(tracer.continueSpan(eq(currentSpan))).thenReturn(newSpan);

    final Message message = new Message("Test".getBytes(), null);
    final Span span = spanManager.beforeHandle(message);

    verify(currentSpan).logEvent(eq(Span.SERVER_RECV));
    verify(extractor).joinTrace(eq(message));
    verify(tracer).continueSpan(eq(currentSpan));

    assertEquals(newSpan, span);
  }

  @Test
  public void testBeforeHandleWithTwoQueues() {
    final Span currentSpan = mock(Span.class);
    when(extractor.joinTrace(any(Message.class))).thenReturn(currentSpan);

    final Span newSpan = mock(Span.class);
    when(tracer.continueSpan(eq(currentSpan))).thenReturn(newSpan);

    final Message message = new Message("Test".getBytes(), null);
    final Span span = spanManager.beforeHandle(message);

    verify(currentSpan).logEvent(eq(Span.SERVER_RECV));
    verify(extractor).joinTrace(eq(message));
    verify(tracer).continueSpan(eq(currentSpan));

    assertEquals(newSpan, span);
  }

  @Test
  public void testAfterHandleWithCurrentSpanAndWithoutException() {
    final Span currentSpan = mock(Span.class, "currentSpan");
    when(tracer.isTracing()).thenReturn(true);
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    spanManager.afterHandle(null);

    verify(tracer).detach(eq(currentSpan));
    verify(tracer, never()).addTag(eq(Span.SPAN_ERROR_TAG_NAME), anyString());
    verify(currentSpan).logEvent(eq(Span.SERVER_SEND));
  }

  @Test
  public void testAfterHandleWithCurrentSpanAndWithException() {
    final Span currentSpan = mock(Span.class, "currentSpan");
    when(tracer.isTracing()).thenReturn(true);
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    spanManager.afterHandle(new NullPointerException());

    verify(tracer).detach(eq(currentSpan));
    verify(tracer).addTag(eq(Span.SPAN_ERROR_TAG_NAME), anyString());
    verify(currentSpan).logEvent(eq(Span.SERVER_SEND));
  }

  @Test
  public void testAfterHandleWithoutCurrentSpan() {
    when(tracer.isTracing()).thenReturn(false);
    when(tracer.getCurrentSpan()).thenReturn(null);

    spanManager.afterHandle(null);
  }

  @Test
  public void testBeforeSendWithCurrentSpanOnClientSendFromMessage() {
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
  public void testBeforeSendWithCurrentSpanOnServerReceiveFromMessage() {
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
  public void testBeforeSendWithCurrentSpanOnServerReceiveFromTracer() {
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

  @Test
  public void testAfterSendOnServerSendWithCurrentSpanAndWithoutException() {
    final Span currentSpan = mock(Span.class, "currentSpan");

    when(currentSpan.logs()).thenReturn(getListFromOne(Span.SERVER_RECV));
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    spanManager.afterSend(null);

    verify(tracer).close(eq(currentSpan));
    verify(tracer, never()).addTag(eq(Span.SPAN_ERROR_TAG_NAME), anyString());
    verify(currentSpan).logEvent(eq(Span.SERVER_SEND));
  }

  @Test
  public void testAfterSendOnServerSendWithCurrentSpanAndException() {
    final Span currentSpan = mock(Span.class, "currentSpan");

    when(currentSpan.logs()).thenReturn(getListFromOne(Span.SERVER_RECV));
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    spanManager.afterSend(new NullPointerException());

    verify(tracer).close(eq(currentSpan));
    verify(tracer).addTag(eq(Span.SPAN_ERROR_TAG_NAME), anyString());
    verify(currentSpan).logEvent(eq(Span.SERVER_SEND));
  }

  @Test
  public void testAfterSendOnClientReceiveSendWithCurrentSpanAndWithoutException() {
    final Span currentSpan = mock(Span.class, "currentSpan");

    when(currentSpan.logs()).thenReturn(Collections.emptyList());
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    spanManager.afterSend(null);

    verify(tracer).close(eq(currentSpan));
    verify(tracer, never()).addTag(eq(Span.SPAN_ERROR_TAG_NAME), anyString());
    verify(currentSpan).logEvent(eq(Span.CLIENT_RECV));
  }

  @Test
  public void testAfterSendOnClientReceiveWithCurrentSpanAndException() {
    final Span currentSpan = mock(Span.class, "currentSpan");

    when(currentSpan.logs()).thenReturn(Collections.emptyList());
    when(tracer.getCurrentSpan()).thenReturn(currentSpan);

    spanManager.afterSend(new NullPointerException());

    verify(tracer).close(eq(currentSpan));
    verify(tracer).addTag(eq(Span.SPAN_ERROR_TAG_NAME), anyString());
    verify(currentSpan).logEvent(eq(Span.CLIENT_RECV));
  }

  @Test
  public void testAfterSendOnServerSendWithoutCurrentSpan() {
    when(tracer.getCurrentSpan()).thenReturn(null);

    spanManager.afterSend(null);

    verify(tracer).close(eq(null));
  }

  @Test
  public void testIsTracingWhenIsTrue() {
    when(tracer.isTracing()).thenReturn(true);

    Assert.assertTrue(spanManager.isTracing());
  }

  @Test
  public void testIsTracingWhenIsFalse() {
    when(tracer.isTracing()).thenReturn(false);

    Assert.assertFalse(spanManager.isTracing());
  }

  private List<Log> getListFromOne(String event) {
    final List<Log> logs = new ArrayList<>();
    logs.add(new Log(System.currentTimeMillis(), event));
    return logs;
  }
}
