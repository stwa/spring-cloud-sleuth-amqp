package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;

/**
 * Span Manager responsible inject and extract span from AMQP {@link Message}.
 *
 * @author André Ignacio
 * @since 0.8
 */
public interface AmqpMessagingSpanManager {
  Span beforeHandle(Message message);

  void afterHandle(Exception ex);

  Span beforeSend(Message message, String spanName);

  void afterSend(Exception ex);
}
