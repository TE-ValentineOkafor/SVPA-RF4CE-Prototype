/* 
 * Universal Electronics Inc. 
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a 
 * retrieval system, or transmitted by any means without prior written 
 * Permission of Universal Electronics Inc. 
 */
package com.sony.svpa.rf4ceprototype;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uei.control.BLEManager;
import com.uei.control.Device;
import com.uei.control.DeviceChangedEvent;
import com.uei.control.DeviceType;
import com.uei.control.IControl;
import com.uei.control.IDevice;
import com.uei.control.IDeviceChangedCallback;
import com.uei.control.IKeyEventCallback;
import com.uei.control.IRFunction;
import com.uei.control.ISetup;
import com.uei.control.Remote;
import com.uei.control.ResultCode;
import com.uei.control.SetupMapResult;
import com.uei.control.SetupMapStatusCode;
import com.uei.quickset.DeviceListener;
import com.uei.quickset.DeviceManager;
import com.uei.quickset.DeviceRef;
import com.uei.quickset.QuickSet;
import com.uei.quickset.QuickSetCompatManager;
import com.uei.quickset.type.data.ControlTypeBase;
import com.uei.quickset.type.data.QDevice;
import com.uei.quickset.type.data.RFControlType;
import com.uei.quickset.type.data.Status;
import com.sony.svpa.rf4ceprototype.QuicksetSampleApplication.OnServiceDisconnectedListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class QuicksetSampleActivity.
 */
public class QuicksetSampleActivity extends Activity implements OnServiceDisconnectedListener{
	/** Tag log messages for debugging. */

	/** The Constant DIALOG_KEY_IMPORT. */
	private static final int DIALOG_KEY_IMPORT = 1;
	private static final int MINUMUM_BRANDMODELTEXT = 2;
	private static final int WAITFORCONNECTION = 20000;
	private static final int MAXRETRIES = 3;
	public static final int REQUEST_STORAGE_PERMISSION = 100;

	private static final String[] MenuOptions = new String[] {
			"Select Menu",
			"Find Remote",
			"Voice Search",
			"OTA Update",
			"Read Remote Device Info",
			"Add EDID data"
	};
	private static QuicksetSampleActivity _singleton = null;

	/** Tag log messages for debugging. */
	public final static String LOGTAG ="MainActivity";

	public static boolean HasPermissionToAccessStorage = false;
	


	/** Store and display device types retrieved from ISetup. */
	private ArrayList<String> _deviceTypes = new ArrayList<String>();
	
	/** The device types adapter. */
	private DeviceTypeListAdapter _deviceTypesAdapter = null;
	
	/** The device type index. */
	private int _deviceTypeIndex = -1;
	
	/** Store and display device functions retrieved from ISetup. */
	private ArrayList<String> _deviceFunctions = new ArrayList<String>();
	
	/** The device functions adapter. */
	private FunctionListAdapter _deviceFunctionsAdapter = null;
	
	/** The device function index. */
	private int _deviceFunctionIndex = -1;
	

	/** The current test codeset functions. */
	private ArrayList<CodesetFunction> _currentCodesetFunctions = null;

	/** Current bonded DirecTV remotes */
	private ArrayList<Remote> _remoteList= new ArrayList<>();

	/** The remotes adapter. */
	private RemoteListAdapter _remotesAdapter = null;

	/** The volume source adapter. */
	private DeviceTypeListAdapter _volumeSourceAdapter = null;

	/** Current devices for volume source. */
	private ArrayList<String> _volSourceDevices = new ArrayList<String>();



	/** The brand index. */
	private int _brandIndex = -1;
	
	/** Store and display configured devices retrieved from ISetup. */
	private ArrayList<IDevice> _devices = new ArrayList<IDevice>();
	
	/** The devices adapter. */
	private DeviceListAdapter _devicesAdapter = null;
	
	/** The device type list spinner */
	private Spinner _lstDeviceTypes = null; 


	/** The remotes list spinner */
	private Spinner _lstRemotes = null;

	/** The volume source device spinner */
	private Spinner _lstVolSource = null;

	/** Use optimized setup maps */
	private CheckBox _chkUseSetupMaps = null;


	/** Enable/Disable Auto Lookup */
	private CheckBox _chkAutoLookup = null;
	
	/** The device index. */
	private int _deviceIndex = -1;

	/** When adding a device, keep a track of codes being tested. */
	private int _codesetCount = -1;
	
	/** The codeset index. */
	private int _codesetIndex = -1;	
	
	/** The visible flag. */
	private boolean _visible = false;

	/** The init flag. */
	private boolean _init = true;

	/** The volume source init flag. */
	private boolean _volumeLockInitialized = false;

	/** Retry */
	private int _retries = 0;

	/** If we're using setup map (otherwise standard codeset testing route). */
	private boolean _isSetupMapRoute = false;

	/** If we're using setup map (otherwise standard codeset testing route) after the result with more than one codes. */
	private boolean _isExtendedSetupMapRoute = false;

	/** How many successful tested functions before setup map will stop. */
	private int _maxMatchTests = 4;
	
	/** How many total tested functions before setup map will stop. */
	private int _maxTotalTests = 12;
	
	/** Current test key number. */
	private int _currentTestKeyNumber = 0;

	/** Progress dialog **/
	private ProgressDialog _waitDialog = null;

	/** The command thread. */
	private IRActionManager _commandManager = null;

	/** The loading dialog. */
	private ProgressDialog _loadingDialog;

	/** The _add device dialog. */
	private Dialog _addDeviceDialog = null;

	private Menu _optionMenu = null;

	/** Key Id for current test key in setup map. */
	private int _currentSetuMapTestKeyId = 0;

	/** Key label for current test key in setup map. */
	private String _currentSetupMapTestKeyLabel = "";

