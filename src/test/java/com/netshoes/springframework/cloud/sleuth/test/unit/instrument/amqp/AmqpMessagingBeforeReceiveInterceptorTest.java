package com.netshoes.springframework.cloud.sleuth.test.unit.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingBeforeReceiveInterceptor;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;

/**
 * Unit tests for {@link AmqpMessagingBeforeReceiveInterceptor}.
 *
 * @author André Ignacio
 */
@RunWith(MockitoJUnitRunner.class)
public class AmqpMessagingBeforeReceiveInterceptorTest {
  @Mock private AmqpMessagingSpanManager spanManager;

  private AmqpMessagingBeforeReceiveInterceptor interceptor;

  @Before
  public void setup() {
    interceptor = new AmqpMessagingBeforeReceiveInterceptor(spanManager);
  }

  @Test
  public void testPostProcessMessageOneArgumentSuccess() throws Throwable {
    final Message message = Mockito.mock(Message.class);
    final MethodInvocation invocation = Mockito.mock(MethodInvocation.class);
    Mockito.when(invocation.getArguments()).thenReturn(new Object[] {message});

    interceptor.invoke(invocation);
    Mockito.verify(invocation).proceed();

    Mockito.verify(spanManager).beforeHandle(Matchers.eq(message));
  }

  @Test
  public void testPostProcessMessageTwoArgumentsSuccess() throws Throwable {
    final Message message = Mockito.mock(Message.class);
    final MethodInvocation invocation = Mockito.mock(MethodInvocation.class);
    Mockito.when(invocation.getArguments()).thenReturn(new Object[] {"some string", message});

    interceptor.invoke(invocation);

    Mockito.verify(spanManager).beforeHandle(Matchers.eq(message));
    Mockito.verify(invocation).proceed();
  }

  @Test(expected = IllegalStateException.class)
  public void testPostProcessMessageNoMessageArgument() throws Throwable {
    final MethodInvocation invocation = Mockito.mock(MethodInvocation.class);
    Mockito.when(invocation.getArguments()).thenReturn(new Object[] {"some string"});

    interceptor.invoke(invocation);
  }
}
