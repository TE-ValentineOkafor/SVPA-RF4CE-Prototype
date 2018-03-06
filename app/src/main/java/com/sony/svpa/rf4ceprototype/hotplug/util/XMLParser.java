/*
 * Copyright 2014 Sony Corporation
 */
package com.sony.svpa.rf4ceprototype.hotplug.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class XMLParser {
    public static final String NS = null;

    public static XmlPullParser getParser(String xmlString) {
        try {
            InputStream is =
                    new ByteArrayInputStream(xmlString.getBytes("UTF_8"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            return parser;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getValue(XmlPullParser parser, String tag) {
        try {
            parser.require(XmlPullParser.START_TAG, NS, tag);
            String value = readText(parser);
            parser.require(XmlPullParser.END_TAG, NS, tag);
            return value;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = Constants.EMPTY_STRING;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested
    // tags. i.e., if the next tag after a START_TAG isn't a matching END_TAG,
    // it keeps going until it finds the matching END_TAG (as indicated by the
    // value of "depth" being 0).
    public static void skip(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