	/** flag to use setup maps */
	private boolean _useSetupMaps = false;

	/** Unique User Id for online access */
	private String _userId = "";

	/** BLE Manager helper */
	private BLEManager _bleManager = null;
	
	private static QuickSet sQuickSet;
	/**
	 * Called when the activity is first created. Initializes the layout,
	 * retrieves the list of configured devices from ISetup service.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
			//set to landscape mode for large screen
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } catch (Exception e) {							
 			e.printStackTrace();
 			Log.e(LOGTAG, e.toString());
 		}
		_singleton = this;
		setContentView(R.layout.main);

		// init BLE Manager
		try {
			// check the system support BLE or not.
			PackageManager packageMan = getPackageManager();
			if (packageMan == null || packageMan != null && !packageMan.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
				Log.e(QuicksetSampleActivity.LOGTAG, "-- BLE feature not supported.");
			} else {
				// get a reference to the bluetooth system service
				BluetoothManager bleMgr = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
				Log.d(QuicksetSampleActivity.LOGTAG, "-- Retrieve BluetoothManager --");
				if (bleMgr != null) {
					Log.d(QuicksetSampleActivity.LOGTAG, "-- Init BLEManager --");
					this._bleManager = new BLEManager(bleMgr);
				} else {
					Log.e(QuicksetSampleActivity.LOGTAG, "-- CANNOT retrieve BluetoothManager --");
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

        this._commandManager = new IRActionManager();
		sQuickSet = QuickSet.getInstance();
        initServices();





		if(Build.VERSION.SDK_INT >= 23) {
			// check for permission
			HasPermissionToAccessStorage = (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
			if(!HasPermissionToAccessStorage) {
				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
			}
		}

		try {
			if(getIntent() != null) {
				Bundle bundle = getIntent().getExtras();
				if (bundle != null) {
					String mode = bundle.getString("mode");
					Log.d(QuicksetSampleActivity.LOGTAG, "Starting mode: " + mode);
					if (mode != null && mode.compareToIgnoreCase(SystemReceiver.INTENT_FIRMWAREUPDATE) == 0) {
						Intent intent = new Intent(this, FirmwareUpdateActivity.class);
						startActivity(intent);
					} else if(mode != null && mode.compareToIgnoreCase(SystemReceiver.INTENT_VOICE) == 0){
						// voice search
						Intent intent = new Intent(this, VoiceSearchActivity.class);
						startActivity(intent);
					}
				}
			}
		} catch(Exception ex) {
		}
	}

	private final DeviceListener mDeviceListener = new DeviceListener() {
		@Override
		public void onDeviceAdded(final String rid, final QDevice device) {
			Log.d(LOGTAG, "QDevice added: " + device.id);
		}

		@Override
		public void onDeviceRemoved(final String rid, final QDevice device) {
			Log.d(LOGTAG, "QDevice removed: " + device.id);
		}

		@Override
		public void onDeviceUpdated(final String rid, final QDevice device) {
			Log.d(LOGTAG, "QDevice updated: " + device.id);
			for (ControlTypeBase type: device.controlTypes) {
				if (type instanceof RFControlType) {
					final RFControlType rfControlType = (RFControlType) type;
					if (!rfControlType.paired) {
						final RFPairingInitializationDialogFragment rfPairingFragment = new RFPairingInitializationDialogFragment();
						final Bundle bundle = new Bundle();
						bundle.putString("deviceId", device.id);
						rfPairingFragment.setArguments(bundle);
						rfPairingFragment.show(getFragmentManager(), "RF Initialization");
						break;
					}
				}
			}
		}
	};

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static QuicksetSampleActivity getSingleton() {
		return _singleton;
	}

	/** Unregister the callback that was used to update a list of devices.
	 * @see IControl
	 */
    @Override
	protected void onDestroy() {
		try {
			if(QuicksetSampleApplication.getSetup() != null) {
				// unregister device changed event listener
				QuicksetSampleApplication.getSetup().unregisterDeviceChangedCallback(_deviceChangedCallback);
			}
			if(QuicksetSampleApplication.getControl() != null) {
				QuicksetSampleApplication.getControl().unregisterKeyEventListener(QuicksetSampleActivity.this._BLEKeyEventCallback);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
		Log.d(QuicksetSampleActivity.LOGTAG, "onDestroy");
		this.closeApplication();
	}
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override 
    protected void onResume()
    {
    	super.onResume();
    	startCommandThread();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
    	stopCommandThread();
    	super.onPause();
    	this._visible = false;
    }
    



   	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case DIALOG_KEY_IMPORT:
				if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					// Get the file path
					File f = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
					if(f.exists()) {
						
						try {
							FileInputStream fs = new FileInputStream(f);
							int count = fs.available();							
							byte[] buffer = new byte[count];
							fs.read(buffer, 0, count);
							fs.close();
															
							// import the selected device configuration
							int result = QuicksetSampleApplication
								.getSetup().importDeviceConfiguration(
												getAuthenticationKey(),
												buffer);
							if(result != ResultCode.SUCCESS) {
								getLastResultCode("importDeviceConfiguration: ");
								handleDeviceConfigurationImported(false);
							} else {
								handleDeviceConfigurationImported(true);
							}
						}catch (Exception e) {							
							e.printStackTrace();
						}						
					}					
				}
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_STORAGE_PERMISSION:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					HasPermissionToAccessStorage = true;
				} else {
					Log.e(QuicksetSampleActivity.LOGTAG, "No permission to access external storage!");
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}






