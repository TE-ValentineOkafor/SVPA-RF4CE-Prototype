/* 
 * Universal Electronics Inc. 
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a 
 * retrieval system, or transmitted by any means without prior written 
 * Permission of Universal Electronics Inc. 
 */
package com.uei.sample;

import android.app.Application;
import android.util.Log;

import com.uei.control.IControl;
import com.uei.control.ISetup;
import com.uei.quickset.QuickSet;
import com.uei.quickset.QuickSetCompatManager;
import com.uei.quickset.type.data.Status;

import java.io.IOException;
import java.io.InputStream;


/**
 * The Class QuicksetSampleApplication.
 */
public class QuicksetSampleApplication extends Application {

	private static QuickSet sQuickSet = null;

	/** The _singleton. */
	private static QuicksetSampleApplication _singleton = null;

	/** The _session. */
	private long _session = 0;
	
	/** The Constant LOGTAG. */
	public static final String LOGTAG = "QSSampleApplication";
	
	/** the flag to indicate QS services are ready. */
	private static boolean _QSServicesReady = false;

	/**
	 * The listener interface for receiving onServiceDisconnected events. The
	 * class that is interested in processing a onServiceDisconnected event
	 * implements this interface, and the object created with that class is
	 * registered with a component using the component's
	 * <code>addOnServiceDisconnectedListener<code> method. When
	 * the onServiceDisconnected event occurs, that object's appropriate
	 * method is invoked.
	 *
	 */
	public interface OnServiceDisconnectedListener
	{		
		/**
		 * Service disconnected.
		 */
		void serviceDisconnected();
	}
	
	public static boolean isQSServicesReady()
	{
		return _QSServicesReady;
	}

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static QuicksetSampleApplication getSingleton() {
		return _singleton;
	}
	
	/**
	 * Gets the control.
	 * 
	 * @return the control
	 */
	public static IControl getControl() {
		if (null == sQuickSet) {
			// TODO: Add warning/error
			return null;
		}

		final QuickSetCompatManager legacyManager = (QuickSetCompatManager) sQuickSet.getManager(QuickSet.QUICKSET_COMPAT_MGR);
		if (null == legacyManager) {
			// TODO: Add warning/error
			return null;
		}

		return legacyManager.getControl();
	}
	
	/**
	 * Gets the setup.
	 * 
	 * @return the setup
	 */
	public static ISetup getSetup() {
		if (null == sQuickSet) {
			// TODO: Add warning/error
			return null;
		}

		final QuickSetCompatManager legacyManager = (QuickSetCompatManager) sQuickSet.getManager(QuickSet.QUICKSET_COMPAT_MGR);
		if (null == legacyManager) {
			// TODO: Add warning/error
			return null;
		}

		return legacyManager.getSetup();
	}
	
	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	public static long getSession() {
		if (null == sQuickSet) {
			// TODO: Add warning/error
			return -1;
		}

		final QuickSetCompatManager legacyManager = (QuickSetCompatManager) sQuickSet.getManager(QuickSet.QUICKSET_COMPAT_MGR);
		if (null == legacyManager) {
			// TODO: Add warning/error
			return -1;
		}


		return legacyManager.getSession();
	}
	
	/**
	 * Renew session.
	 * 
	 * @return the long
	 */
	public static long renewSession() {
		if (null == sQuickSet) {
			// TODO: Add warning/error
			return -1;
		}

		final QuickSetCompatManager legacyManager = (QuickSetCompatManager) sQuickSet.getManager(QuickSet.QUICKSET_COMPAT_MGR);
		if (null == legacyManager) {
			// TODO: Add warning/error
			return -1;
		}


		return legacyManager.renewSession();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		_singleton = this;
		Log.d(LOGTAG, "QuicksetSampleApplication onCreate....");
		sQuickSet = QuickSet.getInstance();
		sQuickSet.initialize(getCustomerKey());
		sQuickSet.bind(this, new QuickSet.OnQuickSetStateCallback() {
			@Override
			public void onSetupConnected() {
				Log.d(LOGTAG, "onSetupConnected");
			}

			@Override
			public void onControlConnected() {
				Log.d(LOGTAG, "onControlConnected");
			}

			@Override
			public void onSetupDisconnected() {
				Log.d(LOGTAG, "onSetupDisconnected");
			}

			@Override
			public void onControlDisconnected() {
				Log.d(LOGTAG, "onControlDisconnected");
			}

			@Override
			public void onQuickSetReady() {
				try {

					Log.d(LOGTAG, "QS SDK Services callback");
					_QSServicesReady = true;
				}
				catch (Exception e) {
					e.printStackTrace();
					Log.e(LOGTAG, e.toString());
				}
			}

			@Override
			public void onError(final Status result) {
				Log.d(LOGTAG, "Error init: " + result.mReason);
			}

			@Override
			public void onSessionExpired() {
				Log.d(LOGTAG, "onSessionExpired");
				QuickSet.getInstance().renewSession();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(LOGTAG, "QuicksetSampleApplication onTerminate");
		sQuickSet.unbind(this);
	}
	
	/**
	 * Exit.
	 */
	public static void exit() {
		System.exit(0); 		
	}

	private byte[] getCustomerKey() {
		byte[] buffer;
		int size;

		InputStream stream;
		try {
			stream = this.getAssets().open("publickey");
			size = stream.available();
			buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			return buffer;


		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
