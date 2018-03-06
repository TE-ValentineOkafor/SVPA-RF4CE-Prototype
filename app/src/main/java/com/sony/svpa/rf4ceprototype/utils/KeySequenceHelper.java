package com.sony.svpa.rf4ceprototype.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Arrays;
import java.util.List;


/**
 * Class for receiving key sequences, accumulating keys until a timeout or ENTER key occurs,
 * then calling a listener
 */
public class KeySequenceHelper {

  public static final String TAG = KeySequenceHelper.class.getSimpleName();

  private final List<Integer> keyFilter;
  private final List<Integer> completionKeys;
  private final StringBuilder sequence = new StringBuilder();
  private final long timeout;
  private final SuccessListener<String> listener;
  private boolean isChannelUp = false;
  private boolean isChannelDown = false;

  private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
  private final Runnable timeoutRunnable = new Runnable() {
    @Override
    public void run() {
      Log.d(TAG, "Timeout after " + timeout + "ms.");
      sendSequence();
      reset();
    }
  };

  /**
   * Key filter for numbers.
   */
  public static final Integer[] NUMBER_KEYS = {
          KeyEvent.KEYCODE_1,
          KeyEvent.KEYCODE_2,
          KeyEvent.KEYCODE_3,
          KeyEvent.KEYCODE_4,
          KeyEvent.KEYCODE_5,
          KeyEvent.KEYCODE_6,
          KeyEvent.KEYCODE_7,
          KeyEvent.KEYCODE_8,
          KeyEvent.KEYCODE_9,
          KeyEvent.KEYCODE_0,
          KeyEvent.KEYCODE_CHANNEL_DOWN,
          KeyEvent.KEYCODE_CHANNEL_UP

  };

  public static final Integer[] ENTER_KEYS = {
      KeyEvent.KEYCODE_ENTER,
      KeyEvent.KEYCODE_NUMPAD_ENTER,
      KeyEvent.KEYCODE_DPAD_CENTER
  };

  /**
   * Create a key sequence helper.
   *  @param keyFilter      List of key codes to listen for.
   * @param completionKeys Lost of key codes that signal the end of data input.
   * @param timeout        Timeout to consider the prior keys pressed a "sequence"
   * @param listener       Listener to receive a string from the keys pressed.
   */
  public KeySequenceHelper(@NonNull Integer[] keyFilter,
                           @NonNull Integer[] completionKeys,
                           long timeout,
                           @NonNull SuccessListener<String> listener) {
    this.keyFilter = Arrays.asList(keyFilter);
    this.completionKeys = Arrays.asList(completionKeys);
    this.timeout = timeout;
    this.listener = listener;
  }

  /**
   * Receive onKeyDown events and save key sequences.
   *
   * @param keyCode Current key code.
   * @param event   current key event.
   * @return True if the helper consumed this key.
   */
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      if (completionKeys.contains(keyCode)) {
        Log.d(TAG, "Completion key was pressed.");
        if (sequence.length() > 0) {
          sendSequence();
          reset();
          return true;
        } else {
          Log.w(TAG, "Nothing to send.");
        }
      } else if (keyFilter.contains(keyCode)) {

        if (keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
          isChannelDown = true;
          // reset/start timer
          timeoutHandler.removeCallbacks(timeoutRunnable);
          timeoutHandler.postDelayed(timeoutRunnable, timeout);
          return true;

        } else if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP){
          isChannelUp = true;
          // reset/start timer
          timeoutHandler.removeCallbacks(timeoutRunnable);
          timeoutHandler.postDelayed(timeoutRunnable, timeout);
          return true;

        } else{
          Log.d(TAG, "Adding key: keyCode = " + keyCode + ", label = \""
                  + event.getDisplayLabel() + "\".");
          sequence.append(event.getDisplayLabel());
          // reset/start timer
          timeoutHandler.removeCallbacks(timeoutRunnable);
          timeoutHandler.postDelayed(timeoutRunnable, timeout);
          return true;
        }

      }
    }
    return false;
  }

  private void sendSequence() {
    Log.d(TAG, "Sending key sequence.");
    if (isChannelDown){
      isChannelDown = false;
      listener.onChannelDown();
    } else if (isChannelUp){
      isChannelUp = false;
      listener.onChannelUp();
    } else {
      listener.onSuccess(sequence.toString());
      reset();
    }

  }

  private void reset() {
    Log.d(TAG, "Resetting key sequence.");
    sequence.setLength(0);
    isChannelUp = false;
    isChannelDown = false;
    timeoutHandler.removeCallbacks(timeoutRunnable);
  }

}
