package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.support.AmqpMessageHeaderAccessor;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Log;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.util.ExceptionUtils;

/**
 * Default implementation for {@link AmqpMessagingSpanManager} who uses {@link
 * AmqpMessagingSpanExtractor} and {@link AmqpMessagingSpanInjector} to create or get {@link Span}
 * from {@link Tracer}.
 *
 * @author André Ignacio
 * @since 0.8
 */
public class DefaultAmqpMessagingSpanManager implements AmqpMessagingSpanManager {
  private static final String MESSAGE_SENT_FROM_CLIENT = "messageSent";
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
  public void afterHandle(Exception ex) {
    final Span currentSpan = tracer.getCurrentSpan();
    if (currentSpan != null) {
      currentSpan.logEvent(Span.SERVER_SEND);
      addErrorTag(ex);
    }
    if (tracer.isTracing()) {
      tracer.detach(currentSpan);
    }
  }

  @Override
  public Span beforeHandle(Message message) {
    final Span span = extractor.joinTrace(message);
    if (span != null) {
      span.logEvent(Span.SERVER_RECV);
    }
    return tracer.continueSpan(span);
  }

  @Override
  public Span beforeSend(Message message, String spanName) {
    final Span parentSpan = tracer.isTracing() ? tracer.getCurrentSpan() : buildSpan(message);
    final Span span = tracer.createSpan(spanName, parentSpan);
    final AmqpMessageHeaderAccessor accessor = AmqpMessageHeaderAccessor.getAccessor(message);
    if (accessor.hasHeader(MESSAGE_SENT_FROM_CLIENT)) {
      span.logEvent(Span.SERVER_RECV);
    } else {
      span.logEvent(Span.CLIENT_SEND);
      accessor.setHeader(MESSAGE_SENT_FROM_CLIENT, Boolean.TRUE.toString());
    }
    injector.inject(span, message);
    return span;
  }

  @Override
  public void afterSend(Exception ex) {
    final Span currentSpan = tracer.getCurrentSpan();
    if (containsServerReceived(currentSpan)) {
      currentSpan.logEvent(Span.SERVER_SEND);
    } else if (currentSpan != null) {
      currentSpan.logEvent(Span.CLIENT_RECV);
    }
    addErrorTag(ex);
    tracer.close(currentSpan);
  }

  private boolean containsServerReceived(Span span) {
    if (span == null) {
      return false;
    }
    for (Log log : span.logs()) {
      if (Span.SERVER_RECV.equals(log.getEvent())) {
        return true;
      }
    }
    return false;
  }

  private Span buildSpan(Message message) {
    return extractor.joinTrace(message);
  }

  private void addErrorTag(Exception ex) {
    if (ex != null) {
      tracer.addTag(Span.SPAN_ERROR_TAG_NAME, ExceptionUtils.getExceptionMessage(ex));
    }
  }
}
