package com.sony.svpa.rf4ceprototype.models;

/**
 * Areas for countries that support EPG providers by area.
 */

public class EpgArea {

  private String domain;
  private String id;
  private String name;
  private int order;
  private boolean national;

  public String getDomain() {
    return domain;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getOrder() {
    return order;
  }

  public boolean isNational() {
    return national;
  }
}
