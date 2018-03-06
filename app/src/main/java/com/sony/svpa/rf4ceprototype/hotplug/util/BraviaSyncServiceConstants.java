/*************************************************************************
 * Copyright 2014 Sony Corporation
 * -----------------------------------------------------------------------
 * File name   : BraviaSyncServiceConstants.java
 * Description :
 *************************************************************************/
package com.sony.svpa.rf4ceprototype.hotplug.util;

public class BraviaSyncServiceConstants {
    //Public variables
    //For BraviaSyncServiceHandler
    //Add parameters accordingly

    /**
     * Setting listner in order to receive asychronous messages from BraviaSyncService. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 1 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: None
    **/
	public static final int SET_LISTENER                          =  1;

    /**
     * Stop receiving asynchronous messages from BraviaSyncService. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 2 <br>
     * -Message.replyTo: Registered messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
	 * <br>
     * Reply Message: None
    **/
    public static final int UNSET_LISTENER                        =  2;

    /**
     * Getting device info for specified logical address. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 3 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Logical Address of the target device(in hex) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: <br>
     * -Message.what: 3 <br>
     * -Message.replyTo: Messenger of the BraviaSyncService <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: String of device info in xml form. <br>
     *               (key for putting into bundle : "xml") 
    **/
    public static final int MSG_GET_DEVICE_INFO                   =  3;
	
    /**
     * Getting list of devices connected to TV. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 4 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: <br>
     * -Message.what: 4 <br>
     * -Message.replyTo: Messenger of the BraviaSyncService <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: String of device List in xml form(List of device info). <br>
     *               (key for putting into bundle : "xml") 
    **/	
    public static final int MSG_GET_DEVICE_LIST                   =  4;
	
    /**
     * Getting settings related to braviasync functions. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 5 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: number of item <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: <br>
     * -Message.what: 5 <br>
     * -Message.replyTo: Messenger of the BraviaSyncService <br>
     * -Message.arg1: number of item <br>
     * -Message.arg2: Setting Value <br>
     * -Message.obj: None <br>
     * <br>
     * Item: <br>
     * -Bravia sync control setting: 1 <br>
     * -Device Auto power off setting: 2 <br>
     * -TV auto power on setting: 3 <br>
     * -Auto input change(MHL) setting: 4 <br>
     * -Device control key setting: 5 <br>
     * -Recorder setting: 6 <br>
     * -Temporal remote control setting: 7 <br>
     * -Charge Mhl during power off setting: 8 <br>
     *  (Speaker setting should obtain from AudioManager) <br>
     *   <br>
     * Setting value: <br>
     * -Bravia sync control setting:    Off: 0, On: 1(Other than 0 or 1, will be ignored) <br>
     * -Device Auto power off setting:  Off: 0, On: 1(Other than 0 or 1, will be ignored) <br>
     * -TV auto power on setting:       Off: 0, On: 1(Other than 0 or 1, will be ignored) <br>
     * -Auto input change(MHL) setting: Off: 0, On: 1(Other than 0 or 1, will be ignored) <br>
     * -Device control key setting:     None: 0, Normal: 1, Tuning Keys: 2, Menu Keys: 3, Tuning and Menu Keys: 4(Other than 0, 1, 2, 3, 4, will be ignored) <br>
     * -Recorder setting: Unregistered: 0, Recorder 1: 1, Recorder 2: 2, Recorder 3: 3, Device 1: 4, Device 2: 5, Device 3: 6 <br>
     * -Temporal remote control setting: Device control with TV remote: 0, TV control with TV remote: 1 <br>
     * -Charge Mhl during power off: Stop charging mhl during power off: 0, Charge mhl during power off: 1(Other than the left, will be ignored) <br>
     *  
    **/
    public static final int MSG_GET_BRAVIASYNC_CONFIG             =  5;

    /**
    * Set options related to braviasync functions. <br>
    *  <br>
    * Received Message: <br>
    * -Message.what: 6 <br>
    * -Message.replyTo: Messenger of requester <br>
    * -Message.arg1: number of item <br>
    * -Message.arg2: setting value <br>
    * -Message.obj: None <br>
    *  <br>
    * Reply Message: <br>
    * -Message.what: 6 <br>
    * -Message.replyTo: Messenger of the BraviaSyncService <br>
    * -Message.arg1: number of item <br>
    * -Message.arg2: setting value <br>
    * -Message.obj: None <br>
    *  <br>
    * Item List: <br>
    * See MSG_GET_BRAVIASYNC_CONFIG <br>
    *  <br>
    * Setting value: <br>
    * See MSG_GET_BRAVIASYNC_CONFIG
    **/
    public static final int MSG_SET_BRAVIASYNC_CONFIG             =  6;

