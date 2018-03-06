package com.sony.svpa.rf4ceprototype.utils;

import android.view.KeyEvent;

import java.util.Locale;

/**
 * Remote control buttons that are recognized by the app.
 */

public enum RemoteButton {

  YELLOW("yellow", KeyEvent.KEYCODE_PROG_YELLOW),
  BLUE("blue", KeyEvent.KEYCODE_PROG_BLUE),
  RED("red", KeyEvent.KEYCODE_PROG_RED),
  GREEN("green", KeyEvent.KEYCODE_PROG_GREEN),
  PAUSE("pause", KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE),
  PLAY("play", KeyEvent.KEYCODE_MEDIA_PLAY),
  STOP("stop", KeyEvent.KEYCODE_MEDIA_STOP),
  FAST_FORWARD("fast forward", KeyEvent.KEYCODE_MEDIA_FAST_FORWARD),
  REWIND("rewind", KeyEvent.KEYCODE_MEDIA_REWIND),
  SKIP("skip forward", KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD),
  SKIP_BACKWARD("skip backward", KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD),
  STEP("step forward", KeyEvent.KEYCODE_MEDIA_STEP_FORWARD),
  STEP_BACKWARD("step backward", KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD),
  NEXT("next", KeyEvent.KEYCODE_MEDIA_NEXT),
  PREVIOUS("previous", KeyEvent.KEYCODE_MEDIA_PREVIOUS),
  RECORD("record", KeyEvent.KEYCODE_MEDIA_RECORD),
  UP("up", KeyEvent.KEYCODE_DPAD_UP),
  DOWN("down", KeyEvent.KEYCODE_DPAD_DOWN),
  LEFT("left", KeyEvent.KEYCODE_DPAD_LEFT),
  RIGHT("right", KeyEvent.KEYCODE_DPAD_RIGHT),
  BACK("back", KeyEvent.KEYCODE_BACK),
  HOME("home", KeyEvent.KEYCODE_HOME),
  ZERO("zero", KeyEvent.KEYCODE_0),
  ONE("one", KeyEvent.KEYCODE_1),
  TWO("two", KeyEvent.KEYCODE_2),
  THREE("three", KeyEvent.KEYCODE_3),
  FOUR("four", KeyEvent.KEYCODE_4),
  FIVE("five", KeyEvent.KEYCODE_5),
  SIX("six", KeyEvent.KEYCODE_6),
  SEVEN("seven", KeyEvent.KEYCODE_7),
  EIGHT("eight", KeyEvent.KEYCODE_8),
  NINE("nine", KeyEvent.KEYCODE_9),
  NUM_ZERO("0", KeyEvent.KEYCODE_0),
  NUM_ONE("1", KeyEvent.KEYCODE_1),
  NUM_TWO("2", KeyEvent.KEYCODE_2),
  NUM_THREE("3", KeyEvent.KEYCODE_3),
  NUM_FOUR("4", KeyEvent.KEYCODE_4),
  NUM_FIVE("5", KeyEvent.KEYCODE_5),
  NUM_SIX("6", KeyEvent.KEYCODE_6),
  NUM_SEVEN("7", KeyEvent.KEYCODE_7),
  NUM_EIGHT("8", KeyEvent.KEYCODE_8),
  NUM_NINE("9", KeyEvent.KEYCODE_9),
  PERIOD("period", KeyEvent.KEYCODE_PERIOD),
  DOT(".", KeyEvent.KEYCODE_PERIOD),
  MINUS("minus", KeyEvent.KEYCODE_MINUS),
  GUIDE("guide", KeyEvent.KEYCODE_GUIDE),
  HELP("help", KeyEvent.KEYCODE_HELP),
  AUDIO("audio", KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK),
  CC("subtitle", KeyEvent.KEYCODE_CAPTIONS),
  ENTER("enter", KeyEvent.KEYCODE_ENTER),
  TV_SATELLITE_BS("satellite bs", KeyEvent.KEYCODE_TV_SATELLITE_BS),
  TV_SATELLITE_CS("satellite cs", KeyEvent.KEYCODE_TV_SATELLITE_CS),
  HDMI1("hdmi 1", KeyEvent.KEYCODE_TV_INPUT_HDMI_1),
  HDMI2("hdmi 2", KeyEvent.KEYCODE_TV_INPUT_HDMI_2),
  HDMI3("hdmi 3", KeyEvent.KEYCODE_TV_INPUT_HDMI_3),
  HDMI4("hdmi 4", KeyEvent.KEYCODE_TV_INPUT_HDMI_4),
  TV("tv", KeyEvent.KEYCODE_TV),
  CHANNEL_UP("channel_up", KeyEvent.KEYCODE_CHANNEL_UP),
  CHANNEL_DOWN("channel_down", KeyEvent.KEYCODE_CHANNEL_DOWN),
  ;

  private final String name;
  private final int keyCode;

  RemoteButton(String name, int keyCode) {
    this.name = name;
    this.keyCode = keyCode;
  }

  public String getName() {
    return name;
  }

  public int getKeyCode() {
    return keyCode;
  }

  public static RemoteButton fromName(String name) {
    for (RemoteButton button : values()) {
      if (button.getName().equals(name.toLowerCase(Locale.getDefault()))) {
        return button;
      }
    }
    // not found
    return null;
  }
}
