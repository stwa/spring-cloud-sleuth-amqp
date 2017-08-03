package com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.core.ReplyToAddressCallback;

/**
 * Mocked implementation for a {@link AmqpTemplate}.
 *
 * @version 1.0
 */
public class AmqpTemplateMock implements AmqpTemplate {
  private final Logger logger = LoggerFactory.getLogger(AmqpTemplateMock.class);

  @Override
  public void send(Message message) throws AmqpException {
    logger.debug("send: {}", message);
  }

  @Override
  public void send(String routingKey, Message message) throws AmqpException {
    logger.debug("send: {} {}", routingKey, message);
  }

  @Override
  public void send(String exchange, String routingKey, Message message) throws AmqpException {
    logger.debug("send: {} {} {}", exchange, routingKey, message);
  }

  @Override
  public void convertAndSend(Object message) throws AmqpException {
    logger.debug("convertAndSend: {}", message);
  }

  @Override
  public void convertAndSend(String routingKey, Object message) throws AmqpException {
    logger.debug("convertAndSend: {} {}", routingKey, message);
  }

  @Override
  public void convertAndSend(String exchange, String routingKey, Object message)
      throws AmqpException {
    logger.debug("convertAndSend: {} {} {}", exchange, routingKey, message);
  }

  @Override
  public void convertAndSend(Object message, MessagePostProcessor messagePostProcessor)
      throws AmqpException {
    logger.debug("convertAndSend: {} {}", message, messagePostProcessor);
  }

  @Override
  public void convertAndSend(
      String routingKey, Object message, MessagePostProcessor messagePostProcessor)
      throws AmqpException {
    logger.debug("convertAndSend: {} {} {}", routingKey, message, messagePostProcessor);
  }

  @Override
  public void convertAndSend(
      String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor)
      throws AmqpException {
    logger.debug(
        "convertAndSend: {} {} {} {}", exchange, routingKey, message, messagePostProcessor);
  }

  @Override
  public Message receive() throws AmqpException {
    logger.debug("receive");
    return null;
  }

  @Override
  public Message receive(String queueName) throws AmqpException {
    logger.debug("receive: {}", queueName);
    return null;
  }

  @Override
  public Message receive(long timeoutMillis) throws AmqpException {
    logger.debug("receive: {}", timeoutMillis);
    return null;
  }

  @Override
  public Message receive(String queueName, long timeoutMillis) throws AmqpException {
    logger.debug("receive: {} {}", queueName, timeoutMillis);
    return null;
  }

  @Override
  public Object receiveAndConvert() throws AmqpException {
    logger.debug("receiveAndConvert");
    return null;
  }

  @Override
  public Object receiveAndConvert(String queueName) throws AmqpException {
    logger.debug("receiveAndConvert: {}", queueName);
    return null;
  }

  @Override
  public Object receiveAndConvert(long timeoutMillis) throws AmqpException {
    logger.debug("receiveAndConvert: {}", timeoutMillis);
    return null;
  }

  @Override
  public Object receiveAndConvert(String queueName, long timeoutMillis) throws AmqpException {
    logger.debug("receiveAndConvert: {} {}", queueName, timeoutMillis);
    return null;
  }

  @Override
  public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback)
      throws AmqpException {
    logger.debug("receiveAndReply: {}", callback);
    return false;
  }

  @Override
  public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback)
      throws AmqpException {
    logger.debug("receiveAndReply: {} {}", queueName, callback);
    return false;
  }

  @Override
  public <R, S> boolean receiveAndReply(
      ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey)
      throws AmqpException {
    logger.debug("receiveAndReply: {} {} {}", callback, replyExchange, replyRoutingKey);
    return false;
  }

  @Override
  public <R, S> boolean receiveAndReply(
      String queueName,
      ReceiveAndReplyCallback<R, S> callback,
      String replyExchange,
      String replyRoutingKey)
      throws AmqpException {
    logger.debug("receiveAndReply: {} {} {}", queueName, callback, replyExchange, replyRoutingKey);
    return false;
  }

  @Override
  public <R, S> boolean receiveAndReply(
      ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback)
      throws AmqpException {
    logger.debug("receiveAndReply: {} {}", callback, replyToAddressCallback);
    return false;
  }

  @Override
  public <R, S> boolean receiveAndReply(
      String queueName,
      ReceiveAndReplyCallback<R, S> callback,
      ReplyToAddressCallback<S> replyToAddressCallback)
      throws AmqpException {
    logger.debug("receiveAndReply: {} {} {}", queueName, callback, replyToAddressCallback);
    return false;
  }

  @Override
  public Message sendAndReceive(Message message) throws AmqpException {
    logger.debug("sendAndReceive: {}", message);
    return null;
  }

  @Override
  public Message sendAndReceive(String routingKey, Message message) throws AmqpException {
    logger.debug("sendAndReceive: {} {}", routingKey, message);
    return null;
  }

  @Override
  public Message sendAndReceive(String exchange, String routingKey, Message message)
      throws AmqpException {
    logger.debug("sendAndReceive: {} {} {}", exchange, routingKey, message);
    return null;
  }

  @Override
  public Object convertSendAndReceive(Object message) throws AmqpException {
    logger.debug("convertSendAndReceive: {}", message);
    return null;
  }

  @Override
  public Object convertSendAndReceive(String routingKey, Object message) throws AmqpException {
    logger.debug("convertSendAndReceive: {} {}", routingKey, message);
    return null;
  }

  @Override
  public Object convertSendAndReceive(String exchange, String routingKey, Object message)
      throws AmqpException {
    logger.debug("convertSendAndReceive: {} {} {}", exchange, routingKey, message);
    return null;
  }

  @Override
  public Object convertSendAndReceive(Object message, MessagePostProcessor messagePostProcessor)
      throws AmqpException {
    logger.debug("convertSendAndReceive: {} {}", message, messagePostProcessor);
    return null;
  }

  @Override
  public Object convertSendAndReceive(
      String routingKey, Object message, MessagePostProcessor messagePostProcessor)
      throws AmqpException {
    logger.debug("convertSendAndReceive: {} {}", routingKey, message, messagePostProcessor);
    return null;
  }

  @Override
  public Object convertSendAndReceive(
      String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor)
      throws AmqpException {
    logger.debug(
        "convertSendAndReceive: {} {}", exchange, routingKey, message, messagePostProcessor);
    return null;
  }
}