    /**
    * Send remote control key. <br>
    *  <br>
    * Received Message: <br>
    * (If you want to specify the source to send) <br>
    * -Message.what: 7 <br>
    * -Message.replyTo: Messenger of requester <br>
    * -Message.arg1: None <br>
    * -Message.arg2: None <br>
    * -Message.obj: String of remoteControlKeyInfo in Xml form <br>
    *             (key for putting into bundle : "xml") <br>
    *  <br>
    * (If just need to send to active source) <br>
    * -Message.what: 7 <br>
    * -Message.replyTo: Messenger of requester <br>
    * -Message.arg1: keyCode <br>
    * -Message.arg2: isPress(0: false, other than 0; true) <br>
    * -Message.obj: <br>
    *  <br>
    * Reply Message:None
    *  <br>
    * NOTICE: <br>
    * Logical address should be in hex.(Example 0x0A is correct not 10) <br>
    * KeyCode should be android key code. <br>
    * IsPress should be either true or false, other string will be ignored <br>
    **/
    public static final int MSG_SEND_REMOTE_CONTROL_KEY           =  7;

    /**
    * Turn on the device and change the input. <br>
    *  <br>
    * Received Message: <br>
    * -Message.what: 8 <br>
    * -Message.replyTo: Messenger of requester <br>
    * -Message.arg1: selected device's logical address <br>
    * -Message.arg2: None <br>
    * -Message.obj: None <br>
    *  <br>
    * Reply Message: <br>
    * -Message.what: 8 <br>
    * -Message.replyTo: Messenger of service <br>
    * -Message.arg1: result <br>
    * -Message.arg2: None <br>
    * -Message.obj: None <br>
    *  <br>
    * Result list: <br>
    * 0: Success <br>
    * 1: Timeout <br>
    * 2: Source not available <br>
    * 3: Target not available <br>
    * 4: Already in progress <br>
    * 5: Exception <br>
    * 6: Incorrect mode <br>
    * 7: Communication failed
    **/
    public static final int MSG_DEVICE_SELECT                     =  8;

    /**
     * Check whether designated device is connected or not. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 9 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: logical address of the device <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: <br>
     * -Message.what: 9 <br>
     * -Message.replyTo: Messenger of service <br>
     * -Message.arg1: if exists 1 else 0(If other than 1 or 0 then treat as 0) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_IS_DEVICE_EXIST                   =  9;

    /**
     * Turn off selected device. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 10 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: logical address of the device to turn off <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_TURN_OFF_DEVICE                   = 10;

    /**
     * Set timer to record tv program. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 11 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Logical Address of the recording device <br>
     * -Message.arg2: None <br>
     * -Message.obj: String of recording device, source info and time in xml form <br>
     *               (key for putting into bundle : "xml") <br>
     * Reply Message: <br>
     * -Message.what: 11 <br>
     * -Message.replyTo: Messenger of service <br>
     * -Message.arg1: result <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * result: <br>
     * SUCCESS = 0 <br>
     * PROG_NOT_ENOUGH_SPACE = 1 <br>
     * PROG_NO_MEDIA_INFO = 2 <br>
     * PROG_MAY_NOT_BE_ENOUGH_SPACE = 3 <br>
     * NOTPROG_NO_FREE_TIMER = 4 <br>
     * NOTPROG_DATE_OUT_OF_RANGE = 5 <br>
     * NOTPROG_RECORD_SEQUENCE_ERROR = 6 <br>
     * NOTPROG_CA_NOT_SUPPORTED = 7 <br>
     * NOTPROG_INSUFFICIENT_CA_INFO = 8 <br>
     * NOTPROG_PARENTAL_LOCK_ON = 9 <br>
     * NOTPROG_CLOCK_FAILURE = 10 <br>
     * NOTPROG_DUPLICATE = 11 <br>
     * DEVICE_NOT_CONNECTED = 12 <br>
     * TIMEOUT = 13 <br>
     * BUSY = 14 <br>
     * OTHER_ERROR = 15 <br>
     * FEATURE_NOT_AVAILABLE = 16 <br>
     * PEER_ABORTED = 17 <br>
     * MEDIA_PROTECTED = 18 <br>
     * OVERLAPPED = 19 <br>
    **/
    public static final int MSG_START_TIMER_RECORDING             = 11;

