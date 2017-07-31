package com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Mocked implementation for a Rabbit listener.
 *
 * @version 1.0
 */
public class RabbitListenerMock {
  private final Logger logger = LoggerFactory.getLogger(RabbitListenerMock.class);

  @RabbitListener(queues = "test-queue")
  public void onMessage(Message message) {
    logger.info("Message {} received.", String.valueOf(message.getBody()));
  }
}
