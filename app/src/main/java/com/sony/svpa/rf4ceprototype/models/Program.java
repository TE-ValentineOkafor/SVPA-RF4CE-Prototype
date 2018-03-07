package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;


import java.util.List;

/**
 * Gson class for EPG program.
 */

public class Program extends EpgData {

  private Detail detail;
  private String id;
  private String name;
  private String subtitle;
  private int rank;
  private String summary;
  private String tmsId;
  private boolean pastProgramDetailsAvailable;
  private List<Integer> scores;
  private List<Genre> genres;
  private String mediaType;
  private ImageSet images;

  /**
   * Create EPG data object from a database cursor.
   *
   * @param cursor
   */
  public Program(Cursor cursor) {
    super(cursor);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public int getRank() {
    return rank;
  }

  public String getSummary() {
    return summary;
  }

  public String getTmsId() {
    return tmsId;
  }

  public boolean isPastProgramDetailsAvailable() {
    return pastProgramDetailsAvailable;
  }

  public List<Integer> getScores() {
    return scores;
  }

  public List<Genre> getGenres() {
    return genres;
  }

  public String getMediaType() {
    return mediaType;
  }

  public ImageSet getImages() {
    return images;
  }

  public Detail getDetail() {
    return detail;
  }

  public void setDetail(Detail detail) {
    this.detail = detail;
  }

}
