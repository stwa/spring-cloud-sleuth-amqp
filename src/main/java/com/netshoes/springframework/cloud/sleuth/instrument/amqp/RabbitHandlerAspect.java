package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;

/**
 * This Aspect intercept methods annotated with {@link
 * org.springframework.amqp.rabbit.annotation.RabbitHandler} and invoke {@link
 * AmqpMessagingSpanManager#beforeHandle(Message)} with {@link Message}.
 *
 * @author André Ignacio
 * @author Dominik Bartholdi
 */
@Aspect
public class RabbitHandlerAspect extends AbstractRabbitAspect {

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public RabbitHandlerAspect(AmqpMessagingSpanManager spanManager) {
    super(spanManager);
  }

  @Around("@annotation(org.springframework.amqp.rabbit.annotation.RabbitHandler)")
  public Object executeAroundRabbitHandlerAnnotation(ProceedingJoinPoint call) throws Throwable {
    return super.executeAround(call);
  }
}
