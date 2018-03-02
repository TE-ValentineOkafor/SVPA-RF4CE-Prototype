/*
 * Universal Electronics Inc.
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom adapter for an item of the list of functions in IR test.
 **/
public class FunctionListAdapter extends ArrayAdapter<String> {

	/** The _items. */
	private ArrayList<String> _items;

	/** Click lisutener */
	private View.OnClickListener _btnOnclickListener = null;

	/* Touch listner */
	private OnTouchListener _btnTouchListener = null;

	/**
	 * Instantiates a new function list adapter.
	 * 
	 * @param context
	 *            the context
	 * @param textViewResourceId
	 *            the text view resource id
	 * @param items
	 *            the items
	 */
	public FunctionListAdapter(Context context, int textViewResourceId,
			ArrayList<String> items) {
		super(context, textViewResourceId);
		this._items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if (this._items != null) {
			return this._items.size();
		}
		return 0;
	}

	/**
	 * Sets the function.
	 * 
	 * @param items
	 *            the new function
	 */
	public void setFunction(ArrayList<String> items) {
		clear();
		this._items = items;
		notifyDataSetChanged();
	}

	public void setButtonOnTouchListener( OnTouchListener l)
	{
		this._btnTouchListener = l;
	}

	public void setButtonOnClickListener( View.OnClickListener l)
	{
		this._btnOnclickListener = l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listitem, null);
		}
		String d = this._items.get(position);
		if (d != null) {
			TextView tt = (TextView) v.findViewById(R.id.txtItem);
			if (tt != null)
			{
				tt.setText(d);
			}

			/* // disable using button to send IR
			Button bt = (Button)v.findViewById(R.id.button);
			if (bt != null) {
				bt.setText(d);
				bt.setTag(position);
				bt.setOnTouchListener(this._btnTouchListener);
				bt.setOnClickListener(this._btnOnclickListener);
			}*/
		}
		v.setOnClickListener(this._btnOnclickListener);
		//v.setOnTouchListener(this._btnTouchListener);
		return v;
	}
}