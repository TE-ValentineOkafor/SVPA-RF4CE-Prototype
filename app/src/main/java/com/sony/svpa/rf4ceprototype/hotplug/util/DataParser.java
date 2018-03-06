/*
 * Copyright 2014 Sony Corporation
 */
package com.sony.svpa.rf4ceprototype.hotplug.util;

import android.text.TextUtils;
import android.util.Log;

import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class DataParser {
    private static final String DEVICE_LIST_TAG = "DeviceList";

    private DataParser() {

    }

    /**
     * convert from xml data to list<{@link DeviceInformation}> type
     * @param xmlString
     * @return List<{@link DeviceInformation}>
     */
    public static List<DeviceInformation> parseDeviceList(String xmlString) {

        if (TextUtils.isEmpty(xmlString)) {
            throw new IllegalArgumentException(
                    "xmlString not allowed to be empty.");
        }

        List<DeviceInformation> deviceList = new ArrayList<DeviceInformation>();
        XmlPullParser parser = XMLParser.getParser(xmlString);
        if (null == parser) {
            Log.e("DataParser", "parser is null");
            return null;
        }
        try {
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, XMLParser.NS,
                    DEVICE_LIST_TAG);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (DeviceInformation.DEVICE_INFORMATION.equals(name)) {
                    DeviceInformation device = parseDeviceInfor(parser);
                    if (device != null)
                        deviceList.add(device);
                } else {
                    XMLParser.skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deviceList;
    }

    /*
     * parse one XmlPullParser to DeviceInformation
     */
    private static DeviceInformation parseDeviceInfor(XmlPullParser parser) {
        try {
            parser.require(XmlPullParser.START_TAG, XMLParser.NS,
                    DeviceInformation.DEVICE_INFORMATION);
            DeviceInformation device = new DeviceInformation();
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (DeviceInformation.LOGICAL_ADDRESS.equals(name)) {
                    device.setLogAddr(getIntValue(XMLParser.getValue(parser,
                            DeviceInformation.LOGICAL_ADDRESS)));
                } else if (DeviceInformation.PHYSICAL_ADDRESS.equals(name)) {
                    device.setPhyAddr(XMLParser.getValue(parser,
                            DeviceInformation.PHYSICAL_ADDRESS));
                } else if (DeviceInformation.DEVICE_TYPE.equals(name)) {
                    device.setDevType(getIntValue(XMLParser.getValue(parser,
                            DeviceInformation.DEVICE_TYPE)));
                } else if (DeviceInformation.VENDOR_ID.equals(name)) {
                    device.setVendorId(XMLParser.getValue(parser,
                            DeviceInformation.VENDOR_ID));
                } else if (DeviceInformation.HDMI_PORT_NUM.equals(name)) {
                    device.setHdmiPort(getIntValue(XMLParser.getValue(parser,
                            DeviceInformation.HDMI_PORT_NUM)));
                } else if (DeviceInformation.CONNECTEDTOAMP.equals(name)) {
                    device.setCntToAmp(Boolean.valueOf(XMLParser.getValue(
                            parser, DeviceInformation.CONNECTEDTOAMP)));
                } else if (DeviceInformation.OSD_NAME.equals(name)) {
                    device.setOsdName(XMLParser.getValue(parser,
                            DeviceInformation.OSD_NAME));
                } else {
                    XMLParser.skip(parser);
                }
            }

            return device;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get int value from string
     * @param s string to parser
     * @return int value (-1 if has no value)
     */
    private static int getIntValue(String s) {
        try {
            return Integer.parseInt(s, 16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