	/** In a separate thread, tries to initialize instances of IControl and ISetup.
	 * When done, retrieves the list of configured devices, and registers a callback on event of this list change.
	 * @see IControl
	 * @see ISetup
	 */
	private void initServices() {
		Thread thread = new Thread() {
			@Override 
			public void run() {

				try {
					long wait = System.currentTimeMillis();




					while ((System.currentTimeMillis() - wait) <= WAITFORCONNECTION) {
						boolean isReady = QuicksetSampleApplication.isQSServicesReady();
						IControl control = QuicksetSampleApplication.getControl();
						ISetup setup = QuicksetSampleApplication.getSetup();

						boolean isControlNull = control != null;
						boolean isSetupNull = setup != null;
						Log.d(LOGTAG, "isReady: " + isReady  + ", control:" + isControlNull + ", setup: " + isSetupNull);
						
						if ( isReady && control != null && setup != null) {
							final DeviceManager devManager = (DeviceManager) sQuickSet.getManager(QuickSet.DEVICE_MGR);
							devManager.register(mDeviceListener);

							QuicksetSampleActivity.this._init = false;
							Log.d(QuicksetSampleActivity.LOGTAG, " Registering Key Event callback....");
							QuicksetSampleApplication.getControl().registerKeyEventListener(QuicksetSampleActivity.this._BLEKeyEventCallback);
							QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										// activate Quickset services
										if (QuicksetSampleApplication.getSetup().activateQuicksetService(getAuthenticationKey()) == true) {
											Toast.makeText(_singleton, "Quickset Activated",Toast.LENGTH_LONG).show();
											Log.d(QuicksetSampleActivity.LOGTAG, "Activated Quickset services.");
										} else {
											getLastResultCode("Failed to activated Quickset services. ");
										}
										int resultCode = QuicksetSampleApplication
												.getControl()
												.getLastResultCode();
										Log.d(QuicksetSampleActivity.LOGTAG,
												"Last result code from IControl = "
														+ resultCode
														+ " - "
														+ ResultCode
														.getString(resultCode));
										// register device changed event listener
										QuicksetSampleApplication.getSetup().registerDeviceChangedCallback(_deviceChangedCallback);
										QuicksetSampleActivity.this.retrieveDevices();
										QuicksetSampleActivity.this.updateRemotesList();
										QuicksetSampleApplication.getSetup().startAutoLookup(QuicksetSampleApplication.getSession(),
												getAuthenticationKey(), true);

									} catch (Exception e) {
										e.printStackTrace();
										Log.e(QuicksetSampleActivity.LOGTAG, "Failed to activate Quickset services.");
									}
								}
							});

							break;
						}
						Thread.sleep(50);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(QuicksetSampleActivity.LOGTAG, e.toString());
				}
			}
		};
		thread.start();
	}

	/**
	 * Checks for valid session. If the session Id is invalid, the app must renew a
	 * new session and restart the setup wizard.
	 * 
	 * @return true, if successful
	 */
	private boolean hasValidSession()
	{
		boolean hasValidSession = false;
		ISetup setup = QuicksetSampleApplication.getSetup();
		long session = QuicksetSampleApplication.getSession();
		
		try	{			
			if(setup != null && QuicksetSampleApplication.isQSServicesReady()){
				int resultCode =  setup.validateSession(session);
				
				if(resultCode != ResultCode.SUCCESS){
					getLastResultCode("Invalid session result: ");
					
					if(resultCode == ResultCode.INVALID_SESSION)
					{
						// renew new session
						QuicksetSampleApplication.renewSession();
						hasValidSession = true;
					}				
					else if(resultCode == ResultCode.QUICKSETSERVICESNOTACTIVATED)
					{
						try {
							// activate Quickset services
							if(QuicksetSampleApplication.getSetup().activateQuicksetService(getAuthenticationKey()) == true)
							{
								Log.d(QuicksetSampleActivity.LOGTAG, "Activated Quickset services.");								
								return true;
							}
							else
							{	
								getLastResultCode("Failed to activated Quickset services. ");
							}
						} catch (RemoteException e) {
							e.printStackTrace();
							Log.e(QuicksetSampleActivity.LOGTAG, e.toString());
						}
						return false;
					}
					else if(resultCode == ResultCode.SERVICES_NOT_READY)
					{
						if (this._retries < MAXRETRIES) {
							// wait for services ready
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							this._retries++;
							return hasValidSession();
						} else {
							this._retries = 0;
							showFatalError();
							return false;
						}
					}
					else
					{
						showFatalError();
						return false;	
					}
				} else	{
					hasValidSession = true;
				}
			}else{
//				handleServicesDisconnected();
				return false;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(QuicksetSampleActivity.LOGTAG, e.toString());
		}
		
		if(hasValidSession == false)
		{			
			showInvalidSession();
		}
		
		return hasValidSession;
	}

	
	/**
	 * Show invalid session.
	 */
	private void showInvalidSession()
	{
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 DialogInterface.OnClickListener listener = new  DialogInterface.OnClickListener()
				 {			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}			 
				};					
				
				AlertDialog.Builder builder1 = new AlertDialog.Builder(QuicksetSampleActivity.this);
				builder1.setTitle("Invalid Session Id")
					.setMessage("Invalid Session! Setup wizard will be restarted!")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setCancelable(false)
					.setPositiveButton("OK", listener);
				AlertDialog alert = builder1.create();
				alert.show();					
			}
		});
	}
	
	/**
	 * Show maximum devices limit has reached
	 */
	private void showReachedMaxDevices()
	{
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 DialogInterface.OnClickListener listener = new  DialogInterface.OnClickListener()
				 {			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}			 
				};
				AlertDialog.Builder builder1 = new AlertDialog.Builder(QuicksetSampleActivity.this);
				builder1.setTitle("Error")
					.setMessage("You have reached the limit of maximum devices.")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setCancelable(false)
					.setPositiveButton("OK", listener);
				AlertDialog alert = builder1.create();
				alert.show();					
			}
		});
	}
	
	/**
	 * Show fatal error
	 */
	private void showFatalError()	
	{
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(QuicksetSampleActivity.this);
    			builder.setIcon(android.R.drawable.ic_dialog_alert)
    			.setTitle("Error!")
    			.setMessage("Application has encountered expected error and will need to close.")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						closeApplication();
					}
				})
    			.create();
    			AlertDialog alert = builder.create();
    			alert.show();
			}
		});
	}

	/**
	 * Gets the last result code.
	 * 
	 * @param text
	 *            the text
	 * log the last result code
	 */
	private void getLastResultCode(String text)
	{
		if(QuicksetSampleApplication.getSetup() != null)
		{
			int resultCode = ResultCode.ERROR;
			try
			{
				resultCode = QuicksetSampleApplication.getSetup().getLastResultCode();
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
			Log.d(QuicksetSampleActivity.LOGTAG, text + " Last result code = " + resultCode + " - " + ResultCode.getString(resultCode));	
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sample.QuicksetSampleApplication.OnServiceDisconnectedListener#serviceDisconnected()
	 */
	@Override
	public void serviceDisconnected() {
//		this.handleServicesDisconnected();
	}

	/**
	 * Close application.
	 */
	private void closeApplication()
	{
		finish();
		QuicksetSampleApplication.exit();
	}

	/** Generate authentication key to access Setup service. **/
    public String getAuthenticationKey()
    {
    	final QuickSetCompatManager compatManager = (QuickSetCompatManager) sQuickSet.getManager(QuickSet.QUICKSET_COMPAT_MGR);
    	return compatManager.getAuthenticationKey();
    }

	/** Attach handlers for bonded remote controls. */
	private void bindRemotesList() {
		this._lstRemotes = (Spinner) findViewById(R.id.lstRemotes);
		this._remotesAdapter = new RemoteListAdapter(this, android.R.layout.simple_spinner_dropdown_item,
				this._remoteList);
		this._lstRemotes.setAdapter(this._remotesAdapter);

		this._lstRemotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
									   View view, int position, long id) {
				if(_remoteList != null && position < _remoteList.size() && position >= 0) {
					Remote remote = _remoteList.get(position);
					if(remote != null) {
						try {
							// select remote for Setup
							if (QuicksetSampleApplication.getSetup() != null) {
								int result = QuicksetSampleApplication.getSetup().selectRemote(
										getAuthenticationKey(), remote.Id);
								Log.d(QuicksetSampleActivity.LOGTAG,
										" Selecting remote Id: " + remote.Id + ", Result = " +
												result);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {

			}
		});
	}

	/**
	 * Device brand selected.
	 */
	private void deviceBrandSelected() {
		if (this._brandIndex >= 0) {
			//showWaitingDialog("Retrieving codes...");
			this._useSetupMaps = _chkUseSetupMaps.isChecked();

			// online search does not support OSM
			if (this._useSetupMaps && this.isSetupMapAvailable()) {
				final int index = brandList.indexOf(_brandIndex);
				if (-1 == index) {
					Log.e(LOGTAG, "Unknown index: " + _brandIndex);
					return;
				}

				Log.d(QuicksetSampleActivity.LOGTAG, "Setup Map is available for "
							+ brandList.get(index));
				this.createSetupMap();
			} else {
				this.retrieveCodesets();
			}
		}
	}

	/**
	 * After testing is done and the code is confirmed by user, it's added to
	 * the list of configured devices.
	 */
	private void addCurrentSetupMapCodeset() {
		try {
			Log.d(QuicksetSampleActivity.LOGTAG,
					"addCurrentSetupMapCodeset started");

			if (hasValidSession()) {
				IDevice device = null;
				String deviceLabel = ""; //this._brands.get(this._brandIndex) + " " + this._deviceTypes.get(this._deviceTypeIndex);
				device = QuicksetSampleApplication
						.getSetup()
						.addCurrentSetupMapDevice(
								QuicksetSampleApplication.getSession(),
								getAuthenticationKey(), deviceLabel);
				getLastResultCode("addCurrentSetupMapCodeset result: ");

				if (device == null) {
					// show error message that max allowed devices have reached.
					try {
						int resultCode = QuicksetSampleApplication.getSetup()
								.getLastResultCode();

						if (resultCode == ResultCode.MAXIMUN_DEVICES_REACHED) {
							this.showReachedMaxDevices();
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
	/**
	 * After testing is done and the code is confirmed by user, it's added to
	 * the list of configured devices.
	 */
    private void addCurrentCodeset() {
		try {
			Log.d(QuicksetSampleActivity.LOGTAG, "addCurrentCodeset: "
					+ this._codesetIndex);
			
			if (hasValidSession()) {

				// Get the device label
				final int index = brandList.indexOf(_brandIndex);
				if (-1 == index) {
					Log.e(LOGTAG, "Unknown index: " + _brandIndex);
					return;
				}
				String deviceLabel = brandList.get(index) + " " + this._deviceTypes.get(this._deviceTypeIndex);

				// Add the device
				final DeviceManager devMgr = (DeviceManager) sQuickSet.getManager(QuickSet.DEVICE_MGR);
				final DeviceRef deviceRef = new DeviceRef.ByCodesetIndexBuilder(_codesetIndex).label(deviceLabel).build();
				final Status status = devMgr.addDevice(deviceRef, null);

				getLastResultCode("addCurrentCodeset result: ");

				if (Status.Code.SUCCESS != status.mCode) {
					Log.d(QuicksetSampleActivity.LOGTAG, "Failed to add device: " + status.mReason);
				} else {
					this.retrieveDevices();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

    /** Stop blasting of the IR. */
    private void testStopIrFunction() {
		try {
			Log.d(QuicksetSampleActivity.LOGTAG, "testStopIrFunction");
			if(hasValidSession())
			{	
				IRCommand command = new IRCommand(-1, 0, IRCommand.IRType.StopIR);
				this._commandManager.addIRCommand(command);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /** Start blasting of the IR. */
    private void testStartIrFunction() {
		try {
			Log.d(QuicksetSampleActivity.LOGTAG, "testStartIrFunction: "
					+ this._deviceFunctionIndex);

			// this is real Key Id
			IRCommand command = new IRCommand(-1, this._currentCodesetFunctions.get(this._deviceFunctionIndex).KeyId,
					IRCommand.IRType.SendIR);
			this._commandManager.addIRCommand(command);
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    /** Remove a device from the list of configured devices. */
    private void deleteDevice() {
		try {
			Log.d(QuicksetSampleActivity.LOGTAG, "deleteDevice: " + this._deviceIndex);
			
			if(hasValidSession())
			{	
				if (this._deviceIndex != -1) {
					Device d = (Device) this._devicesAdapter.getItem(this._deviceIndex);
					int result = QuicksetSampleApplication.getSetup().removeDevice(
							QuicksetSampleApplication.getSession(),
							getAuthenticationKey(),
							d.getId());
					
					getLastResultCode("deleteDevice result: ");
					
					if(result == ResultCode.SUCCESS) {
						this.retrieveDevices();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /** Retrieve the codesets to test for the selected brand. */
	private void retrieveCodesets()
	{
		try
		{
			if (hasValidSession())
			{
				this._isSetupMapRoute = false;
				this._codesetCount = QuicksetSampleApplication.getSetup().getCodesetCountByBrand(
						QuicksetSampleApplication.getSession(),
						getAuthenticationKey(),
						this._brandIndex);

				this._codesetIndex = 0;
				getLastResultCode("retrieveCodesets result: ");
				setCodesetToTest();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Refresh remote list
	 */
	private void updateRemotesList(){
		try
		{
			if(QuicksetSampleApplication.getSetup() != null) {
				Remote[] remotes = QuicksetSampleApplication.getSetup().getAllRemotes(
							getAuthenticationKey());

				if(this._bleManager != null) {
					this._remoteList.clear();
					if (remotes != null) {
						this._remoteList.addAll(Arrays.asList(remotes));
						this._remotesAdapter.notifyDataSetChanged();
					}
				} else {
					Log.e(QuicksetSampleActivity.LOGTAG, "-- NO BluetoothManager --");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SEt current volume source device
	 */
	private void updateVolumeSourceDevice(){
		try
		{
			if(_remoteList != null && _lstRemotes != null && !_remoteList.isEmpty()) {
				int curPosition = _lstRemotes.getSelectedItemPosition();
				if(curPosition >= 0) {
					Remote remote = _remoteList.get(curPosition);
					if (remote != null) {
						int selection = -1;
						for (int i = 0; i < _devices.size(); i++) {
							Device device = (Device) _devices.get(i);
							DeviceType volSource = getDeviceType(device);
							if (volSource.toInteger() == remote.VolumeSource) {
								selection = i;
								break;
							}
						}
						_lstVolSource.setSelection(selection);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /** Checks if setup map is available for brand and current device type or category. */
    private boolean isSetupMapAvailable()
    {
    	boolean available = false;
		try
		{
			if (hasValidSession())
			{
				available = QuicksetSampleApplication.getSetup().isSetupMapAvailable(
						QuicksetSampleApplication.getSession(), 
						getAuthenticationKey(),
						this._brandIndex);
				
				Log.d(QuicksetSampleActivity.LOGTAG, "Setup map is available: " + available);	
				
				getLastResultCode("isSetupMapAvailable result: ");
			}	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return available;
    }
    
    /** Starts the setup map route. */
	private void createSetupMap() {
		try {
			if (hasValidSession()) {
				this._isSetupMapRoute = true;
				this._isExtendedSetupMapRoute = false;
				this._currentTestKeyNumber = 1;

				SetupMapResult keyToTest = QuicksetSampleApplication
						.getSetup()
						.createSetupMap(
						QuicksetSampleApplication.getSession(),
								getAuthenticationKey(),
								this._brandIndex, this._maxMatchTests,
						this._maxTotalTests);


				Log.d(QuicksetSampleActivity.LOGTAG, "Setup map key to test: "
						+ keyToTest);
				
				setSetupMapKeyToTest(keyToTest);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stopWaitDialog();							
		}
    }
   
    /** Report to setup map if tested key worked or not. */
	private void reportTestResult(boolean isWorked) {
		try {
			if (hasValidSession()) {
				SetupMapResult mapState = QuicksetSampleApplication
						.getSetup()
						.reportTestResult(
						QuicksetSampleApplication.getSession(),
						getAuthenticationKey(),
						isWorked);
				
				// show current setup map status
				showSetupMapStatus(mapState); 
				
				switch (mapState.Status) {
					case SetupMapStatusCode.IDLE:
					case SetupMapStatusCode.SEARCHING:
					case SetupMapStatusCode.CANDIDATE:
						moveToNextTest();
						break;
					case SetupMapStatusCode.FAILED:
					case SetupMapStatusCode.FAILED_MAX_TESTS:
					case SetupMapStatusCode.NOT_INITIALIZED:
						setupMapFailed();
						break;
					case SetupMapStatusCode.BEST_MATCH:
					case SetupMapStatusCode.BEST_MATCH_MAX_TESTS:
					case SetupMapStatusCode.PERFECT_MATCH:
						Log.d(QuicksetSampleActivity.LOGTAG,
							"----- OSM candidates count: " + mapState.CurrentCandidatesCount);

					if(mapState.CurrentCandidatesCount > 1) {
						continueTestCodesFromOSM(mapState);
					} else {
						addCurrentSetupMapCodeset();
					}
						break;
					default:
						throw new Exception("Unexpected setup map state: "
							+ mapState);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * Setup map failed.
	 */
	private void setupMapFailed() {
		setSetupMapKeyToTest(null);
    }
    
	/**
	 * Move to next test.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	private void moveToNextTest() throws RemoteException {
		SetupMapResult nextKey = QuicksetSampleApplication.getSetup()
				.moveToNextTest(
				QuicksetSampleApplication.getSession(),
				getAuthenticationKey());
		
		this._currentTestKeyNumber++;
		setSetupMapKeyToTest(nextKey);
    }

    /** Display the button to test the key returned from setup map. */
	private void setSetupMapKeyToTest(SetupMapResult keyData) {
		if (hasValidSession()) {
			try {				
				if (keyData != null) {
					_currentSetuMapTestKeyId = keyData.FunctionId;
					_currentSetupMapTestKeyLabel = keyData.FunctionLabel;

					// display current setup map status
					showSetupMapStatus(keyData); 

					// clear test function button
					this._deviceFunctions.clear();
					this._currentCodesetFunctions = new ArrayList<CodesetFunction>();

					CodesetFunction function = new CodesetFunction(
							this._currentSetupMapTestKeyLabel,
							this._currentSetuMapTestKeyId);
					this._currentCodesetFunctions.add(function);
					this._deviceFunctions
							.add(this._currentSetupMapTestKeyLabel);

					Log.d(QuicksetSampleActivity.LOGTAG,
							"----- Test function: "
									+ this._currentSetupMapTestKeyLabel + "("
									+ this._currentSetuMapTestKeyId + ")");

					this._deviceFunctionsAdapter
							.setFunction(this._deviceFunctions);
					this._deviceFunctionsAdapter.notifyDataSetChanged();
					this._deviceFunctionsAdapter.setButtonOnClickListener(this._testFunctionClickListener);

				} else {

					this._deviceFunctions.clear();
					this._deviceFunctionsAdapter.notifyDataSetChanged();


				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Show setup map status.
	 *
	 * @param status the status
	 */
	private void showSetupMapStatus(SetupMapResult status)
    		{
		if(status != null) {
			
			try {
				Log.d(QuicksetSampleActivity.LOGTAG,
						"Setup map status: " + status.Status + " - "
								+ SetupMapStatusCode.getString(status.Status));
	
				String message = String
						.format("Testing key %d: %s(%d) - Candidates Count=%d",
								this._currentTestKeyNumber,
								this._currentSetupMapTestKeyLabel,
								this._currentSetuMapTestKeyId,
								status.CurrentCandidatesCount);
			
				Log.d(QuicksetSampleActivity.LOGTAG,
						"Setup map status: " + message);			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 
	 * Continue testing the remaining codesets after OSM could not find the perfect match.
	 * 
	 */
	private void continueTestCodesFromOSM(final SetupMapResult mapState) {
		try {
			this._isSetupMapRoute = false;
			this._isExtendedSetupMapRoute = true;
			if (hasValidSession()) {			
				
				Thread thread = new Thread() {
					@Override
					public void run() {
						try {		
    			
							// remaining codesets are the candidates
							_codesetCount = mapState.CurrentCandidatesCount;
							_codesetIndex = 0;
							getLastResultCode("retrieveCodesets result: ");
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										setCodesetFullKeysToTest();
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										stopWaitDialog();							
									}
								}
							});			
					
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				};
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
					}
					
	/** Set codeset to test recommended test keys.*/
	private void setCodesetToTest() {

		if (hasValidSession()) {
			try {				


				if (this._codesetCount > 0) {
					
					Log.e(QuicksetSampleActivity.LOGTAG,
							"----- Get Test function: " + this._codesetIndex);

					IRFunction[] functions = QuicksetSampleApplication
							.getSetup()
							.testCodeset(
									QuicksetSampleApplication.getSession(),
									getAuthenticationKey(),
									this._codesetIndex);
					
					getLastResultCode("setCodesetToTest result: ");
										
					this._deviceFunctions.clear();
					this._currentCodesetFunctions = new ArrayList<CodesetFunction>();
					
					if (functions != null) {
						for (int i = 0; i < functions.length; i++) {
							CodesetFunction function = new CodesetFunction(
									functions[i].Name, functions[i].Id);
							this._currentCodesetFunctions.add(function);
							this._deviceFunctions.add(function.Label);
							Log.d(QuicksetSampleActivity.LOGTAG,
									"----- Test function: " + function.Label);
						}
					}
					this._deviceFunctionsAdapter
							.setFunction(this._deviceFunctions);
					this._deviceFunctionsAdapter.notifyDataSetChanged();

				} else {
					this._deviceFunctions.clear();
					this._deviceFunctionsAdapter.notifyDataSetChanged();
					

    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	/** Set codeset with full keys to test the remaining codesets after OSM. */
	private void setCodesetFullKeysToTest() {
    	
		if (hasValidSession()) {
			try {


				if (this._codesetCount > 0) {
					Log.e(QuicksetSampleActivity.LOGTAG,
							"----- Get Full Test function: " + this._codesetIndex);

					IRFunction[] functions = QuicksetSampleApplication
							.getSetup()
							.testCodeset(
							QuicksetSampleApplication.getSession(), 
							getAuthenticationKey(), 
							this._codesetIndex);
		
					getLastResultCode("setCodesetToTest result: ");
					
					this._deviceFunctions.clear();
					this._currentCodesetFunctions = new ArrayList<CodesetFunction>();
					
					if (functions != null) {
						for (int i = 0; i < functions.length; i++) {
							CodesetFunction function = new CodesetFunction(
									functions[i].Name, functions[i].Id);
							this._currentCodesetFunctions.add(function);
							this._deviceFunctions.add(function.Label);
							Log.d(QuicksetSampleActivity.LOGTAG,
									"----- Test function: " + function.Label);
						}

					}
					this._deviceFunctionsAdapter
							.setFunction(this._deviceFunctions);
					this._deviceFunctionsAdapter.notifyDataSetChanged();

				} else {
					this._deviceFunctions.clear();
					this._deviceFunctionsAdapter.notifyDataSetChanged();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
	/**
	 * Retrieve the list of configured devices from ISetup.
	 * 
     * @see ISetup
     **/
    private void retrieveDevices() {
    		
    	try {
			Log.d(QuicksetSampleActivity.LOGTAG, "retrieveDevices: start");

			// retrieve standard IR devices
			Device[] devices = QuicksetSampleApplication.getSetup()
					.getDevices(getAuthenticationKey());
			Log.d(QuicksetSampleActivity.LOGTAG, "retrieveDevices: "
					+ ((devices == null) ? "null" : devices.length));
			getLastResultCode("retrieve devices: ");

			this._devices.clear();
			this._devices.addAll(Arrays.asList(devices));
			this._volSourceDevices.clear();
			if(this._devices.size() > 0) {
				this._deviceIndex = 0;
				for(Device device : devices) {
					this._volSourceDevices.add(device.Name);
				}
			} else {
				this._deviceIndex = -1;
            }
			this._devicesAdapter.notifyDataSetChanged();
			this._volumeSourceAdapter.notifyDataSetChanged();
			updateRemotesList();
			updateVolumeSourceDevice();
			Log.d(QuicksetSampleActivity.LOGTAG,
					"retrieveDevices: notifyDataSetChanged()");
		} catch (Exception e) {
			e.printStackTrace();
		}		
    }
 
	/**
	 * Retrieve the list of all possible device groups from ISetup.
	 * 
     * @see ISetup
     **/
    private void retrieveDeviceGroups() {
		try {
			if (hasValidSession()) {
				// disable auto lookup
				this._chkAutoLookup.setChecked(false);
				String[] deviceTypes = QuicksetSampleApplication
							.getSetup()
							.getDeviceGroups(
									QuicksetSampleApplication.getSession(),
									getAuthenticationKey());
				Log.d(QuicksetSampleActivity.LOGTAG,
						"Retrieve Device Categories: " + deviceTypes.length);
				getLastResultCode("retrieve device categories: ");

				this._deviceTypes.clear();
				this._deviceTypes.addAll(Arrays.asList(deviceTypes));				
				this._lstDeviceTypes .setSelection(0);
				this._deviceTypesAdapter.notifyDataSetChanged();
				this._deviceIndex = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private List<Integer> brandList = new ArrayList<>();

		


	/**
	 * Handle device configuration was imported  
	 */
	private void handleDeviceConfigurationImported(final boolean success) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					String message = "";
					int icon = android.R.drawable.ic_dialog_info;
					if(success ){
						message = "Device configuration was successfully imported!";						
					}
					else {
					int resultCode = ResultCode.ERROR;
						try {
							resultCode = QuicksetSampleApplication.getSetup()
									.getLastResultCode();
													
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						message = "Device configuration was failed to be imported: " + resultCode;
						icon = android.R.drawable.ic_dialog_alert;
					}
					
					AlertDialog.Builder builder1 = new AlertDialog.Builder(
							QuicksetSampleActivity.this);
					builder1.setTitle("Importing Configuration")
							.setMessage(message)
							.setIcon(icon)
							.setCancelable(false)
							.setPositiveButton("OK", null);
					AlertDialog alert = builder1.create();
					alert.show();
				} catch (Exception e) {
					e.printStackTrace();				
				}
			}		
		});		
	}



	/**
	 * Stop waiting dialog.
	 */
	private void stopWaitDialog() {

		try {
			// activate Quickset services
			if (QuicksetSampleActivity.this._waitDialog != null) {
				QuicksetSampleActivity.this._waitDialog.dismiss();
				QuicksetSampleActivity.this._waitDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOGTAG, " Closing dialog : " + e.toString());
		}
	}
    
	private DeviceType getDeviceType(Device device){
		DeviceType deviceType = DeviceType.TV;
		if(device != null) {
			if(device.DeviceTypeName.compareTo("TV") == 0) {
				deviceType = DeviceType.TV;
			} else if(device.DeviceTypeName.compareTo("Audio") == 0){
				deviceType = DeviceType.Audio;
			}
		}
		return deviceType;
	}

	/**
	 * Start command thread.
	 */
	private void startCommandThread()
	{
		this._commandManager.startManager();
	}

	/**
	 * Stop command thread.
	 */
	private void stopCommandThread()
    {
		this._commandManager.stopManager();
	}

	/**
	 * OnTouchListener to handle when the IR test button is pressed.
	 */
	private OnTouchListener _testFunctionTouchListener = new OnTouchListener() {
    	@Override
		public boolean onTouch(View v, MotionEvent e) {

			switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Integer position = (Integer)v.getTag();
					QuicksetSampleActivity.this._deviceFunctionIndex = position;
					QuicksetSampleActivity.this.testStartIrFunction();
					break;
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					QuicksetSampleActivity.this.testStopIrFunction();
					break;
				case MotionEvent.ACTION_MOVE:
					Log.i(LOGTAG, String.format("----- motion Event: x: %d, y: %d",
							e.getX(), e.getY()));
				default: break;
			}
			return false;
		}
    };

	/**
	 * OnClickListener to handle when the IR test button is pressed.
	 */
	private OnClickListener _testFunctionClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			try {
				Integer position = (Integer) v.getTag();
				QuicksetSampleActivity.this._deviceFunctionIndex = position;
				// start sending IR
				QuicksetSampleActivity.this.testStartIrFunction();
				// then stop immediately
				QuicksetSampleActivity.this.testStopIrFunction();
			} catch(Exception ex) {
				Log.e(LOGTAG, ex.toString());
			}
		}
	};
		
    /** Callback to update the list of configured devices on any change.
     * @see IDeviceChangedCallback
     **/
	private IDeviceChangedCallback _deviceChangedCallback = new IDeviceChangedCallback.Stub() {
		@Override
		public void onDevicesChanged(int event, int[] deviceIds) {
			try {
				final int eventType = event;
				final int[] deviceIdsUpdated = deviceIds;
				if(QuicksetSampleActivity.this._visible)
				{
					QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.d(QuicksetSampleActivity.LOGTAG, "devicesChanged: " + eventType);
							try {
								String text = "";
								if(eventType == DeviceChangedEvent.DeviceDiscovered.toInteger()) {
									text = "New device is discovered";
								} else if(eventType == DeviceChangedEvent.DeviceAdded.toInteger()) {
									text = "New device is added";
								} else if(eventType == DeviceChangedEvent.DeviceRemoved.toInteger()) {
									text = "Device is removed";
								}else if(eventType == DeviceChangedEvent.DeviceUpdated.toInteger()) {
									text = "Device is updated";
								}

								Toast.makeText(QuicksetSampleActivity.this,
											text, Toast.LENGTH_SHORT).show();
								QuicksetSampleActivity.this.retrieveDevices();
							}
							catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}		
		}
	};

	/** Callback to receive key events
	 * @see IKeyEventCallback
	 **/
	private IKeyEventCallback _BLEKeyEventCallback = new IKeyEventCallback.Stub() {
		@Override
		public void onKeyEvent(int remoteId, String address, int keyValue) {
			try {
				Log.e(LOGTAG, String.format("----- BLE Key Event: Remote Id %d, Address: %s, KeyCode = %04X",
						remoteId, address, keyValue));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	/** Custom adapter for an item of the list of configured devices.
	 * @see Device
	 **/
	private class DeviceListAdapter extends ArrayAdapter<IDevice> {
		
		/** The _items. */
		private ArrayList<IDevice> _items;

		/**
		 * Instantiates a new device list adapter.
		 * 
		 * @param context
		 *            the context
		 * @param textViewResourceId
		 *            the text view resource id
		 * @param items
		 *            the items
		 */
		public DeviceListAdapter(Context context, int textViewResourceId, ArrayList<IDevice> items) {
			super(context, textViewResourceId, items);
			this._items = items;
		}
		
		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        
	        try
	        {
		        if (v == null) {
		            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            v = vi.inflate(R.layout.device_item, null);
		        }
		        Device d = (Device)this._items.get(position);
		        if (d != null) {
		        	TextView tt = (TextView) v.findViewById(R.id.txtDeviceName);
		        	if (tt != null) 
		        	{
		        		tt.setText(d.Name + " - " + d.Codeset);
		        	}
		        	
		        	TextView td = (TextView) v.findViewById(R.id.txtDeviceDetails);
		        	if (td != null) td.setText(d.DeviceTypeName + ", " + d.Brand);
		        }
	        }catch (Exception e) {
				e.printStackTrace();
			}
	        return v;
	    }
	}

	/**
	 * Custom adapter for device type list.
	 **/
	private class DeviceTypeListAdapter extends ArrayAdapter<String> {

		/** The _items. */
		private List<String> _items;

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
		public DeviceTypeListAdapter(Context context, int textViewResourceId,
				List<String> items) {
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

		@Override
		public String getItem(int index) {
			String result = "";

			if (index >= 0 && index < this._items.size()) {
				result = this._items.get(index);
			}
			return result;
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
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(android.R.layout.simple_list_item_1, null);
			}
			if(position >= 0 && position < this._items.size()) {
				String d = this._items.get(position);
				if (d != null) {
					TextView view = (TextView)v.findViewById(android.R.id.text1);
					if (view != null) {
						view.setText(d);
						view.setTextSize(10);
					}
				}
			}
			return v;
		}
	}

	/**
	 * The Class CodesetFunction.
	 */
	private class CodesetFunction {
		public String Label;
		public int KeyId;

		/**
		 * Instantiates a new codeset function.
		 * 
		 * @param label
		 *            the label
		 * @param keyId
		 *            the key id
		 */
		public CodesetFunction(String label, int keyId) {
			this.Label = label;
			this.KeyId = keyId;
		}
	}

	/*
	 * @startuml
	 * title Pairing with device
	 *
	 * participant User
	 * participant Client
	 * participant DeviceManager
	 * participant DevicePairingManager
	 * participant DeviceStateListener
	 * participant DevicePairingListener
	 *
	 * create DeviceStateListener
	 * Client -> DeviceStateListener : new
	 * Client -> DeviceManager : register
	 * DeviceManager --> DeviceStateListener
	 *
	 * Client -> DeviceManager : addDevice
	 * Client <<- DeviceStateListener : onDeviceAdded or onDeviceUpdated
	 *
	 * note over Client, DeviceStateListener
	 *
	 * Note: The added/updated device information indicates
	 * whether the device requires setup and is paired
	 *
	 * end note
	 *
	 * User <- Client : Pair wirelessly?
	 *
	 * alt Pair wirelessly == true
	 *
	 * create DevicePairingListener
	 * Client -> DevicePairingListener : new
	 * Client -> DevicePairingManager : register
	 * DevicePairingManager --> DevicePairingListener
	 *
	 * Client -> DevicePairingManager : pair
	 *
	 * alt Pairing Succeeded == true
	 * Client <<- DevicePairingListener : onPaired
	 * User <- Client : Paired successfully
	 * else Pairing Succeeded == false
	 * Client <<- DevicePairingListener : onFailed
	 * User <- Client : Pairing failed
	 * end
	 *
	 * Client -> DevicePairingManager : unregister
	 * DevicePairingManager --> DevicePairingListener
	 * destroy DevicePairingListener
	 *
	 * end
	 * @enduml
	 */
}
