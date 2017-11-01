package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Aspect responsible for get the current {@link Message} before execution of {@link
 * org.springframework.amqp.core.AmqpTemplate} methods and invoke {@link
 * AmqpMessagingSpanManager#beforeSend(Message, String)} and {@link
 * AmqpMessagingSpanManager#afterSend(Exception)}.
 *
 * @see AmqpMessagingSpanManager
 * @see SpanManagerMessagePostProcessor
 * @author André Ignacio
 */
@Aspect
public class AmqpTemplateAspect {
  private final AmqpMessagingSpanManager spanManager;
  private final SpanManagerMessagePostProcessor beforePublishPostProcessor;

  /**
   * Creates a new instance.
   *
   * @param spanManager Span manager for AMQP messaging
   * @param amqpMessagingBeforePublishPostProcessor Message post processor
   */
  public AmqpTemplateAspect(
      AmqpMessagingSpanManager spanManager,
      SpanManagerMessagePostProcessor amqpMessagingBeforePublishPostProcessor) {
    this.spanManager = spanManager;
    this.beforePublishPostProcessor = amqpMessagingBeforePublishPostProcessor;
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.send(..))")
  public void executeAroundSend(ProceedingJoinPoint call) throws Throwable {
    executeWithoutPostProcessor(call);
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.sendAndReceive(..))")
  public void executeAroundSendAndReceive(ProceedingJoinPoint call) throws Throwable {
    executeWithoutPostProcessor(call);
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(Object))")
  public void executeAroundConvertAndSendOneArg(ProceedingJoinPoint call) throws Throwable {
    executeConvertAndSendWithoutPostProcessor(call);
  }

  @Around("execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(String,Object))")
  public void executeAroundConvertAndSendTwoArgs(ProceedingJoinPoint call) throws Throwable {
    executeConvertAndSendWithoutPostProcessor(call);
  }

  @Around(
      "execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(String,String,Object))")
  public void executeAroundConvertAndSendThreeArgs(ProceedingJoinPoint call) throws Throwable {
    executeConvertAndSendWithoutPostProcessor(call);
  }

  @Around(
      "execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(Object,org.springframework.amqp.core.MessagePostProcessor))")
  public void executeAroundConvertAndSendOneArgWithProcessor(ProceedingJoinPoint call)
      throws Throwable {
    final MessagePostProcessor argPostProcessor = getMessagePostProcessor(call.getArgs());
    final boolean byPass = argPostProcessor instanceof SpanManagerMessagePostProcessor;
    if (byPass) {
      call.proceed(call.getArgs());
    } else {
      executeConvertAndSendWithoutPostProcessor(call);
    }
  }

  @Around(
      "execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(String,Object,org.springframework.amqp.core.MessagePostProcessor))")
  public void executeAroundConvertAndSendTwoArgsWithPostProcessor(ProceedingJoinPoint call)
      throws Throwable {
    final MessagePostProcessor argPostProcessor = getMessagePostProcessor(call.getArgs());
    final boolean byPass = argPostProcessor instanceof SpanManagerMessagePostProcessor;
    if (byPass) {
      call.proceed(call.getArgs());
    } else {
      executeConvertAndSendWithoutPostProcessor(call);
    }
  }

  @Around(
      "execution(* org.springframework.amqp.core.AmqpTemplate.convertAndSend(String,String,Object,org.springframework.amqp.core.MessagePostProcessor))")
  public void executeAroundConvertAndSendThreeArgsWithPostProcessor(ProceedingJoinPoint call)
      throws Throwable {
    final MessagePostProcessor argPostProcessor = getMessagePostProcessor(call.getArgs());
    final boolean byPass = argPostProcessor instanceof SpanManagerMessagePostProcessor;
    if (byPass) {
      call.proceed(call.getArgs());
    } else {
      executeConvertAndSendWithoutPostProcessor(call);
    }
  }

  private void executeConvertAndSendWithoutPostProcessor(ProceedingJoinPoint call)
      throws Throwable {
    final AmqpTemplate amqpTemplate = (AmqpTemplate) call.getThis();

    final Object[] args = call.getArgs();
    final MessagePostProcessor argPostProcessor = getMessagePostProcessor(args);

    boolean executed;

    try {
      if (argPostProcessor != null) {
        final MessagePostProcessor overwritePostProcessor =
            new CompositeMessagePostProcessor(argPostProcessor, beforePublishPostProcessor);
        args[args.length - 1] = overwritePostProcessor;
        call.proceed(args);
        executed = true;
      } else {
        executed = changeExecutionOfMethodToUsePostProcessor(amqpTemplate, args);
      }
    } catch (Exception e) {
      spanManager.afterSend(e);
      throw e;
    }
    if (executed) {
      spanManager.afterSend(null);
    } else {
      executeWithoutPostProcessor(call);
    }
  }

  private void executeWithoutPostProcessor(ProceedingJoinPoint call) throws Throwable {
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

  private boolean changeExecutionOfMethodToUsePostProcessor(
      AmqpTemplate amqpTemplate, Object[] args) {
    boolean executed = true;
    final int argsLength = args.length;
    switch (argsLength) {
      case 1:
        amqpTemplate.convertAndSend(args[0], beforePublishPostProcessor);
        break;
      case 2:
        amqpTemplate.convertAndSend((String) args[0], args[1], beforePublishPostProcessor);
        break;
      case 3:
        amqpTemplate.convertAndSend(
            (String) args[0], (String) args[1], args[2], beforePublishPostProcessor);
        break;
      default:
        executed = false;
        break;
    }
    return executed;
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

  private MessagePostProcessor getMessagePostProcessor(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof MessagePostProcessor) {
        return (MessagePostProcessor) arg;
      }
    }
    return null;
  }
}
