package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;

/**
 * Gson class for sets of images.
 */
public class ImageSet extends EpgData {

  private Image thumbnail;
  private Image small;
  private Image medium;
  private Image large;
  private Image xlarge;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public ImageSet(Cursor cursor) {
    super(cursor);
  }

  public Image getThumbnail() {
    return thumbnail;
  }

  public Image getSmall() {
    return small;
  }

  public Image getMedium() {
    return medium;
  }

  public Image getLarge() {
    return large;
  }

  public Image getXlarge() {
    return xlarge;
  }
}
