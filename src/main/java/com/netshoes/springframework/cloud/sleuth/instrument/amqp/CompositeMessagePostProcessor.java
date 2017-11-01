package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * Post processor responsible to chain two or more {@link MessagePostProcessor}.
 *
 * @author André Ignacio
 */
public class CompositeMessagePostProcessor implements MessagePostProcessor {
  private final List<MessagePostProcessor> postProcessorList;

  public CompositeMessagePostProcessor(MessagePostProcessor... postProcessors) {
    postProcessorList = new ArrayList<>();
    for (MessagePostProcessor processor : postProcessors) {
      postProcessorList.add(processor);
    }
  }

  @Override
  public Message postProcessMessage(Message message) throws AmqpException {
    Message localMessage = message;
    for (MessagePostProcessor postProcessor : postProcessorList) {
      localMessage = postProcessor.postProcessMessage(localMessage);
    }
    return localMessage;
  }
}