    /**
     * Miteroku. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 12 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: No need to send xml. (OBSOLETED)String of record source info in xml form <br>
     *              (key for putting into bundle : "xml") <br>
     * Reply Message: <br>
     * -Message.what: 12 <br>
     * -Message.replyTo: Messenger of service <br>
     * -Message.arg1: result <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_START_RECORDING                   = 12;

    /**
     * Stop miteroku. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 13 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: None
    **/
    public static final int MSG_STOP_RECORDING                    = 13;

    /**
     * Passthrough home key to show target device's home menu.
     * <br>
     * Received Message: <br>
     * -Message.what: 14 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Logical Address of the target device <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_SHOW_HOME_MENU                    = 14;

    /**
     * Passthrough option key to show target device's option menu. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 15 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Logical Address of the target device <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: None
    **/
    public static final int MSG_SHOW_OPTION_MENU                  = 15;

    /**
     * Passthrough content menu key to show target device's content list. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 16 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Logical Address of the target device <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_SHOW_CONTENT_LIST                 = 16;

    /**
     * Start/stop system audio mode. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 17 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Audio mode. On -> 1, Off -> 0(other than 0 and 1 will treat as 0) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: <br>
     * -Message.what: 17 <br>
     * -Message.replyTo: Messenger of BraviaSyncService <br>
     * -Message.arg1: Audio mode. On -> 1, Off -> 0(other than 0 and 1 will treat as 0) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_CHANGE_PEER_AUDIO_MODE            = 17;

    /**
     * Reply current active source's logical address. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 18 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
     *  <br>
     * Reply Message: <br>
     * -Message.what: 18 <br>
     * -Message.replyTo: Messenger of service <br>
     * -Message.arg1: Active source's logical address <br>
     * -Message.arg2: None <br>
     * -Message.obj: None 
    **/
    public static final int MSG_GET_ACTIVE_SOURCE                 = 18;

    /**
     * Turn on the bravia sync control setting of connected devices <br>
     * (For setting the bravia sync control setting of the TV itself, use MSG_SET_BRAVIASYNC_CONFIG). <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 19 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_ACTIVATE_HDMI_CONTROL             = 19;

    /**
     * Notification from the service to app, when device is added/removed/info changed. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 20 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Logical address of the device that is added/removed/info changed. <br>
     * (If logical address is 0xF, more than  2 device infomation has changed) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
     **/
    public static final int MSG_NOTIFY_DEVICE_INFO_CHANGED        = 20;

    /**
     * Notification from the service to app, when active source has changed. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 21 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: active source's logical address <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_NOTIFY_ACTIVE_SOURCE_CHANGED      = 21;

    /**
     * Send Cec vendor command with ID. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 22 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: target device logical address <br>
     * -Message.arg2: data size <br>
     * -Message.obj: byte array of vendor command data. <br>
     *              (key for putting into bundle : "command") <br>
     * <br>
     * Reply Message: None <br>
    **/
    public static final int MSG_SEND_VENDOR_COMMAND               = 22;

    /**
     * Send Cec vendor command with ID. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 23 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: byte array of vendor command data <br>
     *              (key for putting into bundle : "command") <br>
     * Reply Message: None
    **/
    public static final int MSG_RECEIVE_VENDOR_COMMAND            = 23;

    /**
     * Send Cec vendor command with ID. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 24 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: byte array of vendor command data <br>
     *              (key for putting into bundle : "command") <br>
     * Reply Message: None
    **/
    public static final int MSG_NOTIFY_CONFIG_CHANGED             = 24;

    /**
     * Get current system audio mode status. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 25 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: <br>
     * Received Message: <br>
     * -Message.what: 25 <br>
     * -Message.replyTo: Messenger of responder <br>
     * -Message.arg1: Audio mode. On -> 1, Off -> 0(other than 0 and 1, treat as 0) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_GET_PEER_AUDIO_MODE               = 25;

    /**
     * Check whether cec/mhl device is connected to the port. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 26 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Port number <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: <br>
     * Received Message: <br>
     * -Message.what: 26 <br>
     * -Message.replyTo: Messenger of responder <br>
     * -Message.arg1: 1: connected, 0: not connected <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_IS_ANY_DEVICE_CONNECTED           = 26;

    /**
     * Check whether recoding device is connected to one of the TV's HDMI input or not. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 27 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: <br>
     * -Message.what: 27 <br>
     * -Message.replyTo: Messenger of responder <br>
     * -Message.arg1: 1: connected, 0: not connected <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_IS_RECORDING_DEVICE_CONNECTED     = 27;

    /**
     * Check whether HDMI(including non-cec) device is connected to the port. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 28 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: <br>
     * -Message.what: 28 <br>
     * -Message.replyTo: Messenger of responder <br>
     * -Message.arg1: int value in hex. See below example. <br>
     * -Message.arg2: None <br>
     * -Message.obj: None
    **/
    public static final int MSG_GET_HOTPLUG_EVENT_INFO            = 28;

