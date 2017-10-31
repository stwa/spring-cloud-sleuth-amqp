package com.netshoes.springframework.cloud.sleuth.instrument.amqp.mock;

public class RabbitListenerMockManager {
  private boolean throwException = false;
  private RuntimeException exception;

  public synchronized void throwExceptionIfConfigured() {
    if (throwException && exception != null) {
      throwException = false;
      throw exception;
    }
  }

  public void throwExceptionInNextMessage(RuntimeException e) {
    this.exception = e;
    this.throwException = true;
  }
}
