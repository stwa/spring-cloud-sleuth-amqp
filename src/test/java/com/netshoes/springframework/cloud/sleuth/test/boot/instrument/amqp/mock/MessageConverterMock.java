package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

/**
 * Mocked implementation for a MessageConverter
 *
 * @since 0.9
 */
public class MessageConverterMock implements MessageConverter {
  private final Logger logger = LoggerFactory.getLogger(MessageConverterMock.class);
  private final RabbitAspectMockManager mockManager;
  private final SimpleMessageConverter simpleMessageConverter;

  public MessageConverterMock(
      RabbitAspectMockManager mockManager, SimpleMessageConverter simpleMessageConverter) {
    this.mockManager = mockManager;
    this.simpleMessageConverter = simpleMessageConverter;
  }

  @Override
  public Message toMessage(Object object, MessageProperties messageProperties)
      throws MessageConversionException {

    mockManager.throwExceptionIfConfigured();
    logger.info("Converting from {} to message", String.valueOf(object));

    return simpleMessageConverter.toMessage(object, messageProperties);
  }

  @Override
  public Object fromMessage(Message message) throws MessageConversionException {
    mockManager.throwExceptionIfConfigured();

    logger.info("Converting from message {}", String.valueOf(message.getBody()));

    return simpleMessageConverter.fromMessage(message);
  }
}