    /**
     * Notification when HDMI(including non-cec) device is connected to the port. <br>
     *  <br>
     * Reply Message: <br>
     * -Message.what: 29 <br>
     * -Message.replyTo: Messenger of responder <br>
     * -Message.arg1: portNumber <br>
     * -Message.arg2: 0: disconnected, 1: connected <br>
     * -Message.obj: None  <br>
     * <br>
     * Example: <br>
     * 0x0001 means there is a device on HDMI1 port <br>
     * 0x0011 means there is a device on both HDMI1 and HDMI2 port <br>
     * 0x0111 means there is a device on HDMI1, HDMI2 and HDMI3 port <br>
     * 0x1111 means there is a device on all HDMI ports <br>
     * 0x1010 means there is a device on HDMI4 and HDMI2 port 
    **/
    public static final int MSG_NOTIFY_HOTPLUG_EVENT              = 29;

    /**
     * [Vendor command] Give genre infromation request from connected devices. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 50 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_NOTIFY_REQUEST_GENRE_INFORMATION  = 50;

    /**
     * [Vendor command] Report genre infromation. <br>
     * <br>
     * Received Message: <br>
     * -Message.what: 51 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Genre identification(See below) <br>
     * -Message.arg2: Genre information(Content nibble and User nibble) <br>
     * -Message.obj: <br>
     *  <br>
     * Reply Message: None <br>
     *  <br>
     * Genre identification number: <br>
     * ARIB:0x00 <br>
     * DVB:0x01 <br>
     * ATSC:0x02 <br>
     * Analog tuner:0x10 <br>
     * CD:0x20 <br>
     * DVD:0x21 <br>
     * BD:0x22 <br>
     * External input:0x30 <br>
     * Invalid:0x3F <br>
     *  <br>
     * Genre information: <br>
     * (ARIB) <br>
     * Please fill out in the following order <br>
     * [Content nibble level1][Content nibble level 2][User nibble upper][User nibble lower] <br>
     * <br>
     * (Invalid) <br>
     * No Data: 0x0000 <br>
     * Can not get info: 0x0001 <br>
     * No signal: 0x0002 <br>
     * Under collecting info: 0x0003 
    **/
    public static final int MSG_SEND_GENRE_INFORMATION            = 51;

    /**
     * [Vendor command] Scene information request from connected devices to TV. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 52 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     * <br>
     * Reply Message: None 
    **/
    public static final int MSG_NOTIFY_REQUEST_SCENE_INFORMATION  = 52;

    /**
     * [Vendor command] Report scene information to connected devices. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 53 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: None <br>
     * -Message.arg2: None <br>
     * -Message.obj: String of scene information data in xml form <br>
     *              (key for putting into bundle : "xml") <br>
     * Reply Message: None
    **/
    public static final int MSG_SEND_SCENE_INFORMATION            = 53;

    /**
     * [Vendor command] Genre information received from other devices. <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 54 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Genre information(Content nibble and User nibble) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_NOTIFY_GENRE_INFORMATION_RECEIVED = 54;

    /**
     * [Vendor command] Set whether current TV picture status is single screen(Number of picture = 1) or PIP & PAP(Number of picture = 2). <br>
     *  <br>
     * Received Message: <br>
     * -Message.what: 55 <br>
     * -Message.replyTo: Messenger of requester <br>
     * -Message.arg1: Number of picture(1 or 2) <br>
     * -Message.arg2: None <br>
     * -Message.obj: None <br>
     *  <br>
     * Reply Message: None
    **/
    public static final int MSG_SEND_MULTI_PICTURE_STATUS         = 55;

	public static final int MSG_MAX                               = 99;

