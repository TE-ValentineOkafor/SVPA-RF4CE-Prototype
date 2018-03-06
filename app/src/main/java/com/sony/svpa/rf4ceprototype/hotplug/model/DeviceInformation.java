/*
 * Copyright 2014 Sony Corporation
 */
package com.sony.svpa.rf4ceprototype.hotplug.model;

import android.content.Context;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.hotplug.util.Constants;


/**
 * @author HungNL1 this document use for saving device information make using
 *         easier
 */
public class DeviceInformation {

    /*
     * Constant
     */
    public static final String DEVICE_INFORMATION = "deviceInformation";
    public static final String LOGICAL_ADDRESS = "LogicalAddress";
    public static final String PHYSICAL_ADDRESS = "PhysicalAddress";
    public static final String DEVICE_TYPE = "DeviceType";
    public static final String VENDOR_ID = "VendorId";
    public static final String HDMI_PORT_NUM = "HdmiPortNum";
    public static final String CONNECTEDTOAMP = "ConnectedToAmp";
    public static final String OSD_NAME = "OsdName";

    private int mLogAddr = 18;
    private String mPhyAddr;
    private int mDevType;
    private String mVendorId;
    private int mHdmiPort;
    private boolean mIsCntToAmp;
    private String mOsdName;
    private int mDevName;

    public DeviceInformation() {

    }

    @Override
    public String toString() {
        return "[value : " + mLogAddr + ", " + mPhyAddr + ", " + mDevType
                + ", " + mVendorId + ", " + mHdmiPort + ", " + mIsCntToAmp
                + ", " + mOsdName + "]";
    }

    public int getLogAddr() {
        return mLogAddr;
    }

    public void setLogAddr(int logAddr) {
        this.mLogAddr = logAddr;
    }

    public String getPhyAddr() {
        return mPhyAddr;
    }

    public void setPhyAddr(String phyAddr) {
        this.mPhyAddr = phyAddr;
    }

    public int getDevType() {
        return mDevType;
    }

    public void setDevType(int devType) {
        this.mDevType = devType;
    }

    public String getVendorId() {
        return mVendorId;
    }

    public void setVendorId(String vendorId) {
        this.mVendorId = vendorId;
    }

    public int getHdmiPort() {
        return mHdmiPort;
    }

    public void setHdmiPort(int hdmiPort) {
        this.mHdmiPort = hdmiPort;
    }

    public boolean isCntToAmp() {
        return mIsCntToAmp;
    }

    public void setCntToAmp(boolean isCntToAmp) {
        this.mIsCntToAmp = isCntToAmp;
    }

    public String getOsdName() {
        return mOsdName;
    }

    public void setOsdName(String osdName) {
        this.mOsdName = osdName;
    }

    public int getDevName() {
        return mDevName;
    }

    public void setDevName(int devName) {
        this.mDevName = devName;
    }

    public int getVisiblePriority() {
        switch (mLogAddr) {
        case Constants.LOGADD_RECORDER1:
        case Constants.LOGADD_RECORDER2:
        case Constants.LOGADD_RECORDER3:
            return 1;
        case Constants.LOGADD_PLAYER1:
        case Constants.LOGADD_PLAYER2:
        case Constants.LOGADD_PLAYER3:
            return 2;
        case Constants.LOGADD_TUNER1:
        case Constants.LOGADD_TUNER2:
        case Constants.LOGADD_TUNER3:
        case Constants.LOGADD_TUNER4:
            return 4;
        case Constants.LOGADD_AUDIOSYS:
            return 6;
        case Constants.LOGADD_DEVICE1:
        case Constants.LOGADD_DEVICE2:
            return 4;
        case Constants.LOGADD_DEVICE3:
            return 5;
        case Constants.LOGADD_MOBILE1:
        case Constants.LOGADD_MOBILE2:
        case Constants.LOGADD_MOBILE3:
        case Constants.LOGADD_MOBILE4:
            return 3;
        default:
            return 8;
        }
    }

    public boolean equal(DeviceInformation device) {
        if (this.mLogAddr == device.getLogAddr()
                && this.mIsCntToAmp == device.isCntToAmp()
                && this.mDevType == device.getDevType()
                && this.mHdmiPort == device.getHdmiPort()
                && this.getOsdName().equals(device.getOsdName())
                && this.getPhyAddr().equals(device.getPhyAddr())
                && this.mVendorId.equals(device.getVendorId())) {
            return true;
        }
        return false;
    }

    /**
     * get device name from logical address
     * @param manager need {@link Context} for get resource
     * @param logAddr logical address of device
     * @return device name
     */
    public static String getDevName(Context context, int logAddr) {
        switch (logAddr) {
        case Constants.LOGADD_RECORDER1:
            return context.getResources().getString(R.string.type_recorder)
                    + " 1";
        case Constants.LOGADD_RECORDER2:
            return context.getResources().getString(R.string.type_recorder)
                    + " 2";
        case Constants.LOGADD_TUNER1:
            return context.getResources().getString(R.string.type_tuner) + " 1";
        case Constants.LOGADD_PLAYER1:
            return context.getResources().getString(R.string.type_player)
                    + " 1";
        case Constants.LOGADD_AUDIOSYS:
            return context.getResources().getString(R.string.type_audio);
        case Constants.LOGADD_TUNER2:
            return context.getResources().getString(R.string.type_tuner) + " 2";
        case Constants.LOGADD_TUNER3:
            return context.getResources().getString(R.string.type_tuner) + " 3";
        case Constants.LOGADD_PLAYER2:
            return context.getResources().getString(R.string.type_player)
                    + " 2";
        case Constants.LOGADD_RECORDER3:
            return context.getResources().getString(R.string.type_recorder)
                    + " 3";
        case Constants.LOGADD_TUNER4:
            return context.getResources().getString(R.string.type_tuner) + " 4";
        case Constants.LOGADD_PLAYER3:
            return context.getResources().getString(R.string.type_player)
                    + " 3";
        case Constants.LOGADD_DEVICE1:
            return context.getResources().getString(R.string.type_device)
                    + " 1";
        case Constants.LOGADD_DEVICE2:
            return context.getResources().getString(R.string.type_device)
                    + " 2";
        case Constants.LOGADD_DEVICE3:
            return context.getResources().getString(R.string.type_device)
                    + " 3";
        case Constants.LOGADD_MOBILE1:
            return context.getResources().getString(R.string.type_mobile)
                    + " 1";
        case Constants.LOGADD_MOBILE2:
            return context.getResources().getString(R.string.type_mobile)
                    + " 2";
        case Constants.LOGADD_MOBILE3:
            return context.getResources().getString(R.string.type_mobile)
                    + " 3";
        case Constants.LOGADD_MOBILE4:
            return context.getResources().getString(R.string.type_mobile)
                    + " 4";
        }

        return Constants.UNKNOW_STRING;
    }
}
