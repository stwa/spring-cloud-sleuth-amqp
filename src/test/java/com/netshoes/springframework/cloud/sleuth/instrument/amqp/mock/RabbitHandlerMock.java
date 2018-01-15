package com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock;

import jdk.nashorn.internal.runtime.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;

/**
 * Mocked implementation for a Rabbit handler.
 *
 * @version 1.0
 */
@RabbitListener(bindings = {
        @QueueBinding(
                value = @Queue(value = "test-handler-queue", autoDelete = "true"),
                exchange = @Exchange(value = "test-handler-exchange", autoDelete = "true", type = "direct"),
                key = "test-handler-key")}
)
public class RabbitHandlerMock {
    private final Logger logger = LoggerFactory.getLogger(RabbitHandlerMock.class);
    private final RabbitHandlerMockManager mockManager;

    public RabbitHandlerMock(RabbitHandlerMockManager mockManager) {
        this.mockManager = mockManager;
    }

    @RabbitHandler
    public void onMessage(Message message) {
        mockManager.throwExceptionIfConfigured();
        logger.info("Message {} received.", String.valueOf(message.getBody()));
    }

    @RabbitHandler
    public Message onMessageWithReply(Message message) {

        mockManager.throwExceptionIfConfigured();
        logger.info("Message {} received.", String.valueOf(message.getBody()));

        return message;
    }
}
