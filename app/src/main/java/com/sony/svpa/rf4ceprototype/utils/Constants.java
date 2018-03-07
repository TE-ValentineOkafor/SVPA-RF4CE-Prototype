package com.sony.svpa.rf4ceprototype.utils;

/**
 * Created by valokafor on 1/5/18.
 */

public class Constants {
    public final static String HDMI_DEVICE_NAME = "device_name";

    public static final String DEVICE_TYPE_CABLE_BOX = "Cable Box";
    public static final String DEVICE_TYPE_AUDIO = "Sound Bar";
    public static final String DEVICE_TYPE_BLUE_RAY = "Blu Ray";
    public static final String DEVICE_TYPE_GAME = "PlayStation";

    public static final String CABLE_BOX_TYPE_DIRECTTV = "DirectTV";
    public static final String CABLE_BOX_DISH = "Dish";
    public static final String CABLE_BOX_SPECTRUM = "Spectrum";
    public static final String CABLE_BOX_COX = "Cox Cable";

    public static final String DEVICE_TYPE = "device_type";
    public static final String RF_TYPE = "rf_type";
    public static final String DEVICE_NAME = "device_name";
    public static final String CABLE_BOX_NAME = "cable_box_name";
    public static final String HDMI_LABEL = "hdmi_label";
    public static final String DEVICE_INFORMATION = "device_info";
    public static final String REMOTE_MAC_ADDRESS = "remote_mac_address";


    public static String[] sampleDevicesTypes = new String[]{
            DEVICE_TYPE_CABLE_BOX,
            DEVICE_TYPE_AUDIO,
            DEVICE_TYPE_BLUE_RAY,
            DEVICE_TYPE_GAME
    };

    public static String[] sampleSetupBoxes = new String[]{
            CABLE_BOX_TYPE_DIRECTTV,
            CABLE_BOX_DISH,
            CABLE_BOX_SPECTRUM,
            CABLE_BOX_COX
    };

    public final static int RF_ZRC_1_1 = 1;
    public final static int RF_ZRC_2_0 = 2;
    public final static int RF_MSO = 3;
    public final static int ESP_DISH = 4;

    public final static int HDMI_PORT_1 = 1;
    public final static int HDMI_PORT_2 = 2;
    public final static int HDMI_PORT_3 = 3;
    public final static int HDMI_PORT_4 = 4;



}
