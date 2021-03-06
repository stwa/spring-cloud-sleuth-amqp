package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Mocked implementation for a Rabbit handler.
 *
 * @since 0.9
 */
@RabbitListener(
  bindings = {
    @QueueBinding(
      value = @Queue(value = "test-handler-queue", autoDelete = "true"),
      exchange = @Exchange(value = "test-handler-exchange", autoDelete = "true", type = "direct"),
      key = "test-handler-key"
    )
  }
)
public class RabbitHandlerMock {
  private final Logger logger = LoggerFactory.getLogger(RabbitHandlerMock.class);
  private final RabbitAspectMockManager mockManager;

  public RabbitHandlerMock(RabbitAspectMockManager mockManager) {
    this.mockManager = mockManager;
  }

  @RabbitHandler
  public void onMessage(String stringMessage) {
    mockManager.throwExceptionIfConfigured();
    logger.info("Message {} received.", stringMessage);
  }
}
