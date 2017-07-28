package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.sun.tools.javac.util.List;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;

/**
 * Unit tests for {@link AmqpMessagingSpanInjector}.
 *
 * @author André Ignacio
 */
@RunWith(MockitoJUnitRunner.class)
public class AmqpMessagingSpanInjectorTest {

  @Test
  public void testInjectSuccess() {
    final AmqpMessagingSpanInjector spanInjector = new AmqpMessagingSpanInjector(new TraceKeys());

    final Span span =
        Span.builder().spanId(Span.hexToId("123")).traceId(Span.hexToId("456")).build();
    final Message message = new Message("Test".getBytes(), new MessageProperties());

    spanInjector.inject(span, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("1", headers.get(TraceMessageHeaders.SAMPLED_NAME));
    Assert.assertEquals("0000000000000123", headers.get(TraceMessageHeaders.SPAN_ID_NAME));
    Assert.assertEquals("0000000000000456", headers.get(TraceMessageHeaders.TRACE_ID_NAME));
    Assert.assertEquals(4, headers.size());
  }

  @Test
  public void testInjectWithParentIdSuccess() {
    final AmqpMessagingSpanInjector spanInjector = new AmqpMessagingSpanInjector(new TraceKeys());

    final Span span =
        Span.builder()
            .parent(Span.hexToId("999"))
            .spanId(Span.hexToId("123"))
            .traceId(Span.hexToId("456"))
            .build();
    final Message message = new Message("Test".getBytes(), new MessageProperties());

    spanInjector.inject(span, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("1", headers.get(TraceMessageHeaders.SAMPLED_NAME));
    Assert.assertEquals("0000000000000999", headers.get(TraceMessageHeaders.PARENT_ID_NAME));
    Assert.assertEquals("0000000000000123", headers.get(TraceMessageHeaders.SPAN_ID_NAME));
    Assert.assertEquals("0000000000000456", headers.get(TraceMessageHeaders.TRACE_ID_NAME));
    Assert.assertEquals(5, headers.size());
  }

  @Test
  public void testInjectSpanNullSuccess() {
    final AmqpMessagingSpanInjector spanInjector = new AmqpMessagingSpanInjector(new TraceKeys());

    final Message message = new Message("Test".getBytes(), new MessageProperties());

    spanInjector.inject(null, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("0", headers.get(TraceMessageHeaders.SAMPLED_NAME));
    Assert.assertEquals(1, headers.size());
  }

  @Test
  public void testInjectSpanNotExportableSuccess() {
    final AmqpMessagingSpanInjector spanInjector = new AmqpMessagingSpanInjector(new TraceKeys());

    final Message message = new Message("Test".getBytes(), new MessageProperties());

    final Span span =
        Span.builder()
            .exportable(false)
            .spanId(Span.hexToId("123"))
            .traceId(Span.hexToId("456"))
            .build();

    spanInjector.inject(span, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("0", headers.get(TraceMessageHeaders.SAMPLED_NAME));
    Assert.assertEquals("0000000000000123", headers.get(TraceMessageHeaders.SPAN_ID_NAME));
    Assert.assertEquals("0000000000000456", headers.get(TraceMessageHeaders.TRACE_ID_NAME));
    Assert.assertEquals(4, headers.size());
  }

  @Test
  public void testInjectSpanAlreadySampledSuccess() {
    final AmqpMessagingSpanInjector spanInjector = new AmqpMessagingSpanInjector(new TraceKeys());

    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader(TraceMessageHeaders.SAMPLED_NAME, "1");
    final Message message = new Message("Test".getBytes(), messageProperties);

    spanInjector.inject(null, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("1", headers.get(TraceMessageHeaders.SAMPLED_NAME));
  }

  @Test
  public void testInjectWithTrackKeysSuccess() {
    final TraceKeys traceKeys = new TraceKeys();
    final Collection<String> traceKeyMessageHeaders =
        List.from(new String[] {"CUSTOM_HEADER", "NULL_HEADER"});
    final TraceKeys.Message traceKeyMessage = new TraceKeys.Message();
    traceKeyMessage.setHeaders(traceKeyMessageHeaders);
    traceKeys.setMessage(traceKeyMessage);

    final AmqpMessagingSpanInjector spanInjector = new AmqpMessagingSpanInjector(traceKeys);

    final Span span =
        Span.builder().spanId(Span.hexToId("123")).traceId(Span.hexToId("456")).build();

    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader("CUSTOM_HEADER", "Custom header value");
    messageProperties.setHeader("NULL_HEADER", null);

    final Message message = new Message("Test".getBytes(), messageProperties);

    spanInjector.inject(span, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("1", headers.get(TraceMessageHeaders.SAMPLED_NAME));
    Assert.assertEquals("0000000000000123", headers.get(TraceMessageHeaders.SPAN_ID_NAME));
    Assert.assertEquals("0000000000000456", headers.get(TraceMessageHeaders.TRACE_ID_NAME));
    Assert.assertEquals(6, headers.size());
    Assert.assertEquals("Custom header value", span.tags().get("message/custom_header"));
    Assert.assertEquals("null", span.tags().get("message/null_header"));
  }
}
