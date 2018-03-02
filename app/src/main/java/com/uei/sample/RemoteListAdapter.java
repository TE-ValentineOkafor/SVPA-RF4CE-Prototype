/*
 * Universal Electronics Inc.
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.uei.sample;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uei.control.Device;
import com.uei.control.Remote;

import java.util.ArrayList;

/** Custom adapter for an item of the list of bonded remotes.
 * @see Device
 **/
public class RemoteListAdapter extends ArrayAdapter<Remote> {

    /** The _items. */
    private ArrayList<Remote> _items;

    /**
     * Instantiates a new Remote list adapter.
     *
     * @param context
     *            the context
     * @param textViewResourceId
     *            the text view resource id
     * @param items
     *            the items
     */
    public RemoteListAdapter(Context context, int textViewResourceId, ArrayList<Remote> items) {
        super(context, textViewResourceId, items);
        this._items = items;
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        try {
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            if (position >= 0 && position < this._items.size()) {
                Remote d = this._items.get(position);
                if (d != null) {
                    TextView view = (TextView) v.findViewById(android.R.id.text1);
                    if (view != null) {
                        view.setText(d.Id + " - " + d.Name);
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();;
            Log.e(QuicksetSampleApplication.LOGTAG, ex.toString());
        }
        return v;
    }
}