    //Logical Address definition
    public static final int LOGICAL_ADDRESS_TV           = 0x0;
    public static final int LOGICAL_ADDRESS_RECORDER1    = 0x1;
    public static final int LOGICAL_ADDRESS_RECORDER2    = 0x2;
    public static final int LOGICAL_ADDRESS_TUNER1       = 0x3;
    public static final int LOGICAL_ADDRESS_PLAYER1      = 0x4;
    public static final int LOGICAL_ADDRESS_AUDIO_SYSTEM = 0x5;
    public static final int LOGICAL_ADDRESS_TUNER2       = 0x6;
    public static final int LOGICAL_ADDRESS_TUNER3       = 0x7;
    public static final int LOGICAL_ADDRESS_PLAYER2      = 0x8;
    public static final int LOGICAL_ADDRESS_RECORDER3    = 0x9;
    public static final int LOGICAL_ADDRESS_TUNER4       = 0xa;
    public static final int LOGICAL_ADDRESS_PLAYER3      = 0xb;
    public static final int LOGICAL_ADDRESS_RESERVED1    = 0xc;
    public static final int LOGICAL_ADDRESS_RESERVED2    = 0xd;
    public static final int LOGICAL_ADDRESS_SPECIFIC_USE = 0xe;
    public static final int LOGICAL_ADDRESS_UNREGISTERED = 0xf;
    public static final int LOGICAL_ADDRESS_BROADCAST    = 0xf;
    public static final int LOGICAL_ADDRESS_MHL_MOBILE1  = 0x10;
    public static final int LOGICAL_ADDRESS_MHL_MOBILE2  = 0x11;
    public static final int LOGICAL_ADDRESS_MHL_MOBILE3  = 0x12;
    public static final int LOGICAL_ADDRESS_MHL_MOBILE4  = 0x13;
    public static final int LOGICAL_ADDRESS_MAX          = 0x14;

    //Device types
    public static final int DEVICE_TYPE_TV = 0x0;
    public static final int DEVICE_TYPE_RECORDER = 0x1;
    public static final int DEVICE_TYPE_RESERVED = 0x2;
    public static final int DEVICE_TYPE_TUNER = 0x3;
    public static final int DEVICE_TYPE_PLAYER = 0x4;
    public static final int DEVICE_TYPE_AUDIO_SYSTEM = 0x5;
    public static final int DEVICE_TYPE_CEC_SWITCH = 0x6;
    public static final int DEVICE_TYPE_VIDEO_PROCESSOR = 0x7;

    //Bravia Sync Setting items
    public static final int ITEM_BRAVIA_SYNC_CONTROL         = 1;
    public static final int ITEM_DEVICE_AUTO_POWER_OFF       = 2;
    public static final int ITEM_TV_AUTO_POWER_ON            = 3;
    public static final int ITEM_MHL_AUTO_INPUT_CHANGE       = 4;
    public static final int ITEM_DEVICE_CONTROL_KEY          = 5;
    public static final int ITEM_RECORDER_SETTING            = 6;
    public static final int ITEM_TEMPORAL_REMOTE_CONTROL     = 7;
    public static final int ITEM_CHARGE_MHL_DURING_POWER_OFF = 8;
    public static final int ITEM_SOUND_MODE_SYNC             = 9;

    //Bravia Sync Setting values
    public static final int PREFERENCE_DISABLE               = 0;
    public static final int PREFERENCE_ENABLE                = 1;
    //Device control key setting
    public static final int PREFERENCE_KEY_NONE              = 0;
    public static final int PREFERENCE_KEY_NORMAL            = 1;
    public static final int PREFERENCE_KEY_TUNING            = 2;
    //Recorder setting
    public static final int PREFERENCE_RECORDER_UNREGISTERED = 0;
    public static final int PREFERENCE_RECORDER_1            = 1;
    public static final int PREFERENCE_RECORDER_2            = 2;
    public static final int PREFERENCE_RECORDER_3            = 3;
    public static final int PREFERENCE_DEVICE_1              = 4;
    public static final int PREFERENCE_DEVICE_2              = 5;
    public static final int PREFERENCE_DEVICE_3              = 6;

