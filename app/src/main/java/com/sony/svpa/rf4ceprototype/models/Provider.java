package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;

/**
 * Gson class for a TV provider.
 */
public class Provider extends EpgData {

  private String domain;
  private String id;
  private String name;
  private String type;
  private String city;
  private int order;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public Provider(Cursor cursor) {
    super(cursor);
  }

  public String getDomain() {
    return domain;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getCity() {
    return city;
  }

  public int getOrder() {
    return order;
  }
}
