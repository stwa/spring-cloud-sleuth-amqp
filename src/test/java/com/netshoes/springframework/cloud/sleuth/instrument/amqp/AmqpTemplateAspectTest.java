package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.AmqpTemplateMockManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
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
  @Mock private MessagePostProcessor messagePostProcessor;

  @Test
  public void aspectInvokeSendSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message = new Message("body-send".getBytes(), new MessageProperties());
    amqpTemplate.send(message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message = new Message("body-send".getBytes(), new MessageProperties());

    assertThatThrownBy(() -> amqpTemplate.send(message)).isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message = new Message("body-send-rk".getBytes(), new MessageProperties());
    amqpTemplate.send("rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message = new Message("body-send-rk".getBytes(), new MessageProperties());

    assertThatThrownBy(() -> amqpTemplate.send("rk", message))
        .isInstanceOf(NullPointerException.class);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.send("exchange", "rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendWithRoutingKeyAndExchangeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-exchange-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.send("exchange", "rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendAndReceiveSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive".getBytes(), new MessageProperties());
    Message replyMessage = amqpTemplate.sendAndReceive(message);

    Assert.assertEquals(replyMessage, message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendAndReceiveError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.sendAndReceive(message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-rk".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive("rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.sendAndReceive("rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    amqpTemplate.sendAndReceive("exchange", "rk", message);

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeSendAndReceiveWithRoutingKeyAndExchangeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    final Message message =
        new Message("body-send-and-receive-exchange-rk".getBytes(), new MessageProperties());
    assertThatThrownBy(() -> amqpTemplate.sendAndReceive("exchange", "rk", message));

    verify(amqpMessagingSpanManager).beforeSend(eq(message), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    amqpTemplate.convertAndSend("body-convert-and-send");

    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    assertThatThrownBy(() -> amqpTemplate.convertAndSend("body-convert-and-send"));

    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeySuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    amqpTemplate.convertAndSend("rk", "body-convert-and-send-rk");

    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    assertThatThrownBy(() -> amqpTemplate.convertAndSend("rk", "body-send-and-receive-rk"));

    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndExchangeSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    amqpTemplate.convertAndSend("exchange", "rk", "body-send-and-receive-exchange-rk");

    verify(amqpMessagingSpanManager).beforeSend(any(Message.class), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndExchangeError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    assertThatThrownBy(
        () -> amqpTemplate.convertAndSend("exchange", "rk", "body-send-and-receive-exchange-rk"));

    verify(amqpMessagingSpanManager).beforeSend(any(Message.class), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithPostProcessorSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    amqpTemplate.convertAndSend((Object) "body-convert-and-send", messagePostProcessor);

    verify(messagePostProcessor).postProcessMessage(any(Message.class));
    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithPostProcessorError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    assertThatThrownBy(
        () -> amqpTemplate.convertAndSend((Object) "body-convert-and-send", messagePostProcessor));

    verify(messagePostProcessor).postProcessMessage(any(Message.class));
    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/*"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndPostProcessorSuccess() throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    amqpTemplate.convertAndSend("rk", (Object) "body-convert-and-send-rk", messagePostProcessor);

    verify(messagePostProcessor).postProcessMessage(any(Message.class));
    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndPostProcessorError() throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    assertThatThrownBy(
        () ->
            amqpTemplate.convertAndSend(
                "rk", (Object) "body-send-and-receive-rk", messagePostProcessor));

    verify(messagePostProcessor).postProcessMessage(any(Message.class));
    verify(amqpMessagingSpanManager)
        .beforeSend(any(Message.class), eq("amqp://my-default-exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndExchangeAndPostProcessorSuccess()
      throws Throwable {
    Assert.assertNotNull(amqpTemplate);

    amqpTemplate.convertAndSend(
        "exchange", "rk", "body-send-and-receive-exchange-rk", messagePostProcessor);

    verify(messagePostProcessor).postProcessMessage(any(Message.class));
    verify(amqpMessagingSpanManager).beforeSend(any(Message.class), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(eq(null));
  }

  @Test
  public void aspectInvokeConvertAndSendWithRoutingKeyAndExchangeAndPostProcessorError()
      throws Throwable {
    Assert.assertNotNull(amqpTemplate);
    mockManager.throwExceptionInNextMethodCall(new NullPointerException());

    assertThatThrownBy(
        () ->
            amqpTemplate.convertAndSend(
                "exchange", "rk", "body-send-and-receive-exchange-rk", messagePostProcessor));

    verify(messagePostProcessor).postProcessMessage(any(Message.class));
    verify(amqpMessagingSpanManager).beforeSend(any(Message.class), eq("amqp://exchange/rk"));
    verify(amqpMessagingSpanManager).afterSend(any(NullPointerException.class));
  }
}
