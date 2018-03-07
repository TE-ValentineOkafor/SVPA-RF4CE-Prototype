package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;


/**
 * Gson class for EPG channels.
 */

public class Channel extends EpgData {

  private String domain;
  @Column("channel_id")
  private String id;
  @Column("name")
  private String name;
  private String attribution;
  @Column("num")
  private String num;
  @Column("name_short")
  private String nameShort;
  private ImageSet images;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public Channel(Cursor cursor) {
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

  public String getAttribution() {
    return attribution;
  }

  public String getNum() {
    return num;
  }

  public String getNameShort() {
    return nameShort;
  }

  public Image getMediumImage() {
    return images.getMedium();
  }

  public Image getSmallImage() {
    return images.getSmall();
  }


}
