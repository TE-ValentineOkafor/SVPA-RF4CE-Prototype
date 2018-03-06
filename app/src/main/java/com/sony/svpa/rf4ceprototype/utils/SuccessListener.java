package com.sony.svpa.rf4ceprototype.utils;

/**
 * Standardized listener for an async response.
 * <p>
 * Contract is to always receive {@link #onSuccess(Object)}.
 * Use {@link ResponseListener} if there is any possibility of an error occurring.
 *
 * @param <T> Type of response object expected.
 * @see ResponseListener
 */

public interface SuccessListener<T> {

  /**
   * Receive a successful response.
   *
   * @param result Response.
   */
  void onSuccess(T result);
  void onChannelUp();
  void onChannelDown();

}
