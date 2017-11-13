package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * This message post processor invoke method {@link
 * AmqpMessagingSpanManager#beforeSend(Message,String)}.
 *
 * @see AmqpMessagingSpanManager
 * @author André Ignacio
 */
public class SpanManagerMessagePostProcessor implements MessagePostProcessor {
  private final AmqpMessagingSpanManager spanManager;
  private final String spanName;

  /**
   * Creates a new instance.
   *
   * @param spanManager AMQP span messaging manager
   * @param spanName Name of Span
   */
  public SpanManagerMessagePostProcessor(AmqpMessagingSpanManager spanManager, String spanName) {
    this.spanManager = spanManager;
    this.spanName = spanName;
  }

  @Override
  public Message postProcessMessage(Message message) throws AmqpException {
    spanManager.beforeSend(message, spanName);
    return message;
  }
}
