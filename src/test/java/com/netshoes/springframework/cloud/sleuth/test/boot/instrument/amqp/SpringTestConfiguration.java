package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpTemplateAspect;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.RabbitHandlerAspect;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.RabbitListenerAspect;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.SpanManagerMessagePostProcessor;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.AmqpTemplateMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitAspectMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitHandlerMock;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitListenerMock;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitTemplateMock;
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
  public RabbitListenerMock rabbitListener(RabbitAspectMockManager mockManager) {
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
  public RabbitHandlerMock rabbitHandlerListener(RabbitAspectMockManager mockManager) {
    return new RabbitHandlerMock(mockManager);
  }

  @Bean
  public RabbitHandlerAspect rabbitHandlerAspect(AmqpMessagingSpanManager spanManager) {
    return new RabbitHandlerAspect(spanManager);
  }

  @Bean
  public RabbitAspectMockManager rabbitAspectMockManager() {
    return new RabbitAspectMockManager();
  }
}
