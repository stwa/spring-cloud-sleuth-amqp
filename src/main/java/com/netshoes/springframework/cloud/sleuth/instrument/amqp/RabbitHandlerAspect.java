package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * This Aspect intercept methods annotated with {@link RabbitListener} for add tracing information
 * when a {@link org.springframework.amqp.support.converter.MessageConverter} isn't necessary.
 *
 * @author André Ignacio
 * @author Dominik Bartholdi
 * @since 0.9
 */
@Aspect
public class RabbitHandlerAspect extends AbstractRabbitMessageReceiverAspect {

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
    return super.executeAroundMessageReceive(call);
  }
}
