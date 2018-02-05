package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utility methods for tests in message receivers.
 *
 * @author André Ignacio
 * @since 0.9
 */
public abstract class AbstractMessageReceiverTest {
  @Autowired private MessageConverter messageConverter;

  protected Message mockSendMessageBehavior(String body) {
    return messageConverter.toMessage(body, new MessageProperties());
  }

  protected String mockMessagingMessageListenerAdapterBehaviour(Message message) {
    return (String) messageConverter.fromMessage(message);
  }
}
