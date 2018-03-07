package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;

/**
 * Gson class for images.
 */
public class Image extends EpgData {

  private String url;
  private String id;
  private int width;
  private int height;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public Image(Cursor cursor) {
    super(cursor);
  }

  public String getUrl() {
    return url;
  }

  public String getId() {
    return id;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