    //Cec Passthrough Keycodes
    public static final int CEC_KEYCODE_SELECT                      = 0x00;
    public static final int CEC_KEYCODE_UP                          = 0x01;
    public static final int CEC_KEYCODE_DOWN                        = 0x02;
    public static final int CEC_KEYCODE_LEFT                        = 0x03;
    public static final int CEC_KEYCODE_RIGHT                       = 0x04;
    public static final int CEC_KEYCODE_RIGHT_UP                    = 0x05;
    public static final int CEC_KEYCODE_RIGHT_DOWN                  = 0x06;
    public static final int CEC_KEYCODE_LEFT_UP                     = 0x07;
    public static final int CEC_KEYCODE_LEFT_DOWN                   = 0x08;
    public static final int CEC_KEYCODE_ROOT_MENU                   = 0x09;
    public static final int CEC_KEYCODE_SETUP_MENU                  = 0x0a;
    public static final int CEC_KEYCODE_CONTENTS_MENU               = 0x0b;
    public static final int CEC_KEYCODE_FAVORITE_MENU               = 0x0c;
    public static final int CEC_KEYCODE_EXIT                        = 0x0d;
    //0x0e - 0x0f is reserved
    public static final int CEC_KEYCODE_MEDIA_TOP_MENU              = 0x10;
    public static final int CEC_KEYCODE_MEDIA_CONTEXT_MENU          = 0x11;
    //0x12 - 0x1c is reserved
    public static final int CEC_KEYCODE_NUMBER_ENTRY_MODE           = 0x1d;
    public static final int CEC_KEYCODE_NUM_11                      = 0x1e;
    public static final int CEC_KEYCODE_NUM_12                      = 0x1f;
    public static final int CEC_KEYCODE_NUM_0                       = 0x20;
    public static final int CEC_KEYCODE_NUM_1                       = 0x21;
    public static final int CEC_KEYCODE_NUM_2                       = 0x22;
    public static final int CEC_KEYCODE_NUM_3                       = 0x23;
    public static final int CEC_KEYCODE_NUM_4                       = 0x24;
    public static final int CEC_KEYCODE_NUM_5                       = 0x25;
    public static final int CEC_KEYCODE_NUM_6                       = 0x26;
    public static final int CEC_KEYCODE_NUM_7                       = 0x27;
    public static final int CEC_KEYCODE_NUM_8                       = 0x28;
    public static final int CEC_KEYCODE_NUM_9                       = 0x29;
    public static final int CEC_KEYCODE_DOT                         = 0x2a;
    public static final int CEC_KEYCODE_ENTER                       = 0x2b;
    public static final int CEC_KEYCODE_CLEAR                       = 0x2c;
    //0x2d - 0x2e is reserved
    public static final int CEC_KEYCODE_NEXT_FAVORITE               = 0x2f;
    public static final int CEC_KEYCODE_CHANNEL_DOWN                = 0x30;
    public static final int CEC_KEYCODE_CHANNEL_UP                  = 0x31;
    public static final int CEC_KEYCODE_PREVIOUS_CHANNEL            = 0x32;
    public static final int CEC_KEYCODE_SOUND_SELECT                = 0x33;
    public static final int CEC_KEYCODE_INPUT_SELECT                = 0x34;
    public static final int CEC_KEYCODE_DISPLAY_INFORMATION         = 0x35;
    public static final int CEC_KEYCODE_HELP                        = 0x36;
    public static final int CEC_KEYCODE_PAGE_UP                     = 0x37;
    public static final int CEC_KEYCODE_PAGE_DOWN                   = 0x38;
    //0x39 - 0x3f is reserved
    public static final int CEC_KEYCODE_POWER                       = 0x40;
    public static final int CEC_KEYCODE_VOLUME_UP                   = 0x41;
    public static final int CEC_KEYCODE_VOLUME_DOWN                 = 0x42;
    public static final int CEC_KEYCODE_MUTE                        = 0x43;
    public static final int CEC_KEYCODE_PLAY                        = 0x44;
    public static final int CEC_KEYCODE_STOP                        = 0x45;
    public static final int CEC_KEYCODE_PAUSE                       = 0x46;
    public static final int CEC_KEYCODE_RECORD                      = 0x47;
    public static final int CEC_KEYCODE_REWIND                      = 0x48;
    public static final int CEC_KEYCODE_FAST_FORWARD                = 0x49;
    public static final int CEC_KEYCODE_EJECT                       = 0x4a;
    public static final int CEC_KEYCODE_FORWARD                     = 0x4b;
    public static final int CEC_KEYCODE_BACKWARD                    = 0x4c;
    public static final int CEC_KEYCODE_STOP_RECORD                 = 0x4d;
    public static final int CEC_KEYCODE_PAUSE_RECORD                = 0x4e;
    //0x4f is reserved
    public static final int CEC_KEYCODE_ANGLE                       = 0x50;
    public static final int CEC_KEYCODE_SUB_PICTURE                 = 0x51;
    public static final int CEC_KEYCODE_VIDEO_ON_DEMAND             = 0x52;
    public static final int CEC_KEYCODE_EPG                         = 0x53;
    public static final int CEC_KEYCODE_TIMER_PROGRAMMING           = 0x54;
    public static final int CEC_KEYCODE_INITIAL_CONFIGURATION       = 0x55;
    public static final int CEC_KEYCODE_SELECT_BROADCAST_TYPE       = 0x56;
    public static final int CEC_KEYCODE_SELECT_SOUND_PRESENTATION   = 0x57;
    //0x58 - 0x5f is reserved
    public static final int CEC_KEYCODE_PLAY_FUNCTION               = 0x60;
    public static final int CEC_KEYCODE_PAUSE_PLAY_FUNCTION         = 0x61;
    public static final int CEC_KEYCODE_RECORD_FUNCTION             = 0x62;
    public static final int CEC_KEYCODE_PAUSE_RECORD_FUNCTION       = 0x63;
    public static final int CEC_KEYCODE_STOP_FUNCTION               = 0x64;
    public static final int CEC_KEYCODE_MUTE_FUNCTION               = 0x65;
    public static final int CEC_KEYCODE_RESTORE_VOLUME_FUNCTION     = 0x66;
    public static final int CEC_KEYCODE_TUNE_FUNCTION               = 0x67;
    public static final int CEC_KEYCODE_SELECT_MEDIA_FUNCTION       = 0x68;
    public static final int CEC_KEYCODE_SELECT_AV_INPUT_FUNCTION    = 0x69;
    public static final int CEC_KEYCODE_SELECT_AUDIO_INPUT_FUNCTION = 0x6a;
    public static final int CEC_KEYCODE_POWER_TOGGLE_FUNC           = 0x6b;
    public static final int CEC_KEYCODE_POWER_OFF_FUNC              = 0x6c;
    public static final int CEC_KEYCODE_POWER_ON_FUNC               = 0x6d;
    //0x6e - 0x70 is reserved
    public static final int CEC_KEYCODE_F1_BLUE                     = 0x71;
    public static final int CEC_KEYCODE_F2_RED                      = 0x72;
    public static final int CEC_KEYCODE_F3_GREEN                    = 0x73;
    public static final int CEC_KEYCODE_F4_YELLOW                   = 0x74;
    public static final int CEC_KEYCODE_F5                          = 0x75;
    public static final int CEC_KEYCODE_DATA                        = 0x76;
    //0x77 - 0xff is reserved
    //End Cec KeyCode

