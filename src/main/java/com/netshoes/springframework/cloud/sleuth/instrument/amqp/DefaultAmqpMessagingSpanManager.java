package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import java.util.StringJoiner;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Default implementation for {@link AmqpMessagingSpanManager} who uses {@link
 * AmqpMessagingSpanExtractor} and {@link AmqpMessagingSpanInjector} to create or get {@link Span}
 * from {@link Tracer}.
 *
 * @author André Ignacio
 */
public class DefaultAmqpMessagingSpanManager implements AmqpMessagingSpanManager {
  private final AmqpMessagingSpanExtractor extractor;
  private final AmqpMessagingSpanInjector injector;
  private final Tracer tracer;

  /**
   * Creates a new instance.
   *
   * @param injector Injector for AMQP messaging
   * @param extractor Extractor for AMQP messaging
   * @param tracer Tracer
   */
  public DefaultAmqpMessagingSpanManager(
      AmqpMessagingSpanInjector injector, AmqpMessagingSpanExtractor extractor, Tracer tracer) {
    this.injector = injector;
    this.extractor = extractor;
    this.tracer = tracer;
  }

  @Override
  public Span extractAndContinueSpan(Message message) {
    final Span span = extractor.joinTrace(message);
    //TODO
    final Span newSpan = tracer.createSpan(null, span);
    return newSpan;
  }

  @Override
  public Span extractAndContinueSpan(Message message, String[] queues) {
    final Span span = extractor.joinTrace(message);
    final String spanName = queueNamesSeparetedByComma(queues);
    final Span newSpan = tracer.createSpan(spanName, span);
    return newSpan;
  }

  @Override
  public Span injectCurrentSpan(Message message) {
    final Span span = tracer.getCurrentSpan();
    injector.inject(span, message);
    return span;
  }

  private String queueNamesSeparetedByComma(String[] queues) {
    final StringJoiner joiner = new StringJoiner(",");
    for (String queue : queues) {
      joiner.add(queue);
    }
    return joiner.toString();
  }
}
