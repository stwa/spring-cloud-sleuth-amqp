package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.AmqpTemplateMock;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.AmqpTemplateMockManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.RabbitListenerMock;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.RabbitListenerMockManager;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
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
  public AmqpTemplate rabbitTemplate(
      AmqpTemplateMockManager mockManager, MessageConverter messageConverter) {
    return new AmqpTemplateMock(mockManager, messageConverter);
  }

  @Bean
  public RabbitListenerAspect rabbitListenerAspect(AmqpMessagingSpanManager spanManager) {
    return new RabbitListenerAspect(spanManager);
  }

  @Bean
  public SpanManagerMessagePostProcessor amqpMessagingBeforePublishPostProcessor(
      AmqpMessagingSpanManager spanManager) {
    return new SpanManagerMessagePostProcessor(spanManager);
  }

  @Bean
  public AmqpTemplateAspect amqpTemplateAspect(
      AmqpMessagingSpanManager spanManager, SpanManagerMessagePostProcessor postProcessor) {
    return new AmqpTemplateAspect(spanManager, postProcessor);
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
}
