/*
 * Copyright 2014 Sony Corporation
 */
package com.sony.svpa.rf4ceprototype.hotplug.util;

import com.mediatek.twoworlds.tv.common.MtkTvCecCommon;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

/**
 * This class declares all constants those are used commonly
 */
public final class Constants {
    public static final String DEVICE_TYPE = "type";
    public static final String EXTERNAL_INPUT = "EXTERNAL_INPUT";
    public static final String INPUT_ID = "input_id";
    public static final String TYPE_EXTERNAL_INPUT = "type_external_input";

    public static final int KEYCODE_CHUP = 166;
    public static final int KEYCODE_CHDOWN = 167;

    public static final String PREF_KEY_SELECTED_DEVICE =
            "com.sony.dtv.braviasyncmenu.selectedDevice";

    public static final String FOCUS_INDEX_KEY = "FocusIndex";
    /**
     * Intent for inner use
     */
    public static final String INTENT_EXIT_BRAVIA_SYNC_MENU =
            "com.sony.dtv.braviasyncmenu.EXIT_BRAVIA_SYNC_MENU";
    /**
     * Intent for starting BraviaSyncMenu
     */
    public static final String INTENT_START_BRAVIA_SYNC_MENU =
            "com.sony.dtv.braviasyncmenu.START_BRAVIA_SYNC_MENU";
    public static final String INTENT_RECEIVE_KEY =
            "android.intent.action.GLOBAL_BUTTON";

    /**
     * Intent for switch input TV
     */
    public static final String INTENT_SWITCH_INPUT_TV =
            "com.sony.dtv.intent.action.TV";

    /**
     * Audio Output mode
     */
    public static final int AUDIO_MODE_TV_SPEAKER = 1;
    public static final int AUDIO_MODE_AUDIO_SYSTEM = 2;

    /**
     * Input state of TV
     */
    public static final int S_VIEWING_OTHER = 0;
    public static final int S_VIEWING_TV = 1;
    public static final int S_VIEWING_CEC = 2;
    public static final int S_VIEWING_MOBILE = 3;
    public static final int S_VIEWING_UNKNOWN = -1;

    public static final String STR_HDMI_PORT1 = "HDMI 1";
    public static final String STR_HDMI_PORT2 = "HDMI 2";
    public static final String STR_HDMI_PORT3 = "HDMI 3";
    public static final String STR_HDMI_PORT4 = "HDMI 4";

    /**
     * Device control key setting
     */
    public static final int DEVICE_CTRL_KEY_NONE =
            MtkTvConfigType.ACFG_DEV_CTRL_KEYS_NONE;

    /**
     * Logical address of TV
     */
    public static final int LOGADDR_TV = MtkTvCecCommon.CEC_LOG_ADDR_TV;
    public static final int LOGADDR_AUDIO_SYS =
            MtkTvCecCommon.CEC_LOG_ADDR_AUD_SYS;

    /**
     * BraviaSyncMenu Timeout after 5 minute
     */
    public static final long IDLE_TIMEOUT = 60000 * 5;// 1s = 1000

    /**
     * Define key on BraviaSyncMenu
     */
    public static final int KEY_DEVCTRL_HOME = 1;
    public static final int KEY_DEVCTRL_OPTION = 2;
    public static final int KEY_DEVCTRL_CONTENT_LIST = 3;
    public static final int KEY_DEVCTRL_AUDIO = 4;
    public static final int KEY_DEVCTRL_POWEROFF = 5;

    /**
     * Logical address of Devices
     */
    public static final int LOGADD_RECORDER1 = 1;
    public static final int LOGADD_RECORDER2 = 2;
    public static final int LOGADD_RECORDER3 = 9;
    public static final int LOGADD_PLAYER1 = 4;
    public static final int LOGADD_PLAYER2 = 8;
    public static final int LOGADD_PLAYER3 = 11;
    public static final int LOGADD_DEVICE1 = 12;
    public static final int LOGADD_DEVICE2 = 13;
    public static final int LOGADD_DEVICE3 = 14;
    public static final int LOGADD_TUNER1 = 3;
    public static final int LOGADD_TUNER2 = 6;
    public static final int LOGADD_TUNER3 = 7;
    public static final int LOGADD_TUNER4 = 10;
    public static final int LOGADD_AUDIOSYS = 5;
    public static final int LOGADD_MOBILE1 = 16;
    public static final int LOGADD_MOBILE2 = 17;
    public static final int LOGADD_MOBILE3 = 18;
    public static final int LOGADD_MOBILE4 = 19;

    /**
     * Input source's name
     */
    public static final String SOURCENAME_TV = "Tuner";
    public static final String SOURCENAME_MOBILE = "Mobile";

    /**
     * Device type
     */
    public static final int DEVICETYPE_MOBILE = 13;
    public static final int DEVICETYPE_RECORDER = 1;
    public static final int DEVICETYPE_PLAYER = 4;
    public static final int DEVICETYPE_AUDIOSYS = 5;
    public static final int DEVICETYPE_TUNER = 3;
    public static final int DEVICETYPE_TV = 0;

    /**
     * TVApp package
     */
    public static final String TVAPP_PACKAGE = "com.sony.dtv.tvx";
    public static final String TVAPP_CLASS = "com.sony.dtv.tvx.tvplayer.main.TvxLauncherActivity";
    public static final String TVAPP_SERVICE_CLASS =
            "com.sony.dtv.tvx.tvplayer.legacy.TvPlayerService";

    /**
     * BraviaSyncService package
     */
    public static final String BRAVIASYNCSERVICE_PACKAGE =
            "com.sony.dtv.braviasyncservice";
    public static final String BRAVIASYNCSERVICE_CLASS =
            "com.sony.dtv.braviasyncservice.BraviaSyncService";

    /**
     * Audio widget package
     */
    public static final String AUDIO_WIDGET_PAKAGE =
            "com.sony.dtv.HomeTheatreControl";
    public static final String AUDIO_WIDGET_CLASS =
            "com.sony.dtv.HomeTheatreControl.HomeTheatreControlActivity";

    public static final String EMPTY_STRING = "";

    public static final int IDX_DEVSELECTION = 0;
    public static final int IDX_DEVCONTROL = 1;
    public static final int IDX_START_RECORD = 2;
    public static final int IDX_STOP_RECORD = 3;
    public static final int IDX_SPEAKER = 4;
    public static final int IDX_OPRAUDIOSYS = 5;
    public static final int IDX_RETURNTV = 7;
    public static final int IDX_REMOTE_TARGET = 8;

    public static final String UNKNOW_STRING = "UNKNOWN";

    public static final int IDX_DEV_CTRL_HOME = 0;
    public static final int IDX_DEV_CTRL_OPTION = 1;
    public static final int IDX_DEV_CTRL_CONTENTLIST = 2;
    public static final int IDX_DEV_CTRL_AUDIO = 3;
    public static final int IDX_DEV_CTRL_POWEROFF = 4;

    // Suppress default constructor for noninstantiability
    private Constants() {
    }
}
