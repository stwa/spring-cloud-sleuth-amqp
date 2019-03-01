package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Span.SpanBuilder;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.support.AmqpMessageHeaderAccessor;

/**
 * Class responsible for extracting a {@link Span} from an AMQP message.
 *
 * @author André Ignacio
 */
public class AmqpMessagingSpanExtractor implements SpanExtractor<Message> {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AmqpMessagingSpanExtractor.class);
  private final Random random;

  public AmqpMessagingSpanExtractor(Random random) {
    this.random = random;
  }

  @Override
  public Span joinTrace(Message message) {
    log.debug("joining trace for message: {}", message);

    final AmqpMessageHeaderAccessor accessor = AmqpMessageHeaderAccessor.getAccessor(message);

    if ((!hasHeader(accessor, Span.TRACE_ID_NAME) || !hasHeader(accessor, Span.SPAN_ID_NAME))
      && (!hasHeader(accessor, TraceMessageHeaders.SPAN_ID_NAME)
      || !hasHeader(accessor, TraceMessageHeaders.TRACE_ID_NAME))) {
      return null;
    }

    if (hasHeader(accessor, Span.TRACE_ID_NAME)) {
      return extractSpanFromHeaders(accessor,
        Span.TRACE_ID_NAME,
        Span.SPAN_ID_NAME,
        Span.SAMPLED_NAME,
        Span.PROCESS_ID_NAME,
        Span.SPAN_NAME_NAME,
        Span.PARENT_ID_NAME);
    } else {
      return extractSpanFromHeaders(accessor,
        TraceMessageHeaders.TRACE_ID_NAME,
        TraceMessageHeaders.SPAN_ID_NAME,
        TraceMessageHeaders.SAMPLED_NAME,
        TraceMessageHeaders.PROCESS_ID_NAME,
        TraceMessageHeaders.SPAN_NAME_NAME,
        TraceMessageHeaders.PARENT_ID_NAME);
    }
  }

  private Span extractSpanFromHeaders(final AmqpMessageHeaderAccessor accessor,
                                      final String traceIdHeader,
                                      final String spanIdHeader,
                                      final String spanSampledHeader,
                                      final String spanProcessIdHeader,
                                      final String spanNameHeader,
                                      final String spanParentIdHeader) {

    SpanBuilder spanBuilder = Span.builder().remote(true);

    log.debug("extracting span from headers: ", Arrays.asList(traceIdHeader, spanIdHeader, spanSampledHeader, spanProcessIdHeader, spanNameHeader, spanParentIdHeader));

    withHeader(accessor, traceIdHeader, traceId -> {
      spanBuilder.traceIdHigh(traceId.length() == 32 ? Span.hexToId(traceId, 0) : 0);
      spanBuilder.traceId(Span.hexToId(traceId));
    });
    withHeader(accessor, spanIdHeader, spanId -> spanBuilder.spanId(Span.hexToId(spanId)));
    withHeader(accessor, spanSampledHeader, spanSampled -> spanBuilder.exportable(Span.SPAN_SAMPLED.equals(spanSampled)));
    withHeader(accessor, spanNameHeader, spanName -> spanBuilder.name(spanName));
    withHeader(accessor, spanProcessIdHeader, processId -> spanBuilder.processId(processId));
    withHeader(accessor, spanParentIdHeader, parentId -> spanBuilder.parent(Span.hexToId(parentId)));

    return spanBuilder.build();
  }

  private String getHeader(AmqpMessageHeaderAccessor accessor, String name) {
    return accessor.getHeader(name, String.class);
  }

  private boolean hasHeader(AmqpMessageHeaderAccessor accessor, String name) {
    return accessor.hasHeader(name);
  }

  private void withHeader(AmqpMessageHeaderAccessor accessor, String headerName, Consumer<String> valueConsumer) {
    String headerValue = getHeader(accessor, headerName);
    if (headerValue != null) {
      valueConsumer.accept(headerValue);
    }
  }
}
