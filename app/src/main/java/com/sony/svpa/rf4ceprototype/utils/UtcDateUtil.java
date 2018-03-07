package com.sony.svpa.rf4ceprototype.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility for performing UTC Date conversions.
 */

public class UtcDateUtil {
  private final static String TAG = "UtcDateUtil";

  /**
   * Convert an ISO formatted UTC(Z) date/time String to a local Date.
   *
   * @param utcDateTime date/time string
   * @return Converted date in local time.
   */
  public static Date getLocalDateTime(String utcDateTime) {
    // convert start and end to Java dates
    try {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
      Date date = format.parse(utcDateTime);
      return new Date(date.getTime() + TimeZone.getDefault().getOffset(date.getTime()));
    } catch (ParseException e) {
      Log.e(TAG, "Error converting date " + utcDateTime + " to local time.");
    }
    return null;
  }

  public static Date convertGraceNoteDateToLocal(String utcDateTime) {
    // convert start and end to Java dates
    try {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault());
      Date date = format.parse(utcDateTime);
      return new Date(date.getTime() + TimeZone.getDefault().getOffset(date.getTime()));
    } catch (ParseException e) {
      Log.e(TAG, "Error converting date " + utcDateTime + " to local time.");
    }
    return null;
  }


  /**
   * Convert a local Date to a UTC date string (optionally compatible with EPG data)
   *
   * @param localDate                Local date to convert.
   * @param roundTo5MinuteIncrements Round the resulting date to CSX-compatible 5 minute increments?
   * @return A date/time string in ISO format for UTC(Z) time zone.
   */
  public static String getUtcDateTime(Date localDate, boolean roundTo5MinuteIncrements) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(localDate);
    if (roundTo5MinuteIncrements) {
      // set seconds to zero
      calendar.set(Calendar.SECOND, 0);
      // round minutes to 5-minute increments
      calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) / 5) * 5);
    }
    return getIsoDate(calendar.getTime());
  }

  /**
   * Return ISO formatted UTC date string for the provided date and time.
   *
   * @param localDate Local date/time.
   * @return ISO string with UTC version of local date/time.
   */
  public static String getIsoDate(Date localDate) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format.format(localDate);
  }

  /**
   * Convert a local date to UTC date.
   *
   * @param localDate Date in local time zone.
   * @return Date in UTC time.
   */
  public static Date getUtcDate(Date localDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(localDate);
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    return calendar.getTime();
  }

  public static String getReadableModifiedDateWithTime(long date){

    String displayDate = new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date(date));
    return displayDate;
  }
}
