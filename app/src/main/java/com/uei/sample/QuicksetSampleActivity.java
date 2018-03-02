/* 
 * Universal Electronics Inc. 
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a 
 * retrieval system, or transmitted by any means without prior written 
 * Permission of Universal Electronics Inc. 
 */
package com.uei.sample;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
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
import com.uei.encryption.helpers.CallerHelper;
import com.uei.quickset.DeviceListener;
import com.uei.quickset.DeviceManager;
import com.uei.quickset.DeviceRef;
import com.uei.quickset.QuickSet;
import com.uei.quickset.QuickSetCompatManager;
import com.uei.quickset.type.data.ControlTypeBase;
import com.uei.quickset.type.data.QDevice;
import com.uei.quickset.type.data.RFControlType;
import com.uei.quickset.type.data.Status;
import com.uei.sample.QuicksetSampleApplication.OnServiceDisconnectedListener;

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
	
	/** The authenticator. */
	private CallerHelper _authenticator = new CallerHelper();

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
	
	/** Store and display device brands retrieved from ISetup. */
	private ArrayList<String> _brands = new ArrayList<String>();

	/** The brands adapter. */
	private ArrayAdapter<String> _brandsAdapter = null;
	
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

	/** The edit model search. */
	private EditText _editModelSearch = null;

	/** The edit brand search. */
	private EditText _editBrandSearch = null;

	/** The brand index. */
	private int _brandIndex = -1;
	
	/** Store and display configured devices retrieved from ISetup. */
	private ArrayList<IDevice> _devices = new ArrayList<IDevice>();
	
	/** The devices adapter. */
	private DeviceListAdapter _devicesAdapter = null;
	
	/** The device type list spinner */
	private Spinner _lstDeviceTypes = null; 
	
	/** The brand list spinner */
	private Spinner _lstBrands = null;

	/** menu options */
	private Spinner _lstMenus = null;

	/** The remotes list spinner */
	private Spinner _lstRemotes = null;

	/** The volume source device spinner */
	private Spinner _lstVolSource = null;

	/** Use optimized setup maps */
	private CheckBox _chkUseSetupMaps = null;

	/** Retrieve top brands only */
	private CheckBox _chkTopBrands = null;

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
		bindMenuOptions();
		bindRemotesList();
        bindDeviceList();
        bindDeviceTypeList();
        bindDeviceFunctionsList();
        bindBrandsList();
        bindDeviceButtons();
        bindCodesetButtons();
		hideCodesetButtons(true);

		this._chkUseSetupMaps = (CheckBox) findViewById(R.id.ckbUseSetupMaps);
		this._chkTopBrands = (CheckBox)findViewById(R.id.ckbTopBrands);

		// Auto look by EDID/CEC, default is true that SDK starts auto lookup at startup
		this._chkAutoLookup = (CheckBox)findViewById(R.id.ckbAutoLookup);
		this._chkAutoLookup.setChecked(false);
		this._chkAutoLookup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				try {
					if(QuicksetSampleApplication.getSetup() != null) {
						// NOTE: start auto lookup by calling this API
						QuicksetSampleApplication.getSetup().startAutoLookup(QuicksetSampleApplication.getSession(),
								getAuthenticationKey(), isChecked);
					}
					if(isChecked){
						resetDeviceTesting();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

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
    	this._visible = true;
		this._lstMenus.setSelection(0);
    	if(this._init == false){
    		hasValidSession();
			updateRemotesList();
    		if (QuicksetSampleApplication.getControl() != null) {
        		try {
        			this.retrieveDevices();
				}
        		catch (Exception e) {							
					e.printStackTrace();
				}				        
        	}
			this.resetDeviceTesting();
    	}
    	else
    	{
    		this._init = false;
    	}
    	startCommandThread();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
    	try {
	    	if(this._addDeviceDialog != null && this._addDeviceDialog.isShowing())
	    	{
	    		this._addDeviceDialog.dismiss();
	    		this._addDeviceDialog = null;
	    	}
    	}catch (Exception e) {							
			e.printStackTrace();
		}	
    	stopCommandThread();
    	super.onPause();
    	this._visible = false;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();    
    	inflater.inflate(R.menu.menu, menu);  
    	this._optionMenu = menu;
		MenuItem itemMenuFirmwareUpdate = null;
		if(this._optionMenu != null) {
			itemMenuFirmwareUpdate = this._optionMenu.findItem(R.id.menuFirmwareUpdate);
			if(itemMenuFirmwareUpdate != null){
				itemMenuFirmwareUpdate.setEnabled(true);
			}
		}
    	
    	return true;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   
    	switch (item.getItemId()) {
			case R.id.menuFirmwareUpdate:
			{
				// firmware update
				Intent intent = new Intent(this, FirmwareUpdateActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.menuFindRemote:
			{
				// find remote
				Intent intent = new Intent(this, FindRemoteActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.menuVoiceSearch:
			{
				// voice search
				Intent intent = new Intent(this, VoiceSearchActivity.class);
				startActivity(intent);
				break;
			}
			default:
				break;

    	}
    	
    	return true;
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

	/* @Override
	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.i(LOGTAG, String.format("----- motion Event down: x: %f, y: %f",
						e.getX(), e.getY()));
				break;
			case MotionEvent.ACTION_OUTSIDE:
				Log.i(LOGTAG, String.format("----- motion Event outside: x: %f, y: %f",
						e.getX(), e.getY()));
				break;
			case MotionEvent.ACTION_CANCEL:
				Log.i(LOGTAG, String.format("----- motion Event cancel: x: %f, y: %f",
						e.getX(), e.getY()));
				break;
			case MotionEvent.ACTION_UP:
				Log.i(LOGTAG, String.format("----- motion Event up: x: %f, y: %f",
						e.getX(), e.getY()));
				break;
			case MotionEvent.ACTION_MOVE:
				Log.i(LOGTAG, String.format("----- motion Event move: x: %f, y: %f",
						e.getX(), e.getY()));
			case MotionEvent.ACTION_HOVER_MOVE:
				Log.i(LOGTAG, String.format("----- motion Event hover: x: %f, y: %f",
						e.getX(), e.getY()));
				break;

			default: break;
		}
		return true;

	}*/
		/**
         * Handle Key down event and forward the key event to QuickSet SDK.
         * We will use scan code to retrieve the Linux key code that is converted from HID report.
         * @param keyCode
         * @param event
         * @return
         */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.d(QuicksetSampleApplication.LOGTAG, "Key Down event: " + keyCode + " - " + event.getScanCode());

		try {
			// forward key event to QuickSet, if there is any specific actions needed to be executed.
			Object item = this._lstRemotes.getSelectedItem();
			if(item != null && item instanceof Remote) {
				Remote remote = (Remote)item;
				if(QuicksetSampleApplication.getControl() != null) {
					// pass scan code to API
					//HACKHACK: testing OTV for Power ON, since we could not get Power On key event,
					// simulate the event with digit 0 key:
					/*if(event.getScanCode() == 0x0b ) {
						int resultCode = QuicksetSampleApplication.getControl().executeKey(remote.Id,
								0x3E, KeyCodeEvent.KeyDown.toInteger());  //TODO: remove this testing code
						//event.getScanCode(), KeyCodeEvent.KeyDown.toInteger());
						Log.d(QuicksetSampleActivity.LOGTAG,
								" Execute key = "
										+ resultCode
										+ " - "
										+ ResultCode.getString(resultCode));
					}*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.onKeyDown(keyCode, event);
	}


	/**
	 * Handle Key up event and forward the key event to QuickSet SDK.
	 * We will use scan code to retrieve the Linux key code that is converted from HID report.
	 * @param keyCode
	 * @param event
     * @return
     */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(QuicksetSampleApplication.LOGTAG, "Key Up event: " + keyCode + " - " + event.getScanCode());

		try {
			// forward key event to QuickSet, if there is any specific actions needed to be executed.
			/*Object item = this._lstRemotes.getSelectedItem();
			if(item != null && item instanceof Remote) {
				Remote remote = (Remote)item;
				if(QuicksetSampleApplication.getControl() != null) {
					// pass scan code to API
					int resultCode = QuicksetSampleApplication.getControl().executeKey(remote.Id,
							event.getScanCode(), KeyCodeEvent.KeyUp.toInteger());
					Log.d(QuicksetSampleActivity.LOGTAG,
							" Execute key = "
									+ resultCode
									+ " - "
									+ ResultCode.getString(resultCode));
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onKeyUp(keyCode, event);
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
//							QuicksetSampleApplication.registerServiceDisconnectedListener(QuicksetSampleActivity.this);
							Log.d(QuicksetSampleActivity.LOGTAG, " Registering Key Event callback....");
							QuicksetSampleApplication.getControl().registerKeyEventListener(QuicksetSampleActivity.this._BLEKeyEventCallback);
							QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										//QuicksetSampleActivity.this._loadingDialog.dismiss();
										//QuicksetSampleActivity.this._loadingDialog = null;
										enableDisableButtons(true);
										// activate Quickset services
										if (QuicksetSampleApplication.getSetup().activateQuicksetService(getAuthenticationKey()) == true) {
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
	 * Handle services disconnected.
	 */
//	private void handleServicesDisconnected()
//	{
//		if(this._visible)
//		{
//			this.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						resetDeviceTesting();
//						QuicksetSampleApplication.getSingleton().bindServices();
//						AlertDialog.Builder builder1 = new AlertDialog.Builder(QuicksetSampleActivity.this);
//						builder1.setTitle("Quickset Services")
//							.setMessage("Quickset services were closed unexpectedly. Reconnecting services...")
//							.setIcon(android.R.drawable.ic_dialog_alert)
//							.setCancelable(false)
//							.setPositiveButton("OK", null);
//						AlertDialog alert = builder1.create();
//						alert.show();
//						initServices();
//					} catch (Exception e) {
//						e.printStackTrace();
//						Log.e(QuicksetSampleActivity.LOGTAG, e.toString());
//					}
//				}
//			});
//		}
//	}
	
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
						resetDeviceTesting();
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
//		QuicksetSampleApplication.getSingleton().unbindServices();
		finish();
		QuicksetSampleApplication.exit();
	}

	/** Attach codeset-related event handlers:
	 * one for tested code confirmed as working, 
	 * one for switching to the next code.
	 */
    private void bindCodesetButtons() {
    	Button btWorks = (Button) findViewById(R.id.btWorks);
    	btWorks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (QuicksetSampleActivity.this._isSetupMapRoute)
				{
					QuicksetSampleActivity.this.reportTestResult(true);
				}
				else
				{
					QuicksetSampleActivity.this.addCurrentCodeset();			
				}
			}			
    	});
    	
    	Button btNextCode = (Button) findViewById(R.id.btNextCode);
    	btNextCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (QuicksetSampleActivity.this._isSetupMapRoute) {
					QuicksetSampleActivity.this.reportTestResult(false);
				} else {
					if (QuicksetSampleActivity.this._codesetIndex == (QuicksetSampleActivity.this._codesetCount - 1)) {
						QuicksetSampleActivity.this._codesetIndex = 0;
					} else {
						QuicksetSampleActivity.this._codesetIndex++;
					}
					if(QuicksetSampleActivity.this._isExtendedSetupMapRoute) {						
						QuicksetSampleActivity.this.setCodesetFullKeysToTest();
					} else {
						QuicksetSampleActivity.this.setCodesetToTest();
                    }
				}
			}
    	});

    	Button btnBrandSearch = (Button) findViewById(R.id.btBrandSearch);
    	btnBrandSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_editBrandSearch.clearFocus();
				if (QuicksetSampleActivity.this._deviceTypeIndex >= 0) {
					TextView tvCodesets = (TextView)findViewById(R.id.txtCodesets);
					tvCodesets.setText("");
					doBrandSearch(_editBrandSearch.getText().toString());
				} 
			}			
    	});
    	
    	this._editBrandSearch = (EditText)this.findViewById(R.id.txtBrandName);
    	
    	this._editBrandSearch.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button       
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					hideKeyboard();
					if (_editBrandSearch.getText().toString().length() >= MINUMUM_BRANDMODELTEXT) {
						doBrandSearch(_editBrandSearch.getText().toString());
					}
					return true;
				}

				return false;
			}
		});
		Button btModelSearch = (Button)findViewById(R.id.btmodelSearch);
		btModelSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean modelSearch = true;
				/*try {
					int result = QuicksetSampleApplication
								.getSetup()
								.getModelSearchLockStatus();
					Log.d(QuicksetSampleActivity.LOGTAG,
								"Model search status: "
								+ result);
								
					// model search is enabled when the result is 0 (not locked).
					modelSearch = (result == 0);	
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				if(modelSearch) {
					_editModelSearch.clearFocus();
					
					// Perform action on key press
					if(_editModelSearch.getText().toString().length() >= MINUMUM_BRANDMODELTEXT) {
						doModelSearch(_editModelSearch.getText().toString());
					}
				}
			}
		});
		
		this._editModelSearch = (EditText)this.findViewById(R.id.editModelname);
    	
    	this._editModelSearch.setOnKeyListener(new View.OnKeyListener()
		{    
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{        
				// If the event is a key-down event on the "enter" button       
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) 
				{          
					_editModelSearch.clearFocus();
					// Perform action on key press
					if(_editModelSearch.getText().toString().length() >= MINUMUM_BRANDMODELTEXT) {
						doModelSearch(_editModelSearch.getText().toString());
					}
					return true;       
				}       
				
				return false; 
			}
		});
    	
    }

    /** Disable buttons until Services are ready */
    private void enableDisableButtons(boolean enable) {
    	int state = enable ? View.VISIBLE : View.INVISIBLE;
    	
    	try {
			Button btn = (Button) findViewById(R.id.btDelete);
	    	btn.setVisibility(state);
	    	btn = (Button) findViewById(R.id.btAdd);
	     	btn.setVisibility(state);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

  	/**
	 * Perform brand search.
	*/
	private void doBrandSearch(final String brand)
	{
		showWaitingDialog("Searching brands...");
		Thread thread = new Thread() {
			@Override
			public void run() {
		
				try {
					if(brand != null && _deviceTypeIndex >= 0 ) {
						try
						{
							if(brand.length() >= MINUMUM_BRANDMODELTEXT) {
								// search brands with online flag
								final String[] brands = QuicksetSampleApplication.getSetup().searchBrands(
										QuicksetSampleApplication.getSession(),
										getAuthenticationKey(),
										_deviceTypeIndex, brand);
							
								getLastResultCode("search brands result: ");
								
								QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										try {
											stopWaitDialog();											
											_deviceFunctions.clear();
											_deviceFunctionsAdapter.notifyDataSetChanged();
											_deviceFunctionIndex = -1;											
											_codesetCount = 0;
											_codesetIndex = -1;
											displayBrands(brands);											
										} catch (Exception e) {
											e.printStackTrace();
											Log.i(LOGTAG, " Closing dialog : " + e.toString());
										}
									}
								});		
							} else {
								QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										try {
											retrieveBrands();
										} catch (Exception e) {
											e.printStackTrace();
											Log.e(LOGTAG, " Closing dialog : " + e.toString());
										}
									}
								});		
							}								
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						stopWaitDialog();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		};
		thread.start();
	}
	
	/**
	* Hide keyboard.
	*/
	private void hideKeyboard()
	{
		try {
			InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.toggleSoftInput(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Perform model search.
	*/
	private void doModelSearch(final String model)
	{		
		showWaitingDialog("Searching models...");
		Thread thread = new Thread() {
			@Override
			public void run() {
				_codesetCount =0;
				_codesetIndex = 0;
				try {					
					if (hasValidSession()) {						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									retrieveModelSearchCodesets();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});	
					}					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					stopWaitDialog();
				}
			}
		};
		thread.start();
		
	}

	/**
	 * Show buttons "Works!" and "Next code..." when the the test code flow is
	 * started.
	 */
	private void showCodesetButtons() {
		Button btn = (Button)findViewById(R.id.btWorks);
		btn.setVisibility(View.VISIBLE);
		Button btNextCode = (Button)findViewById(R.id.btNextCode);
		btNextCode.setVisibility(View.VISIBLE);

		if (this._isSetupMapRoute == true) {
			btNextCode.setText(R.string.btnNextCode_NotWork);
		} else {
			btNextCode.setText(R.string.btnNextCode);
		}

		btn = (Button)findViewById(R.id.btBrandSearch);
		btn.setVisibility(View.VISIBLE);
		EditText text = (EditText)findViewById(R.id.txtBrandName);
		text.setVisibility(View.VISIBLE);
		btn = (Button)findViewById(R.id.btmodelSearch);
		btn.setVisibility(View.VISIBLE);
		text = (EditText)findViewById(R.id.editModelname);
		text.setVisibility(View.VISIBLE);
	}

  	/** Hide buttons after the test code flow is finished. */
	private void hideCodesetButtons(boolean hideModelSearch) {
		Button btn = (Button)findViewById(R.id.btWorks);
		btn.setVisibility(View.INVISIBLE);

		btn = (Button)findViewById(R.id.btNextCode);
		btn.setVisibility(View.INVISIBLE);
		if(hideModelSearch) {
			btn = (Button)findViewById(R.id.btBrandSearch);
			btn.setVisibility(View.INVISIBLE);
			EditText text = (EditText)findViewById(R.id.txtBrandName);
			text.setVisibility(View.INVISIBLE);
			btn = (Button)findViewById(R.id.btmodelSearch);
			btn.setVisibility(View.INVISIBLE);
			text = (EditText)findViewById(R.id.editModelname);
			text.setVisibility(View.INVISIBLE);
		}
	}  

	/**
	 * Empty all the lists (device types, brands, test functions) after the test
	 * code flow is finished.
	 */
    private void resetDeviceTesting() {
		this._deviceTypes.clear();
		this._deviceTypesAdapter.notifyDataSetChanged();
		this._deviceTypeIndex = -1;
		resetDeviceTypeSelected(true);
	}
	
	/**
	 * Reset display after a device type is selected
	 */
	private void resetDeviceTypeSelected(boolean hideModelSearch) {
    	
		this._deviceFunctions.clear();
		this._deviceFunctionsAdapter.notifyDataSetChanged();
		this._deviceFunctionIndex = -1;
		
		this._brands.clear();
		this._brandsAdapter.notifyDataSetChanged();
		this._brandIndex = -1;
		this._codesetCount = 0;
		this._codesetIndex = -1;
		
		TextView tvCodesets = (TextView) findViewById(R.id.txtCodesets);
		tvCodesets.setText("");
		hideCodesetButtons(hideModelSearch);
    }
    
    /** Generate authentication key to access Setup service. **/
    public String getAuthenticationKey()
    {
    	final QuickSetCompatManager compatManager = (QuickSetCompatManager) sQuickSet.getManager(QuickSet.QUICKSET_COMPAT_MGR);
    	return compatManager.getAuthenticationKey();
    }
    
    /** Attach handlers for delete device button and add device button. */
    private void bindDeviceButtons() {
    	
    	Button btAddDevice = (Button) findViewById(R.id.btAdd);
    	btAddDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QuicksetSampleActivity.this.retrieveDeviceGroups();
			}

		});
    	
    	Button btDeleteDevice = (Button) findViewById(R.id.btDelete);
    	btDeleteDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QuicksetSampleActivity.this.deleteDevice();
			}

		});
	}

	private void bindMenuOptions() {
		this._lstMenus = (Spinner) findViewById(R.id.lstMenus);
		DeviceTypeListAdapter menuAdapter = new DeviceTypeListAdapter (this, android.R.layout.simple_spinner_dropdown_item,
				Arrays.asList(MenuOptions));
		this._lstMenus.setAdapter(menuAdapter);
		this._lstMenus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				if(position >= 0 && position < MenuOptions.length) {
					switch (position) {
						case 1:
						{
							// find remote
							Intent intent = new Intent(QuicksetSampleActivity.this,
									FindRemoteActivity.class);
							startActivity(intent);
							break;
						}
						case 2:
						{
							// voice search
							Intent intent = new Intent(QuicksetSampleActivity.this,
									VoiceSearchActivity.class);
							startActivity(intent);
							break;
						}
						case 3:
						{
							// firmware update
							Intent intent = new Intent(QuicksetSampleActivity.this,
									FirmwareUpdateActivity.class);
							startActivity(intent);
							break;
						}
						case 4:
						{
							// read device info
							Intent intent = new Intent(QuicksetSampleActivity.this,
									ReadRemoteActivity.class);
							startActivity(intent);
							break;
						}
						case 5:
						{
							// add EDID data
							Intent intent = new Intent(QuicksetSampleActivity.this,
									SetEdidDataActivity.class);
							startActivity(intent);
							break;
						}
						default:
							break;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
			}
		});
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

    /** Attach handlers for selecting a device from a list. */
    private void bindDeviceList() {
    	ListView lstDevices = (ListView) findViewById(R.id.lstDevices);
		this._devicesAdapter = new DeviceListAdapter(this, R.layout.device_item, this._devices);
		lstDevices.setAdapter(this._devicesAdapter);
		
		lstDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				QuicksetSampleActivity.this._deviceIndex = position;
				_devicesAdapter.notifyDataSetChanged();
			}
			
		});

		this._lstVolSource = (Spinner) findViewById(R.id.lstDevicesVolLock);
		this._volumeSourceAdapter = new DeviceTypeListAdapter(this, android.R.layout.simple_spinner_dropdown_item,
				this._volSourceDevices);
		this._lstVolSource.setAdapter(this._volumeSourceAdapter);
		this._lstVolSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
									   View view, int position, long id) {
				if(_volSourceDevices != null && position < _volSourceDevices.size() && position >= 0) {

					Remote remote = null;
					if(_remoteList != null && _remoteList.size() > 0) {
						int rIndex = _lstRemotes.getSelectedItemPosition();
						if (rIndex >= 0 && rIndex < _remoteList.size()) {
							remote = _remoteList.get(rIndex);
						}
					}
					String deviceName = _volSourceDevices.get(position);
					if(remote != null) {
						try {
							Remote[] remotes = QuicksetSampleApplication.getSetup().getAllRemotes(
									getAuthenticationKey());
							int curVolSource = 0;
							// get current remote's volume soruce
							if(remotes != null && remotes.length > 0) {
								for(Remote r : remotes) {
									if(r.Name.compareTo(remote.Name) == 0){
										// get the latest volume source
										curVolSource = r.VolumeSource;
										break;
									}
								}
							}
							if(_devices != null) {
								for(IDevice d : _devices) {
									Device device = (Device)d;
									if(device.Name.compareTo(deviceName) == 0 && device.Codeset != null && device.Codeset.length() == 5) {
										// select new volume source
										if (QuicksetSampleApplication.getSetup() != null && QuicksetSampleActivity.this._volumeLockInitialized == true) {
											DeviceType volSource = getDeviceType(device);
											if(volSource.toInteger() != curVolSource) {
												int result = QuicksetSampleApplication.getSetup().selectVolumeSourceDevice(
														getAuthenticationKey(), remote.Id, volSource.toInteger());
												Log.d(QuicksetSampleActivity.LOGTAG,
														String.format(" Selecting volume srouce (%d) for remote Id (%d) - Result = %d",
																volSource.toInteger(), remote.Id, result));
												return;
											} else {
												// no change on volume source
												return;
											}
										}
										else {
											//Skip the first time loading for volume source
											QuicksetSampleActivity.this._volumeLockInitialized = true;
										}
									}
								}
								if(deviceName != null && deviceName.startsWith("Unknown") && QuicksetSampleActivity.this._volumeLockInitialized == true) {
									// cannot select volume source device
									Toast.makeText(QuicksetSampleActivity.this, "Cannot select unknown device!", Toast.LENGTH_SHORT).show();
								}
								updateVolumeSourceDevice();
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

    /** Attach handlers for selecting a device type.
     * When a device type is selected, fill the list of brands for this device type.
     */
    private void bindDeviceTypeList() {
    	this._lstDeviceTypes = (Spinner) findViewById(R.id.lstDeviceTypes);
		this._deviceTypesAdapter = new DeviceTypeListAdapter(this, android.R.layout.simple_spinner_dropdown_item, this._deviceTypes);
		this._lstDeviceTypes.setAdapter(this._deviceTypesAdapter);
		
		this._lstDeviceTypes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				QuicksetSampleActivity.this._deviceTypeIndex = position;
				QuicksetSampleActivity.this._brandIndex = -1;
				Log.d(QuicksetSampleActivity.LOGTAG,
						" Loading brands for "
								+ QuicksetSampleActivity.this._deviceTypes
										.get(position));
				showWaitingDialog("Retrieving brands...");
				Thread thread = new Thread() {
					@Override
					public void run() {								
						try {				
							
							QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										QuicksetSampleActivity.this.retrieveBrands();
									} catch (Exception e) {
										e.printStackTrace();
										Log.e(LOGTAG, " Closing dialog : " + e.toString());
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

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
				QuicksetSampleActivity.this._deviceTypeIndex = -1;
			}
		});
    }
       
    /** When the device function is selected from a list, the corresponding IR is blasted.
     */
    private void bindDeviceFunctionsList() {
    	ListView lstDeviceFunctions = (ListView) findViewById(R.id.lstDeviceFunctions);
		this._deviceFunctionsAdapter = new FunctionListAdapter (this, R.layout.listitem, this._deviceFunctions);
		//this._deviceFunctionsAdapter.setButtonOnTouchListener(this._testFunctionTouchListener);
		//this._deviceFunctionsAdapter.setButtonOnClickListener(this._testFunctionClickListener);
		lstDeviceFunctions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position >= 0 && position < _currentCodesetFunctions.size()){
					try {
						QuicksetSampleActivity.this._deviceFunctionIndex = position;
						// start sending IR
						QuicksetSampleActivity.this.testStartIrFunction();
						// then stop immediately
						QuicksetSampleActivity.this.testStopIrFunction();
					} catch(Exception ex) {
						Log.e(LOGTAG, ex.toString());
					}
				}
			}

		});
		lstDeviceFunctions.setAdapter(this._deviceFunctionsAdapter);
		QuicksetSampleActivity.this._deviceFunctionIndex = 0;
    }
    
    /** On brand selection, codes are retrieved to be tested for this brand. */
    private void bindBrandsList() {
    	this._lstBrands = (Spinner) findViewById(R.id.lstBrands);
		this._brandsAdapter = new DeviceTypeListAdapter (this, android.R.layout.simple_spinner_dropdown_item, 
									this._brands);
		this._lstBrands .setAdapter(this._brandsAdapter);
		
		this._lstBrands .setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(QuicksetSampleActivity.LOGTAG, " Loading codesets for "
						+ QuicksetSampleActivity.this._brands.get(position));
				final Integer index = brandList.get(position);

				if (null != index) {
					QuicksetSampleActivity.this._brandIndex = index;

					// TODO: Add logic for model selection
					if (2 == index) {
						// Note: Using '2' is a magic mapping. Need to clean up
						// FIXME: Remove hard-coding
						final EditText modelName = findViewById(R.id.editModelname);
						modelName.setText("Hopper with Sling");
					}
					deviceBrandSelected();
				} else {
					Log.e(QuicksetSampleActivity.LOGTAG, " Position: " + position);

					Log.e(QuicksetSampleActivity.LOGTAG, " Couldn't find codesets for "
							+ QuicksetSampleActivity.this._brands.get(position));

					Log.e(QuicksetSampleActivity.LOGTAG, " Contains key: " + brandList.get(0));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
				QuicksetSampleActivity.this._brandIndex = -1;
			}

		});
    }
    
    /**
     * Adds the new device by codeset.
     */
    private void addNewDevice()
	{
		//set up dialog
        final Dialog dialog = new Dialog(this);        
        String title = "Add New Device";
                       
        dialog.setContentView(R.layout.adddevice);
        dialog.setTitle(title);
        dialog.setCancelable(true);
                
		final EditText textLabel = (EditText)dialog.findViewById(R.id.txtDeviceName);
		final EditText textBrand = (EditText)dialog.findViewById(R.id.txtDeviceBrand);
		final EditText textModel = (EditText)dialog.findViewById(R.id.txtDeviceModel);
		final EditText textCodeset = (EditText)dialog.findViewById(R.id.txtDeviceCodeset);
		
		//set up button
        final Button buttonOK = (Button) dialog.findViewById(R.id.buttonOK);
                               
        buttonOK.setOnClickListener(new OnClickListener() 
        	{
        		@Override
                public void onClick(View v) 
            	{	
        			dialog.dismiss();
        			QuicksetSampleActivity.this._addDeviceDialog = null;
        			QuicksetSampleActivity.this._loadingDialog = new ProgressDialog(QuicksetSampleActivity.this);            	
        			QuicksetSampleActivity.this._loadingDialog.setMessage("Adding new device...");
        			QuicksetSampleActivity.this._loadingDialog.setIndeterminate(true);
        			QuicksetSampleActivity.this._loadingDialog.setCancelable(false);
        			QuicksetSampleActivity.this._loadingDialog.show();			

        			Thread thread = new Thread() {
        				@Override 
        				public void run() {        			
		        			try {

		        				if(hasValidSession())
		        				{					
		        					String codeset = textCodeset.getText().toString().toUpperCase();
		        					// checking for input codeset name
		        					if(codeset.length() == 5) {
			        					Device device = QuicksetSampleApplication.getSetup().addDeviceByCodeset(
			        							QuicksetSampleApplication.getSession(),
			        							getAuthenticationKey(),
			        							textBrand.getText().toString(),
			        							textModel.getText().toString(),
			        							textLabel.getText().toString(),
			        							codeset);
			        					QuicksetSampleActivity.this._loadingDialog.dismiss();
			        					QuicksetSampleActivity.this._loadingDialog = null;
			        					
			        					if(device == null) {
			        						QuicksetSampleActivity.this.runOnUiThread(new Runnable() {
			        							@Override
			        							public void run() {
			        								int resultCode = ResultCode.ERROR;
			        								try
			        								{
			        									resultCode = QuicksetSampleApplication.getSetup().getLastResultCode();
			        								}
			        								catch (RemoteException e)
			        								{
			        									e.printStackTrace();
			        								}
			        								Log.d(QuicksetSampleActivity.LOGTAG, "addDeviceByCodeset result: Last result code = " + resultCode + " - " + ResultCode.getString(resultCode));	
			        								
			        								AlertDialog.Builder builder = new AlertDialog.Builder(QuicksetSampleActivity.this);
			        				    			builder.setIcon(android.R.drawable.ic_dialog_alert)
			        				    			.setTitle("Error!")
			        				    			.setMessage("Failed to add new device: " + ResultCode.getString(resultCode))
			        				    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        				    				@Override
			        				    				public void onClick(DialogInterface dialog, int whichButton) {    					
			        				    					dialog.dismiss();
			        				    				}
			        				    			})        
			        				    			.create();
			        				    			AlertDialog alert = builder.create();
			        				    			alert.show();
			        							}
			        						});
			        					} else {
			        						getLastResultCode("addDeviceByCodeset result: ");
			        					}
		        					}else {
		        						QuicksetSampleActivity.this._loadingDialog.dismiss();
			        					QuicksetSampleActivity.this._loadingDialog = null;
		        					}		        						
		        				} else {
		        					QuicksetSampleActivity.this._loadingDialog.dismiss();
		        					QuicksetSampleActivity.this._loadingDialog = null;
		        				}
		        			}
		        			catch (Exception e) {
		        				e.printStackTrace();
		        			}
        				}
        			};
        			thread.start();
                }
        	});
        
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new OnClickListener() 
        	{
        		@Override
                public void onClick(View v) 
            	{	
        			dialog.dismiss();
        			QuicksetSampleActivity.this._addDeviceDialog = null;
                }
        	});        
       
        //now that the dialog is set up, it's time to show it    
        dialog.show(); 
        this._addDeviceDialog = dialog;
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
//				IDevice device = null;
//				device = QuicksetSampleApplication.getSetup().addDevice(
//						QuicksetSampleApplication.getSession(),
//						getAuthenticationKey(),
//						this._codesetIndex, deviceLabel);


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


	/** Retrieve the codesets to test for the selected brand. */
	private void retrieveModelSearchCodesets() {
		try {
			if (hasValidSession()) {
				int brandIndex = _brandIndex;
				this._isSetupMapRoute = false;
				EditText text = (EditText)findViewById(R.id.editModelname);
				String modelName = text.getText().toString();

				// model search requires at least 2 characters/numbers
				if (modelName.length() >= MINUMUM_BRANDMODELTEXT) {
					this._codesetCount = QuicksetSampleApplication
							.getSetup()
							.getCodesetCountByModel(
									QuicksetSampleApplication.getSession(),
									getAuthenticationKey(),
									brandIndex, modelName);
					this._codesetIndex = 0;
					getLastResultCode("retrieveCodesets result: ");

					this._useSetupMaps = _chkUseSetupMaps.isChecked();
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
						this.setCodesetToTest();
					}

				}
			}
		} catch (Exception e) {
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
					/*// retrieve current bonded sling remotes
					HashMap<String, BluetoothDevice> devices = this._bleManager.getDiscoveredDevices();
					List<String> curentRemotes = new ArrayList<>();
					// check differences
					if (remotes != null) {
						for (Remote remote : remotes) {
							if(remote != null) {
								Log.d(QuicksetSampleActivity.LOGTAG, "Remote: " + remote.Id + " - " + remote.Version);
								if (devices.containsKey(remote.Name) == false) {
									// remote this remote form the list
									int result = QuicksetSampleApplication.getSetup().removeRemote(
											getAuthenticationKey(), remote.Id);
									Log.d(QuicksetSampleActivity.LOGTAG, "Remove Remote: " + remote.Id + " - " + result);
								} else {
									curentRemotes.add(remote.Name);
								}
							}
						}
					}

					// add any new bonded remotes
					Set<String> remoteAddresses = devices.keySet();
					for (String address : remoteAddresses) {
						if (curentRemotes.contains(address) == false) {
							Remote newRemote = QuicksetSampleApplication.getSetup().addRemote(
									getAuthenticationKey(), address);
							if (newRemote != null) {
								Log.d(QuicksetSampleActivity.LOGTAG, "New Remote added: " + newRemote.Id + " - " + newRemote.Name);
							}
						}
					}*/

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
					showCodesetButtons();
				} else {
					TextView tvCodesets = (TextView)findViewById(R.id.txtCodesets);
					tvCodesets.setText("No codes found!");

					this._deviceFunctions.clear();
					this._deviceFunctionsAdapter.notifyDataSetChanged();

					hideCodesetButtons(false);
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
    			TextView tvCodesets = (TextView) findViewById(R.id.txtCodesets);
				tvCodesets.setText(message);
			
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
				TextView tvCodesets = (TextView)findViewById(R.id.txtCodesets);

				if (this._codesetCount > 0) {
					tvCodesets.setText("Testing Code "
							+ (this._codesetIndex + 1) + " of "
							+ this._codesetCount);
					
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
					
					showCodesetButtons();
				} else {
					tvCodesets.setText("No codes found!");
					
					this._deviceFunctions.clear();
					this._deviceFunctionsAdapter.notifyDataSetChanged();
					
					hideCodesetButtons(false);
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
				TextView tvCodesets = (TextView) findViewById(R.id.txtCodesets);
				
				if (this._codesetCount > 0) {
					tvCodesets.setText("Testing Code "
							+ (this._codesetIndex + 1) + " of "
							+ this._codesetCount);
					
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
					
					showCodesetButtons();
				} else {
					tvCodesets.setText("No codes found!");
					
					this._deviceFunctions.clear();
					this._deviceFunctionsAdapter.notifyDataSetChanged();
					
					hideCodesetButtons(false);
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
			resetDeviceTesting();
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
	 * Retrieve the list of all possible brands for the current device type from
	 * ISetup.
	 * 
     * @see ISetup
     **/
    private void retrieveBrands() {
		try {
			if (hasValidSession()) {
				
				TextView tvCodesets = (TextView)findViewById(R.id.txtCodesets);
				tvCodesets.setText("");
				resetDeviceTypeSelected(false);
				// get the flag to retrieve top brands only or full brands
				boolean topBrandsOnly = this._chkTopBrands.isChecked();

				String[] brands = QuicksetSampleApplication
							.getSetup()
							.getBrands(
						QuicksetSampleApplication.getSession(),
									getAuthenticationKey(),
									this._deviceTypeIndex, topBrandsOnly);

				final String[] brandSubset = {
						"Comcast",
						"Dish Network",
						"TiVo"
				};

				brandList = new ArrayList<>();
				String brand;
				for (int i = 0; i < brands.length; i++) {
					brand = brands[i];
					for (int j = 0; j < brandSubset.length; j++) {
						if (brandSubset[j].equalsIgnoreCase(brand)) {
							Log.d("MVR", "Brand: {" +brandSubset[j] + "["+ j + "], " + brand + "[" + i + "]}");
							brandList.add(j, i);
						}
					}
				}

//				String[] b2 = new String[brandList.size()];
//				int k = 0;
//				for (Map.Entry<String, Integer> entry : brandList.entrySet()) {
//					b2[k] = brands[entry.getValue()];
//					k++;
//				}
//
				stopWaitDialog();				
				getLastResultCode("Retrieve brands: ");
				displayBrands(brandSubset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Display brands.
	 *
	 * @param brands the brands
	 */
	private void displayBrands(final String[] brands)
	{
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					_brands.clear();

					if (brands != null) {
						for (int index = 0; index < brands.length; index++) {
							Log.i(QuicksetSampleActivity.LOGTAG,
									"--- Brand Name: " + brands[index]);
							_brands.add(brands[index]);
						}
						Log.i(QuicksetSampleActivity.LOGTAG,
								"--- TOTAL Brands: " + _brands.size());
					}
					_brandIndex = -1;
					_brandsAdapter.notifyDataSetChanged();
					_lstBrands.setSelection(0);
					if(_brandIndex < 0 && _brands.size() > 0) {
						// set first selection
						_brandIndex = 0;
						deviceBrandSelected();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}
  
	/**
	 * Handle device configuration was exported
	 */
	private void handleDeviceConfigurationExported(final boolean success, final String file) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					String message = "";
					int icon = android.R.drawable.ic_dialog_info;
					if(success ){
						message = "Device configuration was successfully exported to " + file;						
					}
					else {
					int resultCode = ResultCode.ERROR;
						try {
							resultCode = QuicksetSampleApplication.getSetup()
									.getLastResultCode();
													
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						message = "Device configuration was failed to be exported: " + resultCode;
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
	 * Show waiting dialog.
	 */
	private void showWaitingDialog(final String message) {

		if (_waitDialog != null) {
			stopWaitDialog();
		}
		try {
			_waitDialog = new ProgressDialog(QuicksetSampleActivity.this);
			_waitDialog.setMessage(message);
			_waitDialog.setIndeterminate(true);
			_waitDialog.setCancelable(false);
			_waitDialog.show();
		} catch (Exception ex) {
			Log.d(LOGTAG, "showWaitingDialog: " + ex.toString());
		}
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
							QuicksetSampleActivity.this.resetDeviceTesting();
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