    //Additional keycodes (not defined in android)
    public static final int SONY_KEYCODE_POWER_ON_FUNCTION         = 1000;
    public static final int SONY_KEYCODE_POWER_OFF_FUNCTION        = 1001;

    //Cec output information
    //scene information
    public static final int CEC_SCENE_INFORMATION_OFF              = 0x00;
    public static final int CEC_SCENE_INFORMATION_CINEMA           = 0x10;
    public static final int CEC_SCENE_INFORMATION_SPORTS           = 0x20;
    public static final int CEC_SCENE_INFORMATION_PHOTO            = 0x30;
    public static final int CEC_SCENE_INFORMATION_MUSIC            = 0x40;
    public static final int CEC_SCENE_INFORMATION_GAME             = 0x50;
    public static final int CEC_SCENE_INFORMATION_GRAPHICS         = 0x60;
    public static final int CEC_SCENE_INFORMATION_ANIME            = 0x70;

    //picture information
    public static final int CEC_PICTURE_INFORMATION_DYNAMIC        = 0x00;
    public static final int CEC_PICTURE_INFORMATION_STANDARD       = 0x01;
    public static final int CEC_PICTURE_INFORMATION_CUSTOM         = 0x02;
    public static final int CEC_PICTURE_INFORMATION_CINEMA1        = 0x10;
    public static final int CEC_PICTURE_INFORMATION_CINEMA2        = 0x11;
    public static final int CEC_PICTURE_INFORMATION_SPORTS         = 0x20;
    public static final int CEC_PICTURE_INFORMATION_PHOTO_STANDARD = 0x30;
    public static final int CEC_PICTURE_INFORMATION_PHOTO_DYNAMIC  = 0x31;
    public static final int CEC_PICTURE_INFORMATION_PHOTO_ORIGINAL = 0x32;
    public static final int CEC_PICTURE_INFORMATION_PHOTO_CUSTOM   = 0x33;
    public static final int CEC_PICTURE_INFORMATION_GAME_STANDARD  = 0x50;
    public static final int CEC_PICTURE_INFORMATION_GAME_ORIGINAL  = 0x51;
    public static final int CEC_PICTURE_INFORMATION_GRAPHICS       = 0x60;
    public static final int CEC_PICTURE_INFORMATION_ANIME          = 0x70;

