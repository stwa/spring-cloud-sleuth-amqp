package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;

/**
 * Aspect responsible for get the current {@link Message} before execution of {@link
 * org.springframework.amqp.rabbit.core.RabbitTemplate} methods and invoke {@link
 * AmqpMessagingSpanManager#injectCurrentSpan(Message)}.
 *
 * @see AmqpMessagingSpanInjector
 * @author André Ignacio
 */
@Aspect
public class RabbitTemplateAspect {
  private final AmqpMessagingSpanManager spanManager;

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public RabbitTemplateAspect(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  @Around("execution(* org.springframework.amqp.rabbit.core.RabbitTemplate.send(..))")
  public void executeAroundSend(ProceedingJoinPoint call) throws Throwable {
    final Object[] args = call.getArgs();
    final Message message = getMessageArgument(args);
    before(message);
    call.proceed(args);
  }

  @Around("execution(* org.springframework.amqp.rabbit.core.RabbitTemplate.sendAndReceive(..))")
  public void executeAroundSendAndReceive(ProceedingJoinPoint call) throws Throwable {
    final Object[] args = call.getArgs();
    final Message message = getMessageArgument(args);
    before(message);
    call.proceed(args);
  }

  private void before(Message message) {
    if (message != null) {
      spanManager.injectCurrentSpan(message);
    }
  }

  private Message getMessageArgument(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof Message) {
        return (Message) arg;
      }
    }
    return null;
  }
}
