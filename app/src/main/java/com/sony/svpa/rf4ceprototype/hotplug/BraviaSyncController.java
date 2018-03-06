package com.sony.svpa.rf4ceprototype.hotplug;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.sony.dtv.tvx.tvplayer.legacy.ITvPlayerService;
import com.sony.svpa.rf4ceprototype.events.HotPlugDetectedEvent;
import com.sony.svpa.rf4ceprototype.events.NewDeviceIdentifiedEvent;
import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;
import com.sony.svpa.rf4ceprototype.hotplug.util.BraviaSyncServiceConstants;
import com.sony.svpa.rf4ceprototype.hotplug.util.Constants;
import com.sony.svpa.rf4ceprototype.hotplug.util.DataParser;
import com.sony.svpa.rf4ceprototype.hotplug.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.sony.svpa.rf4ceprototype.hotplug.util.BraviaSyncServiceConstants.MSG_GET_DEVICE_LIST;


/**
 * Created by Breeze on 2/9/2018.
 */

public class BraviaSyncController {
    private static final String TAG = "HdmiHotplug";
    private static BraviaSyncController braviaSyncController = null;
    private ITvPlayerService mPlayerService;
    private String mInputId;
    private Messenger mSyncServiceMessenger;
    boolean isConnected = false;
    private int mInitState = -1; // NOT init = -1, initializing = 0, initdone = 1
    private final List<DeviceInformation> mDeviceList =
            new ArrayList<DeviceInformation>();
    // Bundle object key
    private static final String KEY_XML_STRING = "xml";
    // Message.what values
    private static final int MSG_SET_LISTENER = 1;
    private static final int MSG_UNSET_LISTENER = 2;
    private Context mContext;
    private String mCurrentActivePort = null;
    private int mLastActiveSource;
    private int mActiveSrcLogAddr = 0;
    private boolean isSupportMhl = false;

    private static final String CONNECTED_HDMI1 = "HDMI1";

    private static final String CONNECTED_HDMI2 = "HDMI2";

    private static final String CONNECTED_HDMI3 = "HDMI3";

    private static final String CONNECTED_HDMI4 = "HDMI4";

    private int mLastHotPlugConnectedDevice = -1;


    private BraviaSyncController(Context context){
        mContext = context;
        Intent stateServiceIntent = new Intent();
        stateServiceIntent.setClassName(Constants.TVAPP_PACKAGE,
                Constants.TVAPP_SERVICE_CLASS);
        mContext.bindService(stateServiceIntent,
                mStateServiceConnection, Context.BIND_AUTO_CREATE);
        Intent intent = new Intent();
        intent.setClassName(Constants.BRAVIASYNCSERVICE_PACKAGE,
                Constants.BRAVIASYNCSERVICE_CLASS);
        Log.i(TAG,  "Binding BraviaSyncService");
        mContext.bindService(intent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public static BraviaSyncController getInstance(Context context){
        if(braviaSyncController == null){
            braviaSyncController = new BraviaSyncController(context);
        }
        return  braviaSyncController;
    }

    private ServiceConnection mStateServiceConnection =
            new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.i(TAG, "mStateService onServiceDisconnected");
                    mPlayerService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    mPlayerService = ITvPlayerService.Stub.asInterface(service);
                    Log.d(TAG,"connected to ITvPlayerService");
                    //scheduleHdmiScan(getApplicationContext());
                    //mGetCfgInitStep++;
                    //continueInit();
                }
            };

