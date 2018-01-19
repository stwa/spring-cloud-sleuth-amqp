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
  private final RabbitAspectMockManager mockManager;

  public RabbitListenerMock(RabbitAspectMockManager mockManager) {
    this.mockManager = mockManager;
  }

  @RabbitListener(queues = "test-queue")
  public void onMessage(Message message) {
    mockManager.throwExceptionIfConfigured();
    logger.info("Message {} received.", String.valueOf(message.getBody()));
  }

  @RabbitListener(queues = "test-queue-with-reply")
  public Message onMessageWithReply(Message message) {

    mockManager.throwExceptionIfConfigured();
    logger.info("Message {} received.", String.valueOf(message.getBody()));

    return message;
  }
}
