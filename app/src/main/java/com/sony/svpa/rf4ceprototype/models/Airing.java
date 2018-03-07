package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;

/**
 * Gson class for EPG program ("Airing")
 */

public class Airing extends EpgData {

  private String domain;
  private String id;
  private String channelId;
  private String start;
  private String end;
  private Program program;
  private String channelName;
  private int duration;
  private String mediaType;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public Airing(Cursor cursor) {
    super(cursor);
  }

  public String getDomain() {
    return domain;
  }

  public String getId() {
    return id;
  }

  public String getChannelId() {
    return channelId;
  }

  public String getStart() {
    return start;
  }

  public String getEnd() {
    return end;
  }

  public Program getProgram() {
    return program;
  }

  public String getChannelName() {
    return channelName;
  }

  public int getDuration() {
    return duration;
  }

  public String getMediaType() {
    return mediaType;
  }
}
