package com.sony.svpa.rf4ceprototype.models;

import java.util.Locale;

/**
 * Enum of providers for CSX video searches.
 */

public enum VideoProvider {

  NETFLIX("netflix"),
  YOUTUBE("youtube");

  private final String value;

  VideoProvider(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  public static VideoProvider fromString(String s) {
    for (VideoProvider provider : values()) {
      if (provider.value.equals(s.toLowerCase(Locale.getDefault()))) {
        return provider;
      }
    }
    // no match
    return null;
  }
}