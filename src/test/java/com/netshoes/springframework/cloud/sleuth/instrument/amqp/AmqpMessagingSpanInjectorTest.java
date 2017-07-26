package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
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
  private AmqpMessagingSpanInjector spanInjector;

  @Before
  public void setup() {
    spanInjector = new AmqpMessagingSpanInjector(new TraceKeys());
  }

  @Test
  public void testInjectSuccess() {
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
  public void testInjectSpanNullSuccess() {
    final Message message = new Message("Test".getBytes(), new MessageProperties());

    spanInjector.inject(null, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("0", headers.get(TraceMessageHeaders.SAMPLED_NAME));
    Assert.assertEquals(1, headers.size());
  }

  @Test
  public void testInjectSpanAlreadySampledSuccess() {
    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader(TraceMessageHeaders.SAMPLED_NAME, "1");
    final Message message = new Message("Test".getBytes(), messageProperties);

    spanInjector.inject(null, message);

    final Map<String, Object> headers = message.getMessageProperties().getHeaders();

    Assert.assertEquals("1", headers.get(TraceMessageHeaders.SAMPLED_NAME));
  }
}
