package com.netshoes.springframework.cloud.sleuth.test.unit.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanExtractor;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;

/**
 * Unit tests for {@link AmqpMessagingSpanExtractor}.
 *
 * @author André Ignacio
 */
@RunWith(MockitoJUnitRunner.class)
public class AmqpMessagingSpanExtractorTest {
  private AmqpMessagingSpanExtractor spanExtractor;

  @Before
  public void setup() {
    spanExtractor = new AmqpMessagingSpanExtractor(new Random());
  }

  @Test
  public void testJoinTraceWithoutSpanHeaders() {
    final Message message = new Message("Test".getBytes(), new MessageProperties());
    final Span span = spanExtractor.joinTrace(message);
    Assert.assertNull(span);
  }

  @Test
  public void testJoinTraceWithoutParentSuccess() {
    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader(TraceMessageHeaders.TRACE_ID_NAME, "123");
    messageProperties.setHeader(TraceMessageHeaders.SPAN_ID_NAME, "456");

    final Message message = new Message("Test".getBytes(), messageProperties);
    final Span span = spanExtractor.joinTrace(message);

    Assert.assertEquals("0000000000000123", span.traceIdString());
    Assert.assertEquals("0000000000000456", Span.idToHex(span.getSpanId()));
  }

  @Test
  public void testJoinTraceWithParentSuccess() {
    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader(TraceMessageHeaders.TRACE_ID_NAME, "123");
    messageProperties.setHeader(TraceMessageHeaders.SPAN_ID_NAME, "456");
    messageProperties.setHeader(TraceMessageHeaders.PARENT_ID_NAME, "111");

    final Message message = new Message("Test".getBytes(), messageProperties);
    final Span span = spanExtractor.joinTrace(message);

    Assert.assertEquals("0000000000000123", span.traceIdString());
    Assert.assertEquals("0000000000000456", Span.idToHex(span.getSpanId()));
    Assert.assertEquals("0000000000000111", Span.idToHex(span.getParents().get(0)));
  }

  @Test
  public void testJoinTraceCompleteSuccess() {
    final MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader(TraceMessageHeaders.TRACE_ID_NAME, "123");
    messageProperties.setHeader(TraceMessageHeaders.SPAN_ID_NAME, "456");
    messageProperties.setHeader(TraceMessageHeaders.SPAN_NAME_NAME, "amqp");
    messageProperties.setHeader(TraceMessageHeaders.PARENT_ID_NAME, "111");
    messageProperties.setHeader(TraceMessageHeaders.PROCESS_ID_NAME, "process");

    final Message message = new Message("Test".getBytes(), messageProperties);
    final Span span = spanExtractor.joinTrace(message);

    Assert.assertEquals("0000000000000123", span.traceIdString());
    Assert.assertEquals("0000000000000456", Span.idToHex(span.getSpanId()));
    Assert.assertEquals("amqp", span.getName());
    Assert.assertEquals("0000000000000111", Span.idToHex(span.getParents().get(0)));
    Assert.assertEquals("process", span.getProcessId());
  }
}
