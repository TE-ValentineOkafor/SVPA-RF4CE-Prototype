package com.sony.svpa.rf4ceprototype.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sony.svpa.rf4ceprototype.events.EpgAreaChangedEvent;
import com.sony.svpa.rf4ceprototype.events.EpgCountryChangedEvent;
import com.sony.svpa.rf4ceprototype.events.EpgProviderChangedEvent;
import com.sony.svpa.rf4ceprototype.models.EpgArea;
import com.sony.svpa.rf4ceprototype.models.EpgCountry;
import com.sony.svpa.rf4ceprototype.models.Provider;
import com.uei.control.Remote;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by valokafor on 3/6/18.
 */

public class SettingsHelper {

    private static final String TAG = SettingsHelper.class.getSimpleName();
    private static final String PREFS_FILE = "prefs";
    private static SettingsHelper INSTANCE;

    private static final String MESSAGING_TOKEN = "MessagingToken";
    private static final String PAIRING_COMPLETE = "PairingComplete";
    private static final String TV_NAME = "TvName";
    private static final String EPG_COUNTRY = "EpgCountry";
    private static final String EPG_AREA = "EpgArea";
    private static final String EPG_ZIP_CODE = "EpgZipCode";
    private static final String EPG_PROVIDER = "EpgProvider";
    private static final String REST_SERVER = "RestServer";

    private Context context;



    /**
     * Get helper instance.
     */
    public static SettingsHelper getHelper(Context context) {
        if (INSTANCE == null) {
            // ensure application context is used to prevent leaks
            INSTANCE = new SettingsHelper(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private SettingsHelper(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }


    public String getMessagingToken() {
        return getSharedPreferences().getString(MESSAGING_TOKEN, null);
    }

    public boolean isPairingComplete() {
        return getSharedPreferences().getBoolean(PAIRING_COMPLETE, false);
    }

    public void setPairingComplete(boolean complete) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PAIRING_COMPLETE, complete);
        editor.commit();
    }

    public String getTvName() {
        return getSharedPreferences().getString(TV_NAME, null);
    }

    public void setTvName(String tvName) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(TV_NAME, tvName);
        editor.commit();

    }

    public void setEpgCountry(EpgCountry country) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(EPG_COUNTRY, country.toString());
        editor.commit();
        EventBus.getDefault().post(new EpgCountryChangedEvent(country));
    }

    public EpgCountry getEpgCountry() {
        String country = getSharedPreferences().getString(EPG_COUNTRY, EpgCountry.USA.toString());
        return EpgCountry.valueOf(country);
    }

    public String getEpgZipCode() {
        return getSharedPreferences().getString(EPG_ZIP_CODE, null);
    }

    public void setEpgZipCode(String zipCode) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(EPG_ZIP_CODE, zipCode);
        editor.commit();
    }

    public Provider getEpgProvider() {
        String providerJson = getSharedPreferences().getString(EPG_PROVIDER, null);
        if (providerJson != null) {
            try {
                return new Gson().fromJson(providerJson, Provider.class);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setEpgArea(EpgArea area) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(EPG_AREA, new Gson().toJson(area));
        editor.commit();
        EventBus.getDefault().post(new EpgAreaChangedEvent(area));
    }

    public EpgArea getEpgArea() {
        String json = getSharedPreferences().getString(EPG_AREA, null);
        if (json != null) {
            try {
                return new Gson().fromJson(json, EpgArea.class);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setEpgProvider(Provider epgProvider) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(EPG_PROVIDER, new Gson().toJson(epgProvider));
        editor.commit();
        EventBus.getDefault().post(new EpgProviderChangedEvent(epgProvider));
    }



    public Date getEpgEndDate() {
        Calendar calendar = Calendar.getInstance();
        // increment the calendar as needed to return the desired EPG end date
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        return calendar.getTime();
    }

    public void setRemoteMacAddress(Remote remote){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constants.REMOTE_MAC_ADDRESS, remote.Name);
        editor.commit();

    }


}
