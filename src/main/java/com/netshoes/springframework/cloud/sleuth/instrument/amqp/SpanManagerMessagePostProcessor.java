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

  /**
   * Creates a new instance.
   *
   * @param spanManager AMQP span messaging manager
   */
  public SpanManagerMessagePostProcessor(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  @Override
  public Message postProcessMessage(Message message) throws AmqpException {
    // TODO Discover exchange before POST message
    spanManager.beforeSend(message, "unknown");
    return message;
  }
}
