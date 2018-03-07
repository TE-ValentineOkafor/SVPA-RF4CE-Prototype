package com.sony.svpa.rf4ceprototype.models;

import android.database.Cursor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base class for EPG data
 */

public abstract class EpgData {

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.METHOD})
  public @interface Column {

    /**
     * @return the column name in the database
     */
    String value();

  }

  /**
   * Create EPG data object from a database cursor.
   * @param cursor
   */
  public EpgData(Cursor cursor) {

  }

}
