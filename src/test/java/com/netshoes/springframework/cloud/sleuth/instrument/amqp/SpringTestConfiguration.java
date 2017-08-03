package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.AmqpTemplateMock;
import com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock.RabbitListenerMock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
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
  public RabbitListenerMock rabbitListener() {
    return new RabbitListenerMock();
  }

  @Bean
  public AmqpTemplate rabbitTemplate() {
    return new AmqpTemplateMock();
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
  public AmqpMessagingSpanManager spanManager() {
    return Mockito.mock(AmqpMessagingSpanManager.class);
  }
}
