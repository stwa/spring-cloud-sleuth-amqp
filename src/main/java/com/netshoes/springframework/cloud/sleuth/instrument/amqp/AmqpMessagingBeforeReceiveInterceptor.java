package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;

/**
 * Interceptor responsible for call {@link AmqpMessagingSpanManager#extractAndContinueSpan(Message, String[])}
 * with the current {@link Message}.
 *
 * @see AmqpMessagingSpanManager
 * @author André Ignacio
 */
public class AmqpMessagingBeforeReceiveInterceptor implements MethodInterceptor {
  private static final String[] QUEUE_NAMES = new String[] {"amqp"};
  private final AmqpMessagingSpanManager spanManager;

  /**
   * Creates a new instance.
   *
   * @param spanManager AMQP messaging span manager
   */
  public AmqpMessagingBeforeReceiveInterceptor(AmqpMessagingSpanManager spanManager) {
    this.spanManager = spanManager;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    final Message message = getMessageArgument(invocation.getArguments());
    if (message == null) {
      throw new IllegalStateException("Message cannot be null");
    }
    before(message);
    return invocation.proceed();
  }

  private void before(Message message) {
    spanManager.extractAndContinueSpan(message, QUEUE_NAMES);
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
