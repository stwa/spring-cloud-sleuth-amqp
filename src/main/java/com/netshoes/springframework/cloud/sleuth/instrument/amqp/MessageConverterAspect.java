package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;

/**
 * This Aspect intercept method fromMessage of {@link
 * org.springframework.amqp.support.converter.MessageConverter} and invoke {@link
 * AmqpMessagingSpanManager#beforeHandle(Message)} with {@link Message}.
 *
 * @author André Ignacio
 * @since 0.9
 */
@Aspect
public class MessageConverterAspect extends AbstractRabbitMessageReceiverAspect {
  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public MessageConverterAspect(AmqpMessagingSpanManager spanManager) {
    super(spanManager);
  }

  @Around(
      "execution(* org.springframework.amqp.support.converter.MessageConverter.fromMessage(..))")
  public Object aroundFromMessage(ProceedingJoinPoint call) throws Throwable {
    return super.executeAroundMessageReceive(call);
  }
}
