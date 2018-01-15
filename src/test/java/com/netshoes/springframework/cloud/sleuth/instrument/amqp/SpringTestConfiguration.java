package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.*;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for tests with Spring.
 *
 * @author André Ignacio
 */
@SpringBootConfiguration
@EnableAspectJAutoProxy
public class SpringTestConfiguration {

  @Bean
  public RabbitListenerMock rabbitListener(RabbitListenerMockManager mockManager) {
    return new RabbitListenerMock(mockManager);
  }

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
  public SpanManagerMessagePostProcessor amqpMessagingBeforePublishPostProcessor(
      AmqpMessagingSpanManager spanManager) {
    return new SpanManagerMessagePostProcessor(spanManager, "my-exchange");
  }

  @Bean
  public AmqpTemplateAspect amqpTemplateAspect(AmqpMessagingSpanManager spanManager) {
    return new AmqpTemplateAspect(spanManager);
  }

  @Bean
  public AmqpMessagingSpanManager spanManager() {
    return Mockito.mock(AmqpMessagingSpanManager.class);
  }

  @Bean
  public AmqpTemplateMockManager amqpTemplateMockManager() {
    return new AmqpTemplateMockManager();
  }

  @Bean
  public RabbitListenerMockManager rabbitListenerMockManager() {
    return new RabbitListenerMockManager();
  }

  @Bean
  public RabbitHandlerMock rabbitHandlerListener(RabbitHandlerMockManager mockManager) {
    return new RabbitHandlerMock(mockManager);
  }

  @Bean
  public RabbitHandlerAspect rabbitHandlerAspect(AmqpMessagingSpanManager spanManager) {
    return new RabbitHandlerAspect(spanManager);
  }

  @Bean
  public RabbitHandlerMockManager rabbitHandlerMockManager() {
    return new RabbitHandlerMockManager();
  }

}