    //one touch recording result
    public static final int RECORDING_SUCCESS                                = 0;
    public static final int RECORDING_ERROR_NOT_ENOUGH_SPACE                 = 1;
    public static final int RECORDING_ERROR_CANNOT_RECORD_OTHER_THAN_DIGITAL = 2;
    public static final int RECORDING_ERROR_CANNOT_RECORD_SELECTED_SOURCE    = 3;
    public static final int RECORDING_ERROR_INCORRECT_CHANNEL_SETTING        = 4;
    public static final int RECORDING_ERROR_BCAS_CARD_ERROR                  = 5;
    public static final int RECORDING_ERROR_PROTECTED_PROGRAM                = 6;
    public static final int RECORDING_ERROR_NO_MEDIA_TO_RECORD               = 7;
    public static final int RECORDING_ERROR_MEDIA_ERROR                      = 8;
    public static final int RECORDING_ERROR_PLAYING                          = 9;
    public static final int RECORDING_ERROR_ALREADY_RECORDING                = 10;
    public static final int RECORDING_ERROR_PARENT_LOCK                      = 11;
    public static final int RECORDING_ERROR_DEVICE_NOT_FOUND                 = 12;

    //Timer recording result
    public static final int TIMER_RECORDING_SUCCESS                                  = 0;
    public static final int TIMER_RECORDING_ERROR_NOT_ENOUGH_SPACE                   = 1;
    public static final int TIMER_RECORDING_ERROR_MIGHT_NOT_ENOUGH_SPACE             = 2;
    public static final int TIMER_RECORDING_ERROR_NO_MEDIA_INFO                      = 3;
    public static final int TIMER_RECORDING_ERROR_INFO_NO_FREE_TIMER                 = 4;
    public static final int TIMER_RECORDING_ERROR_DATE_OUT_OF_RANGE                  = 5;
    public static final int TIMER_RECORDING_ERROR_RECORDING_SEQUENCE_ERROR           = 6;
    public static final int TIMER_RECORDING_ERROR_CA_SYSTEM_NOT_SUPPORTED            = 7;
    public static final int TIMER_RECORDING_ERROR_NO_OR_INSUFFICIENT_CA_ENTITLEMENTS = 8;
    public static final int TIMER_RECORDING_ERROR_PARENTAL_LOCK_ON                   = 9;
    public static final int TIMER_RECORDING_ERROR_CLOCK_FAILURE                      = 10;
    public static final int TIMER_RECORDING_ERROR_DUPLICATE                          = 11;
    public static final int TIMER_RECORDING_ERROR_CHECK_RECORDER_CONNECTION          = 12;
    public static final int TIMER_RECORDING_ERROR_TIMEOUT                            = 13;
    public static final int TIMER_RECORDING_ERROR_BUSY                               = 14;
    public static final int TIMER_RECORDING_ERROR_OTHER                              = 15;
    public static final int TIMER_RECORDING_ERROR_FEATURE_NOT_AVAILABLE              = 16;
    public static final int TIMER_RECORDING_ERROR_PEER_ABORTED                       = 17;
    public static final int TIMER_RECORDING_ERROR_INVALID_EXTERNAL_PLUG_NUMBER       = 18;
    public static final int TIMER_RECORDING_ERROR_INVALID_EXTERNAL_PHYSICAL_ADDRESS  = 19;
    public static final int TIMER_RECORDING_ERROR_DOES_NOT_SUPPORT_RESOLUTION        = 20;

    public static final int PROFILE_FOREGROUND = 0;
    public static final int PROFILE_BACKGROUND = 1;

    //OSD Name
    public static final String TV_OSD_NAME = "TV";

    //Recorder Menu Key
    public static final String INTENT_KEY = "com.sony.dtv.braviasyncservice.INTENT_KEY";
    public static final int INTENT_GLOBAL_BUTTON = 1;

    //Protected variables
    //Package private variables
    //Private variables

    //Constructors
    //Do not instanciate
    private BraviaSyncServiceConstants() {
    };

    //Public methods
    //Protected methods
    //Package private methods
    //Private methods
}
