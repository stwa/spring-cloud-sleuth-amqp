package com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp;

import static org.mockito.Mockito.mock;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.AmqpMessagingSpanManager;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.RabbitHandlerAspect;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.AmqpTemplateMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.MessageConverterMock;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitAspectMockManager;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitHandlerMock;
import com.netshoes.springframework.cloud.sleuth.test.boot.instrument.amqp.mock.RabbitListenerMock;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Simple configuration for tests with Spring.
 *
 * @author André Ignacio
 */
@EnableAspectJAutoProxy
public class SpringMockTestConfiguration {
  @Bean
  public AmqpMessagingSpanManager spanManager() {
    return mock(AmqpMessagingSpanManager.class);
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
  public MessageConverter messageConverter(RabbitAspectMockManager mockManager) {
    return new MessageConverterMock(mockManager, new SimpleMessageConverter());
  }

  @Bean
  public RabbitHandlerAspect rabbitHandlerAspect(AmqpMessagingSpanManager spanManager) {
    return new RabbitHandlerAspect(spanManager);
  }

  @Bean
  public RabbitAspectMockManager rabbitAspectMockManager() {
    return new RabbitAspectMockManager();
  }

  @Bean
  public RabbitListenerMock rabbitListener(RabbitAspectMockManager mockManager) {
    return new RabbitListenerMock(mockManager);
  }
}
