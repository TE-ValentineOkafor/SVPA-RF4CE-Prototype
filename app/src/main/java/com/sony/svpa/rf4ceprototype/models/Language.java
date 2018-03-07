package com.sony.svpa.rf4ceprototype.models;

/**
 * Enum of CSX languages.
 */

public enum Language {

  ENGLISH("eng");

  private final String value;

  Language(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}