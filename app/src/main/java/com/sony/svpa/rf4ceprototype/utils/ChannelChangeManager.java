package com.sony.svpa.rf4ceprototype.utils;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.List;


public class ChannelChangeManager {

  private static final String TAG = ChannelChangeManager.class.getSimpleName();

  // delay between multiple key presses
  private static final int MULTI_KEY_DELAY = 500;

  // singleton instance
  private static ChannelChangeManager INSTANCE;

  // dependencies
  private final Instrumentation instrumentation;
  private final ChannelSearchProviderHelper channelSearchProviderHelper;
  private final Handler keyPressHandler;

  /**
   * Standard constructor.
   *
   * @param context App context.
   */
  private ChannelChangeManager(Context context) {
    instrumentation = new Instrumentation();
    channelSearchProviderHelper = ChannelSearchProviderHelper.getInstance(context);
    HandlerThread handlerThread = new HandlerThread("ChannelChangeKeyPresses");
    handlerThread.start();
    keyPressHandler = new Handler(handlerThread.getLooper());
  }


  /**
   * Get the singleton instance.
   *
   * @param context App context.
   * @return The singleton instance.
   */
  public static ChannelChangeManager getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new ChannelChangeManager(context.getApplicationContext());
    }
    return INSTANCE;
  }

  /**
   * Change channel by name or number.
   * <p>
   * Will change by number if all the characters in channel string are present on the remote.
   * Otherwise, the channel name is resolved and used to change the channel.
   *
   * @param channel Channel name or number.
   * @return true if channel change was susccessful
   */
  public boolean changeChannelNameOrNumber(String channel) {
    if (allCharactersOnRemote(channel)) {
      return changeChannelWithNumber(channel);
    } else {
      return changeChannelWithName(channel);
    }
  }

  private boolean allCharactersOnRemote(String channel) {
    for (int x = 0; x < channel.length(); x++) {
      String oneCharacter = channel.substring(x, x + 1);
      RemoteButton button = RemoteButton.fromName(oneCharacter);
      if (button == null) {
        return false;
      }
    }
    return true;
  }





  private boolean changeChannelWithNumber(String channelNumber) {
    List<ChannelSearchProviderItem> channelSearchProviderItemList =
            channelSearchProviderHelper.getChannelSearchResults(channelNumber);
    if (channelSearchProviderItemList != null && channelSearchProviderItemList.size() > 0) {
      //for now, trust the result from Google. First item is usually best match.
      tuneChannel(channelSearchProviderItemList.get(0));
      return true;
    } else {
      //channel not found
      return false;
    }
  }

  private void tuneChannel(ChannelSearchProviderItem item) {
    channelSearchProviderHelper.changeChannel(item);
  }

  private boolean changeChannelWithName(String channelName) {
    String searchText = channelName;
    List<ChannelSearchProviderItem> channelSearchProviderItemList =
            channelSearchProviderHelper.getChannelSearchResults(searchText);
    if (channelSearchProviderItemList != null && channelSearchProviderItemList.size() > 0) {
      //for now, trust the result from Google. First item is usually best match.
      tuneChannel(channelSearchProviderItemList.get(0));
      return true;
    } else {
      //channel not found
      return false;
    }

  }

  /**
   * Increment channel. If top channel is passed, roll over to first channel.
   *
   * @param numberOfChannels Number of channels to increment.
   */
  public void channelUp(int numberOfChannels) {
    pressKey(RemoteButton.CHANNEL_UP, numberOfChannels);
  }

  /**
   * Decrement channel. If top channel is passed, roll over to first channel.
   *
   * @param numberOfChannels Number of channels to decrement.
   */
  public void channelDown(int numberOfChannels) {
    pressKey(RemoteButton.CHANNEL_DOWN, numberOfChannels);
  }

  private void pressKey(final RemoteButton button, int times) {
    Log.d(TAG, "TV press button " + button.getName() + ".");
    // note that sending key events like this from a Service requires a system signature
    keyPressHandler.post(new Runnable() {
      @Override
      public void run() {
        instrumentation.sendKeyDownUpSync(button.getKeyCode());
      }
    });
    if (times > 1) {
      for (int i = 0; i < times - 1; i++) {
        keyPressHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
            instrumentation.sendKeyDownUpSync(button.getKeyCode());
          }
        }, MULTI_KEY_DELAY * i);
      }
    }
  }




}
