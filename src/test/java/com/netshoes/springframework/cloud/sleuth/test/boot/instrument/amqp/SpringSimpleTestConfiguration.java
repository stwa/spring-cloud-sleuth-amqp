package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpTemplateAspect;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.RabbitListenerAspect;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.SpanManagerMessagePostProcessor;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.AmqpTemplateMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitTemplateMock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

/**
 * Simple configuration for tests with Spring.
 *
 * @author André Ignacio
 */
public class SpringSimpleTestConfiguration {

  @Bean
  public MessageConverter messageConverter() {
    return new ContentTypeDelegatingMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      AmqpTemplateMockManager mockManager, MessageConverter messageConverter) {
    return new RabbitTemplateMock(mockManager, messageConverter, "my-default-exchange");
  }

  @Bean
  public RabbitListenerAspect rabbitListenerAspect(AmqpMessagingSpanManager spanManager) {
    return new RabbitListenerAspect(spanManager);
  }

  @Bean
  public AmqpTemplateAspect amqpTemplateAspect(AmqpMessagingSpanManager spanManager) {
    return new AmqpTemplateAspect(spanManager);
  }

  @Bean
  public SpanManagerMessagePostProcessor amqpMessagingBeforePublishPostProcessor(
      AmqpMessagingSpanManager spanManager) {
    return new SpanManagerMessagePostProcessor(spanManager, "my-exchange");
  }
}