    /*
* Callback when BraviaSyncService is connected / disconnected
*/
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG,  "onServiceDisconnected BraviaSyncService");

            // send message to unregister the listener for
            // BraviaSyncService
            sendMessage(MSG_UNSET_LISTENER);

            mSyncServiceMessenger = null;
            mServiceConnection = null;
            isConnected = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,  "onServiceConnected BraviaSyncService");

            mSyncServiceMessenger = new Messenger(service);

            // send message to register the listener for
            // BraviaSyncService
            sendMessage(MSG_SET_LISTENER);

            if (mInitState == -1 || isConnected == false) {
                //HdmiMonitorService.this.start();
            }
            isConnected = true;
            scanHdmiPorts();
        }
    };

    /**
     * This class listen to asynchronous message from BraviaSyncService
     */
    Messenger mSyncServiceMsgReceiver = new Messenger(new Handler(
            new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    BraviaSyncController.this.handlerMessage(msg);
                    return true;
                }
            }));

    /**
     * handler all message receiver from sync service
     * @param msg message to analyze
     */
    private void handlerMessage(Message msg){
        if (null != msg){
            Log.i(TAG,  Constants.EMPTY_STRING
                    + msg.what + ", " + msg.arg1 + ", " + msg.arg2);
            switch (msg.what){
                case MSG_GET_DEVICE_LIST:
                    Bundle obj = (Bundle) msg.obj;
                    String xmlString = obj.getString(KEY_XML_STRING);
                    Log.i(TAG,  "devices = " + xmlString);
                    List<DeviceInformation> parsedList =
                            DataParser.parseDeviceList(xmlString);
                    if (parsedList == null) {
                        Log.w(TAG, "parsedList is null");
                        return;
                    }
                    int size = parsedList.size();
                    DeviceInformation device;
                    //not supporting MHL, remove mobile devices from list
                    for (int i = size - 1; i >= 0; i--) {
                        device = parsedList.get(i);
                        if (device.getLogAddr() == Constants.LOGADD_MOBILE1
                                || device.getLogAddr() == Constants.LOGADD_MOBILE2
                                || device.getLogAddr() == Constants.LOGADD_MOBILE3
                                || device.getLogAddr() == Constants.LOGADD_MOBILE4) {
                            parsedList.remove(i);
                        }

                        Log.d(TAG, " osd name: " + device.getOsdName() + " vendor id: " + device.getVendorId() +
                        " hdmi port: " + device.getHdmiPort()+ "logical addr: " + device.getLogAddr() + " physical addr: " + device.getPhyAddr());
                    }
                    if (!Utils.compareDeviceList(parsedList, mDeviceList)) {
                        Collections.sort(parsedList,
                                new Comparator<DeviceInformation>() {

                                    @Override
                                    public int compare(DeviceInformation lhs,
                                                       DeviceInformation rhs) {
                                        if ((lhs.getVisiblePriority() < rhs
                                                .getVisiblePriority())
                                                || (lhs.getVisiblePriority() == rhs
                                                .getVisiblePriority() && lhs
                                                .getLogAddr() < rhs
                                                .getLogAddr())) {
                                            return -1;
                                        } else {
                                            return 1;
                                        }
                                    }
                                });
                        mDeviceList.clear();
                        mDeviceList.addAll(parsedList);
                        Log.d(TAG,"device list changed");
                        DeviceInformation deviceInformation = getHotPlugConnectedDevice();
                        if(deviceInformation != null){
                            Log.d(TAG, "device added:" + deviceInformation.toString());
                            Log.d(TAG, "Posting NewDeviceIdentifiedEvent");
                            EventBus.getDefault().post(new NewDeviceIdentifiedEvent(deviceInformation));



//                            Toast.makeText(mContext, "Device added: HDMI " + deviceInformation.getHdmiPort() + " Name: " +
//                                    deviceInformation.getOsdName() + " VendorID: " + deviceInformation.getVendorId(),Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast.makeText(mContext,"HDMI Device List Changed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"HDMI Device List Changed no deviceinfo found");
                           // EventBus.getDefault().post(new NewDeviceIdentifiedEvent(null));
                        }
                    }
                    else{
                        Log.d(TAG,"device list unchanged and no device found");
                       // EventBus.getDefault().post(new NewDeviceIdentifiedEvent(null));
                    }

                    break;

                case BraviaSyncServiceConstants.MSG_NOTIFY_HOTPLUG_EVENT:
                    String connectStatus = "unknown";
                    boolean connected = false;
                    if(msg.arg2 == 0) {
                        connectStatus = "disconnected";
                        connected = false;
                        mLastHotPlugConnectedDevice = -1;
                        //Toast.makeText(mContext,"HDMI " + msg.arg1 + " was " + connectStatus, Toast.LENGTH_SHORT).show();
                    } else if(msg.arg2 == 1){
                        connectStatus = "connected";
                        connected = true;
                        //save hdmi port
                        mLastHotPlugConnectedDevice  = msg.arg1;
                    }
                    Log.d(TAG,"MSG_NOTIFY_HOTPLUG_EVENT : HDMI at port: " + msg.arg1 + " was " + connectStatus);
                    //Toast.makeText(mContext,"HDMI " + msg.arg1 + " was " + connectStatus, Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new HotPlugDetectedEvent(msg.arg1, connected));
                    break;
                case BraviaSyncServiceConstants.MSG_NOTIFY_ACTIVE_SOURCE_CHANGED:
                    //Toast.makeText(mContext,"Active Source changed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"active source changed event msg received");
                    sendMessage(BraviaSyncServiceConstants.MSG_GET_ACTIVE_SOURCE);
                    break;

                case BraviaSyncServiceConstants.MSG_NOTIFY_DEVICE_INFO_CHANGED:
                    //Toast.makeText(mContext,"Device Info changed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"device info changed event msg received logical address: " + msg.arg1);
                    sendMessage(MSG_GET_DEVICE_LIST);
                    //sendMessage(BraviaSyncServiceConstants.MSG_GET_DEVICE_INFO,msg.arg1,0,null);

                    //scanHdmiPorts();
                    break;

                case BraviaSyncServiceConstants.MSG_GET_ACTIVE_SOURCE:
                    mLastActiveSource = msg.arg1;
                    Log.d(TAG,"active source:" + mLastActiveSource + "source name: " + getActiveSourcePortName());
                    break;

                case BraviaSyncServiceConstants.MSG_GET_DEVICE_INFO:
                    Log.d(TAG,"received get device info");
                    break;

                default:
                    Log.d(TAG,"received unhandled message");
                    break;

            }

        }

    }
    /*
 * Send a message to BraviaSyncService
 * @param msgCode The Id of message to send
 */
    private void sendMessage(int msgCode) {
        sendMessage(msgCode, 0, 0, null);
    }

    /*
 * Send a message to BraviaSyncService
 * @param msgCode The Id of message to send
 * @param argument1 The first argument of message to send
 * @param argument2 The second argument of message to send
 * @param obj The object to provide additional information
 */
    private void sendMessage(int msgCode, int argument1, int argument2,
                             Object obj) {
        Message msg = Message.obtain();
        msg.what = msgCode;
        msg.replyTo = mSyncServiceMsgReceiver;
        msg.arg1 = argument1;
        msg.arg2 = argument2;
        msg.obj = obj;

        if (null != mSyncServiceMessenger) {
            try {
                mSyncServiceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.w(TAG,  "Caught RemoteException");
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,  "BraviaSyncService was not bound!");
        }
    }

    private String getActiveSourcePortName(){
        try{
            Bundle b = mPlayerService.getCurrentInputInfo(0);
            if(b != null){
                String type = b.getString(Constants.DEVICE_TYPE);
                Log.d(TAG, "type = " + type);
                if (type != null && type.equals(Constants.EXTERNAL_INPUT)){
                    String inputId = b.getString(Constants.INPUT_ID);
                    mCurrentActivePort =
                            b.getString(Constants.TYPE_EXTERNAL_INPUT);
                    Log.d(TAG, "active input: " + inputId);
                }
                else {mCurrentActivePort = type;}
                TvInputManager manager =
                        (TvInputManager) mContext
                                .getSystemService(Context.TV_INPUT_SERVICE);
                List<TvInputInfo> inputList = manager.getTvInputList();
                mInputId = b.getString(Constants.INPUT_ID);
                for (int i = 0; i < inputList.size(); i++) {
                    TvInputInfo info = inputList.get(i);
                    String portID = info.getId();
                    if (mInputId != null && mInputId.contains(portID)) {
                        mCurrentActivePort =
                                info.loadLabel(mContext).toString();
                        break;
                    }
                }
            }
            else {
                Log.d(TAG, "no active port");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void scanHdmiPorts(){
        Log.d(TAG,"scanHdmiPorts");
        sendMessage(MSG_GET_DEVICE_LIST);
    }

    public void onDestroy(){
        Log.d(TAG,"onDestroy");
        if(null != mServiceConnection && isConnected && mContext != null){
            try {
                mServiceConnection.onServiceDisconnected(null);
                mStateServiceConnection.onServiceDisconnected(null);
                mContext.unbindService(mServiceConnection);
                mContext.unbindService(mStateServiceConnection);
            } catch (RuntimeException e) {
                Log.w(TAG, e.getMessage());
            }
        }
        mServiceConnection = null;
        braviaSyncController = null;
    }

    /**
     * update active source for control device purpose
     */
    private void updateActiveSource() {
        //Check isDeviceExists(mActiveSrcLogAddr) when unplug HDMI
        String hdmiPort = getCurrentHDMIPort();
        Log.d(TAG,  "Current HDMI Portt = " + hdmiPort);
        List<DeviceInformation> listDevices = new ArrayList<DeviceInformation>();
        boolean isExistActiveSource = isDeviceExists(mLastActiveSource);
        if (!TextUtils.isEmpty(hdmiPort)) {
            int length = mDeviceList.size();
            for (int i = 0; i < length; i++) {
                DeviceInformation device = mDeviceList.get(i);
                if (hdmiPort.contains(String.valueOf(device.getHdmiPort()))) {
                    if (isExistActiveSource && (device.getLogAddr() == mLastActiveSource)) {
                        mActiveSrcLogAddr = mLastActiveSource;
                        Log.i(TAG,  "updateActiveSource : mActiveSrcLogAddr = "
                                + mActiveSrcLogAddr);
                        return;
                    } else {
                        listDevices.add(device);
                    }
                }
            }
            mActiveSrcLogAddr = getLogicalAddress(listDevices);
            if (!isDeviceExists(mActiveSrcLogAddr)) {
                mActiveSrcLogAddr = 0;
            }
        } else {
            mActiveSrcLogAddr = 0;
        }
        Log.i(TAG, "updateActiveSource : mActiveSrcLogAddr = "
                + mActiveSrcLogAddr);
    }

    /**
     * Check logical address of device is exists in device list
     * @param logAddr logical address
     * @return true if device exists, false in other hand
     */
    private boolean isDeviceExists(int logAddr) {
        int length = mDeviceList.size();
        for (int i = 0; i < length; i++) {
            DeviceInformation device = mDeviceList.get(i);
            if (device.getLogAddr() == logAddr) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get current HDMI port
     * @return HDMI port is corresponding with current view.
     */
    private String getCurrentHDMIPort() {
        String activeName = getActiveSourcePortName();
        Log.d(TAG,  "mInputId = " + mInputId);
        if(!TextUtils.isEmpty(mInputId) && !TextUtils.isEmpty(activeName)) {
            if (mInputId.contains(CONNECTED_HDMI1) || activeName.contains(Constants.STR_HDMI_PORT1)) {
                return Constants.STR_HDMI_PORT1;
            } else if (mInputId.contains(CONNECTED_HDMI2) || activeName.contains(Constants.STR_HDMI_PORT2)) {
                return Constants.STR_HDMI_PORT2;
            } else if (mInputId.contains(CONNECTED_HDMI3) || activeName.contains(Constants.STR_HDMI_PORT3)) {
                return Constants.STR_HDMI_PORT3;
            } else if (mInputId.contains(CONNECTED_HDMI4) || activeName.contains(Constants.STR_HDMI_PORT4)) {
                return Constants.STR_HDMI_PORT4;
            } else {
                return Constants.EMPTY_STRING;
            }
        } else {
            return Constants.EMPTY_STRING;
        }
    }

    /**
     * Get logical address of current input
     * @param listDevices list devices are connected
     * @return Logical address of device in current view
     */
    private int getLogicalAddress(List<DeviceInformation> listDevices) {
        int logAddr = 0;
        if (!listDevices.isEmpty()) {
            //If list devices only have a device then checking this device is audio system or not.
            if ((listDevices.size() == 1) && (listDevices.get(0).getLogAddr() == Constants.LOGADD_AUDIOSYS)) {
                logAddr = Constants.LOGADD_AUDIOSYS;
            } else {
                for (DeviceInformation device : listDevices) {
                    if (device.getLogAddr() != Constants.LOGADD_AUDIOSYS) {
                        logAddr = device.getLogAddr();
                        break;
                    }
                }
            }
        }
        return logAddr;
    }

    /**
     * Retrieve the current viewing state of TV.
     * @return Current state of TV input, either viewing TV, MOBILE, CEC, or
     *         OTHER
     */
    public int getInputState() {
        Log.i(TAG, "getInputState");
        updateActiveSource();
        if (!isTvAppRunning()) {
            Log.d(TAG,"isTvAppRunning == false");
            return Constants.S_VIEWING_UNKNOWN;
        }
        Log.i(TAG,"isTvAppRunning == true");

        String activeSrcPortName = getActiveSourcePortName();
        if (TextUtils.isEmpty(activeSrcPortName)) {
            return Constants.S_VIEWING_OTHER;
        }

        /* Check if viewing TV service */
        TvInputManager manager =
                (TvInputManager) mContext
                        .getSystemService(Context.TV_INPUT_SERVICE);
        List<TvInputInfo> inputList = manager.getTvInputList();
        Log.d(TAG,"inputList size = " + inputList.size());
        activeSrcPortName = activeSrcPortName.toLowerCase(Locale.getDefault());

        for (int i = 0; i < inputList.size(); i++) {
            TvInputInfo info = inputList.get(i);
            String label = info.loadLabel(mContext).toString();
            Log.d(TAG,"input = " + label + ", " + info.getId());
            label = label.toLowerCase(Locale.getDefault());
            if (label.contains(activeSrcPortName)
                    && info.getType() == TvInputInfo.TYPE_TUNER) {
                return Constants.S_VIEWING_TV;
            }
        }
        /* Check if viewing CEC device */
        Log.d(TAG, "mActiveSrcLogAddr = " + mActiveSrcLogAddr);
        if (isSupportMhl()
                && (mActiveSrcLogAddr == Constants.LOGADD_MOBILE1
                || mActiveSrcLogAddr == Constants.LOGADD_MOBILE2
                || mActiveSrcLogAddr == Constants.LOGADD_MOBILE3
                || mActiveSrcLogAddr == Constants.LOGADD_MOBILE4 || (mCurrentActivePort
                .equals(Constants.SOURCENAME_MOBILE) && isDeviceExists(Constants.LOGADD_MOBILE1)))) {
            Log.d(TAG,"LOGADDR_MOBILE");
            return Constants.S_VIEWING_MOBILE;
        } else if ((mActiveSrcLogAddr != Constants.LOGADDR_TV)
                && (mActiveSrcLogAddr != Constants.LOGADDR_AUDIO_SYS)) {
            // If active source is CEC and it is not TV or Mobile or Audio
            // System
            // then it must be CEC controllable device
            Log.d(TAG, "S_VIEWING_CEC");
            return Constants.S_VIEWING_CEC;
        }
        return Constants.S_VIEWING_OTHER;
    }

    /**
     * Call this API to get name of the current input source
     * @return Name of current input source
     */
    public String getActiveSourceName() {
        String name = Constants.EMPTY_STRING;
        int mState = getInputState();

        Log.i(TAG, "getInputState = " + mState);
        if (mState != Constants.S_VIEWING_UNKNOWN) {
            switch (mState) {
                case Constants.S_VIEWING_TV:
                    Log.i(TAG, Constants.SOURCENAME_TV);
                    name = getActiveSourcePortName();
                    break;
                case Constants.S_VIEWING_MOBILE:
                case Constants.S_VIEWING_CEC:
                    Log.i(TAG,"CEC");
                    name = getCecActiveSourceName();
                    if (Constants.EMPTY_STRING.equals(name)) {
                        name = getActiveSourcePortName();
                    }
                    break;
                case Constants.S_VIEWING_OTHER:
                    Log.i(TAG, "OTHER");
                    name = getActiveSourcePortName();
                    break;
                default:
                    name = getActiveSourcePortName();
                    break;
            }
        } else {
            Log.i(TAG, "getInputState = S_VIEWING_UNKNOWN");
        }
        Log.i(TAG, "getActiveSourceName: " + name);
        return name;
    }

    /*
 * Utility function to get the name of current input if that input is CEC
 * device
 * @return Name of current CEC input
 */
    private String getCecActiveSourceName() {
        int length;
        int logAddr;
        String name = Constants.EMPTY_STRING;

        try {
            if (mDeviceList != null) {
                Log.d(TAG, "device list not null");

                length = mDeviceList.size();
                Log.i(TAG, "device list size = " + length);

                for (int i = 0; i < length; i++) {
                    DeviceInformation device = mDeviceList.get(i);

                    if (device != null) {
                        logAddr = device.getLogAddr();
                        Log.d(TAG, "got : " + logAddr
                                + ", find : " + mActiveSrcLogAddr);
                        if (logAddr == mActiveSrcLogAddr) {
                            name =
                                    DeviceInformation.getDevName(
                                            mContext,
                                            logAddr);
                            break;
                        }
                    }
                }
            } else {
                Log.w(TAG,"device list null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public boolean isSupportMhl() {
        return isSupportMhl;
    }

    /**
     * Check TvScreenApplication is running or not
     * @return true if TvScreenApplication is running
     */

    public boolean isTvAppRunning() {
        boolean bResult = false;


        Log.i(TAG, "run is TVApp running");
        ActivityManager manager =
                (ActivityManager) mContext
                        .getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> processInfos =
                manager.getRunningAppProcesses();
        if (processInfos == null) {
            return false;
        }
        for (int i = 0; i < processInfos.size(); i++) {
            String name = processInfos.get(i).processName;
            Log.i(TAG,"app : " + name);
            if (Constants.TVAPP_PACKAGE.equals(name)) {
                bResult = true;
                break;
            }
        }
        return bResult;
    }

    /*note: only works for direct plug into HDMI of TV*/
    private DeviceInformation getHotPlugConnectedDevice(){
        if(this.mLastHotPlugConnectedDevice != -1){
            for(int x = 0; x < mDeviceList.size(); x++){
                DeviceInformation deviceInformation = mDeviceList.get(x);
                if(mLastHotPlugConnectedDevice == deviceInformation.getHdmiPort()){
                    return deviceInformation;
                }
            }
        }
        return null;
    }
}
