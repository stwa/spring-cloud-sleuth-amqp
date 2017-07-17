package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * This Aspect intercept methods annotated with {@link RabbitListener} and invoke {@link
 * AmqpMessagingSpanManager#extractAndContinueSpan(Message, String[])} with {@link Message} and
 * queue names.
 *
 * @author André Ignacio
 */
@Aspect
public class RabbitListenerAspect {
  private final AmqpMessagingSpanManager spanManager;

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public RabbitListenerAspect(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  @Around("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
  public void executeAroundRabbitListenerAnnotation(ProceedingJoinPoint call) throws Throwable {
    final Object[] args = call.getArgs();
    final Message message = getMessageArgument(args);
    final String[] queueNames = getQueueNames(call);
    before(message, queueNames);
    call.proceed();
  }

  private void before(Message message, String[] queueNames) {
    if (message != null) {
      spanManager.extractAndContinueSpan(message, queueNames);
    }
  }

  /**
   * Get queues names separated with comma from {@link RabbitListener} annotation.
   *
   * @param call Call
   * @return Queues names
   */
  private String[] getQueueNames(ProceedingJoinPoint call) {
    final MethodSignature signature = (MethodSignature) call.getSignature();
    final Method method = signature.getMethod();
    final RabbitListener rabbitListenerAnnotation = method.getAnnotation(RabbitListener.class);
    final String[] queues = rabbitListenerAnnotation.queues();
    return queues;
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
