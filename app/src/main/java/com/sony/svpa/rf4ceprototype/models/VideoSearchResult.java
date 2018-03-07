package com.sony.svpa.rf4ceprototype.models;

import java.util.List;

/**
 * Gson class for the results of video searches.
 */

public class VideoSearchResult {

  private String domain;
  private String id;
  private String name;
  private int duration;
  private String summary;
  private String releaseDate2;
  private String contentType;
  private List<Genre> genres;
  private Series series;
  private String supplierId;
  private String supplier;
  private String mediaType;
  private ImageSet images;
  private ImageSet logoImages;

  public String getDomain() {
    return domain;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getDuration() {
    return duration;
  }

  public String getSummary() {
    return summary;
  }

  public String getReleaseDate2() {
    return releaseDate2;
  }

  public String getContentType() {
    return contentType;
  }

  public List<Genre> getGenres() {
    return genres;
  }

  public Series getSeries() {
    return series;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public String getSupplier() {
    return supplier;
  }

  public String getMediaType() {
    return mediaType;
  }

  public ImageSet getImages() {
    return images;
  }

  public ImageSet getLogoImages() {
    return logoImages;
  }
}



