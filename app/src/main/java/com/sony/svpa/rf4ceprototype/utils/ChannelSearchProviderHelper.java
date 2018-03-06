package com.sony.svpa.rf4ceprototype.utils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for searching channels using Android TV search provider.
 */
public class ChannelSearchProviderHelper {

  private static ChannelSearchProviderHelper INSTANCE;
  private static final String TAG = ChannelSearchProviderHelper.class.getSimpleName();
  private WeakReference<Context> contextRef;
  private static final int SEARCH_LIMIT = 10;

  /**
   * action type for Search Provider.
   * ACTION_TYPE_SWITCH_CHANNEL is for selecting channel of TV.
   */
  private static final int ACTION_TYPE_SWITCH_CHANNEL = 2;


  private ChannelSearchProviderHelper(Context context) {
    contextRef = new WeakReference<>(context);
  }

  /**
   * Get the singleton instance.
   *
   * @param context App context.
   * @return Singleton instance.
   */
  public static ChannelSearchProviderHelper getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new ChannelSearchProviderHelper(context.getApplicationContext());
    }
    return INSTANCE;
  }

  /**
   * Return a list of channel search results.
   *
   * @param channel Text to search for, could be a channel name or number.
   * @return List of results, ranked in order. Empty list if nothing found.
   */
  public List<ChannelSearchProviderItem> getChannelSearchResults(String channel) {
    List<ChannelSearchProviderItem> channelSearchProviderItems = new ArrayList<>();
    Uri.Builder builder = new Uri.Builder();

    Uri mUri = builder.scheme("content")
        .authority(".function.search")
        .appendPath("search_suggest_query")
        .appendPath(channel)
        .appendQueryParameter("action", String.valueOf(ACTION_TYPE_SWITCH_CHANNEL))
        .appendQueryParameter("limit", String.valueOf(SEARCH_LIMIT))
        .build();

    String[] projection = new String[]{
        SearchManager.SUGGEST_COLUMN_TEXT_1,
        SearchManager.SUGGEST_COLUMN_TEXT_2,
        SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
        SearchManager.SUGGEST_COLUMN_INTENT_DATA,
    };

    Context context = contextRef.get();
    if (context != null) {
      Cursor c = context.getContentResolver().query(mUri, projection, null, null, null);
      if (c != null && c.getCount() > 0) {
        Log.d(TAG, "query : " + mUri.toString() + "..." + c.getCount() + " result found.");
        while (c.moveToNext()) {
          String s1 = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
          String s2 = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));
          String action = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_ACTION));
          String data = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA));
          ChannelSearchProviderItem channelSearchProviderItem =
              new ChannelSearchProviderItem(s1, s2, action, data);
          channelSearchProviderItems.add(channelSearchProviderItem);
        }
        c.close();
        printResultList(channelSearchProviderItems);
      } else {
        Log.d(TAG, "no channel results found");
      }
    }

    return channelSearchProviderItems;
  }

  /**
   * Print a channel list to LogCat.
   *
   * @param channelSearchProviderItems List of channel results.
   */
  private void printResultList(List<ChannelSearchProviderItem> channelSearchProviderItems) {
    Log.d(TAG, "Found " + channelSearchProviderItems.size() + " channel(s).");
    for (ChannelSearchProviderItem item : channelSearchProviderItems) {
      Log.d(TAG, "----------------------------------------");
      Log.d(TAG, item.getSuggestText1());
      if (item.getSuggestText2() != null) {
        Log.d(TAG, item.getSuggestText2());
      }
      Log.d(TAG, item.getSuggestIntentAction());
      Log.d(TAG, item.getSuggestIntentData());
    }
    Log.d(TAG, "----------------------------------------");
  }

  /**
   * Change channel by search item.
   *
   * @param item Item to change to.
   */
  public void changeChannel(ChannelSearchProviderItem item) {
    Intent intent = new Intent();
    intent.setAction(item.getSuggestIntentAction());
    intent.setData(Uri.parse(item.getSuggestIntentData()));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Context context = contextRef.get();
    if (context != null) {
      context.startActivity(intent);
    }
  }
}
