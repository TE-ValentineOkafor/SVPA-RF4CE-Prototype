package com.sony.svpa.rf4ceprototype.utils;

/**
 * Standardized listener for an async response.
 * <p>
 * Contract is to receive one of {@link #onSuccess(Object)} or {@link #onError(Throwable)}
 * but never both.
 */

public interface ResponseListener<T> {

  /**
   * Receive a successful response.
   *
   * @param result Response.
   */
  void onSuccess(T result);

  /**
   * An async error occurred.
   *
   * @param error The error that occurred.
   */
  void onError(Throwable error);
}
