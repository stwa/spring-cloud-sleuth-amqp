package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.lang.reflect.Method;

/**
 * This Aspect intercept methods annotated with {@link org.springframework.amqp.rabbit.annotation.RabbitHandler} and invoke {@link
 * AmqpMessagingSpanManager#beforeHandle(Message, String[])} with {@link Message} and queue names.
 *
 * @author André Ignacio
 * @author Dominik Bartholdi
 */
@Aspect
public class RabbitHandlerAspect {
  private final AmqpMessagingSpanManager spanManager;

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public RabbitHandlerAspect(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  @Around("@annotation(org.springframework.amqp.rabbit.annotation.RabbitHandler)")
  public Object executeAroundRabbitHandlerAnnotation(ProceedingJoinPoint call) throws Throwable {
    final Object[] args = call.getArgs();
    final Message message = getMessageArgument(args);
    final String[] queueNames = getQueueNames(call);
    if (message != null) {
      spanManager.beforeHandle(message, queueNames);
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

  /**
   * Get queues names separated with comma from {@link RabbitListener} annotation.
   *
   * @param call Call
   * @return Queues names
   */
  private String[] getQueueNames(ProceedingJoinPoint call) {
    final MethodSignature signature = (MethodSignature) call.getSignature();
    Class<?> declaringClass = signature.getMethod().getDeclaringClass();
    final RabbitListener rabbitListenerAnnotation = declaringClass.getAnnotation(RabbitListener.class);
    String[] queues = rabbitListenerAnnotation.queues();
    if(queues == null || queues.length == 0){
      QueueBinding[] bindings = rabbitListenerAnnotation.bindings();
      queues = new String[bindings.length];
      for (int i = 0; i < bindings.length; i++) {
          queues[i] = bindings[i].value().value();
      }
    }
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
