package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.amqp.core.Message;

/**
 * This abstract Aspect is used to intercept methods annotated with {@link
 * org.springframework.amqp.rabbit.annotation.RabbitListener} and {@link
 * org.springframework.amqp.rabbit.annotation.RabbitHandler} and invoke {@link
 * AmqpMessagingSpanManager#beforeHandle(Message)} with {@link Message}.
 *
 * @author André Ignacio
 * @author Dominik Bartholdi
 */
public abstract class AbstractRabbitAspect {
  private final AmqpMessagingSpanManager spanManager;

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public AbstractRabbitAspect(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  protected Object executeAround(ProceedingJoinPoint call) throws Throwable {
    final Object[] args = call.getArgs();
    final Message message = getMessageArgument(args);
    if (message != null) {
      spanManager.beforeHandle(message);
    }
    try {
      Object result = call.proceed();
      spanManager.afterHandle(null);

      return result;
    } catch (Exception e) {
      spanManager.afterHandle(e);
      throw e;
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
