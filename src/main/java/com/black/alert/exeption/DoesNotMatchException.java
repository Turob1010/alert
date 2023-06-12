package com.black.alert.exeption;

public class DoesNotMatchException extends RuntimeException {

  public DoesNotMatchException() {}

  public DoesNotMatchException(final String message) {
    super(message);
  }

  public DoesNotMatchException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DoesNotMatchException(final Throwable cause) {
    super(cause);
  }

  public DoesNotMatchException(
      final String message,
      final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
