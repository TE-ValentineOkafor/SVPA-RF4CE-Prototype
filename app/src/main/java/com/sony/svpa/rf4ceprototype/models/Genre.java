package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;

/**
 * Gson class for genres.
 */

public class Genre extends EpgData {

  private String id;
  private String name;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public Genre(Cursor cursor) {
    super(cursor);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
