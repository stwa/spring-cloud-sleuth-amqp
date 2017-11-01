package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.AmqpTemplateMockManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link AmqpTemplateAspect}.
 *
 * @author André Ignacio
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AmqpTemplateAspectTest {
  @Autowired private AmqpTemplate amqpTemplate;
  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;
  @Autowired private AmqpTemplateMockManager mockManager;

  @Test
  public void aspectInvokeSendSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message = new Message("body-send".getBytes(), new MessageProperties());
    amqpTemplate.send(message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message = new Message("body-send".getBytes(), new MessageProperties());

    assertThatThrownBy(() -> amqpTemplate.send(message)).isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message = new Message("body-send-rk".getBytes(), new MessageProperties());
    amqpTemplate.send("rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message = new Message("body-send-rk".getBytes(), new MessageProperties());

    assertThatThrownBy(() -> amqpTemplate.send("rk", message))
        .isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.send("exchange", "rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("exchange"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeyAndExchangeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-exchange-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.send("exchange", "rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("exchange"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendAndReceiveSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive(message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendAndReceiveError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.sendAndReceive(message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-rk".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive("rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.sendAndReceive("rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive("exchange", "rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("exchange"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyAndExchangeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.sendAndReceive("exchange", "rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("exchange"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-convert-and-send".getBytes(), new MessageProperties());
    amqpTemplate.convertAndSend(message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-convert-and-send".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.convertAndSend(message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-convert-and-send-rk".getBytes(), new MessageProperties());
    amqpTemplate.convertAndSend("rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.convertAndSend("rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq(""));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.convertAndSend("exchange", "rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("exchange"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndExchangeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.convertAndSend("exchange", "rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("exchange"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }
}
