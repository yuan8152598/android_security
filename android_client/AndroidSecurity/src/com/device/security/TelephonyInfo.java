package com.device.security;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.device.security.TelephonyIntents;

public class TelephonyInfo extends Activity{
    private final static String TAG = "TelephonyInfo";
    private Context mContext;   
    private boolean Debug = true;
  
    private static boolean simState;

    private SharedPreferences mPrefs;
    public TelephonyInfo(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        if(Debug) {
            Log.d(TAG,"TelephonyInfo create successful");
        }

        simState = getDefaultSimState();

        // Register for misc other intent broadcasts.
        IntentFilter intentFilter =
                new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_SIM_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_SERVICE_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_EMERGENCY_CALLBACK_MODE_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public void GetTelephonyInfo() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        String tel = tm.getLine1Number();
        String imei = tm.getSimSerialNumber();
        String imsi = tm.getSubscriberId();
        if(Debug) {        
            Log.d(TAG,"Get the telephony info deviceid = " + deviceid + "  tel = " + tel + "   imei = " + imei +  " imsi =  " + imsi);
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Debug) {
                Log.d(TAG,"received broadcast " + action);
            }
            if (action.equals(TelephonyIntents.ACTION_SIM_STATE_CHANGED)) {
                if (Debug) {
                    Log.d(TAG,"Receive ACTION_SIM_STATE_CHANGED");
                }
                HandlerSimChanged();
            }
        }
    };

    private void HandlerSimChanged() {
        TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);   
        int state = tm.getSimState();
        boolean NewState =  false;
        switch (state) {  
            case TelephonyManager.SIM_STATE_READY :
                NewState = true;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN :
            case TelephonyManager.SIM_STATE_ABSENT :
            case TelephonyManager.SIM_STATE_PIN_REQUIRED :
            case TelephonyManager.SIM_STATE_PUK_REQUIRED :
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
            default:
                NewState = false;
                break;
        }
        if (Debug) {
            Log.d(TAG, "simState = " + simState);
        }
        saveSimState(NewState);
    }

    private void saveSimState(boolean state) {
        if(Debug) {
            Log.d(TAG, "saveSimState to SharedPreferences");
        }
        mPrefs = mContext.getSharedPreferences("com.devices.preferences",MODE_APPEND|Context.MODE_WORLD_WRITEABLE);
        Editor edit = mPrefs.edit();
        edit.putBoolean("Sim_State",state);
        edit.commit();
    }

    private boolean getDefaultSimState() {
        mPrefs = mContext.getSharedPreferences("com.devices.preferences",MODE_APPEND|Context.MODE_WORLD_WRITEABLE);
        boolean state = mPrefs.getBoolean("Sim_State",false);
        return state;
    }
}
