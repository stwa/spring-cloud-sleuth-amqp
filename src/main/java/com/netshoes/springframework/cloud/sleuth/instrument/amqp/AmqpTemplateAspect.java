package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Aspect responsible for get the current {@link Message} before execution of {@link
 * org.springframework.amqp.core.AmqpTemplate} methods and invoke {@link
 * AmqpMessagingSpanManager#beforeSend(Message, String)}.
 *
 * @see AmqpMessagingSpanInjector
 * @author André Ignacio
 */
@Aspect
public class AmqpTemplateAspect {
  private final AmqpMessagingSpanManager spanManager;

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   */
  public AmqpTemplateAspect(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.send(..))")
  public void executeAroundSend(ProceedingJoinPoint call) throws Throwable {
    execute(call);
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.sendAndReceive(..))")
  public void executeAroundSendAndReceive(ProceedingJoinPoint call) throws Throwable {
    execute(call);
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(..))")
  public void executeAroundConvertAndSend(ProceedingJoinPoint call) throws Throwable {
    execute(call);
  }

  private void execute(ProceedingJoinPoint call) throws Throwable {
    final Object[] args = call.getArgs();
    final Message message = getMessageFromArguments(args);

    String exchange = getExchangeFromArguments(args);
    if (exchange == null) {
      exchange = getExchangeFromRabbitTemplate(call.getTarget());
    }
    before(message, exchange);
    try {
      call.proceed(args);
    } catch (Exception e) {
      spanManager.afterSend(e);
      throw e;
    }
    spanManager.afterSend(null);
  }

  private void before(Message message, String exchange) {
    if (message != null) {
      spanManager.beforeSend(message, exchange);
    }
  }

  private String getExchangeFromRabbitTemplate(Object target) {
    String exchange = "";
    if (target instanceof RabbitTemplate) {
      exchange = ((RabbitTemplate) target).getExchange();
    }
    return exchange;
  }

  private String getExchangeFromArguments(Object[] args) {
    if (args.length > 1) {
      if (args[0] instanceof String && args[1] instanceof String) {
        return (String) args[0];
      }
    }
    return null;
  }

  private Message getMessageFromArguments(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof Message) {
        return (Message) arg;
      }
    }
    return null;
  }
}
