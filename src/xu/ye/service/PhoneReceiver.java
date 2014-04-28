package xu.ye.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
public class PhoneReceiver extends BroadcastReceiver {
    // debug TAG
    String TAG = "TelephonyReceiver";
    
    String incomingNumber;
    
    @Override
    public void onReceive(Context context, Intent intent) {
            
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        // call state when outgoing (去電撥出狀態)
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // get the number of outgoing (獲得撥出號碼)
            Log.i("TAG", "new outgoing number" + this.getResultData());
        }else {
            // get call state when state changed
            switch (telephonyManager.getCallState()) {
            
            // incoming call active state (來電接通狀態)
            // outgoing call first state (去電撥出後，首先監聽到的狀態)
            // and outgoing call active state (去電接通狀態)
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i("onCallStateChanged", "CALL_STATE_OFFHOOK");
                
                break;
            // incoming call state (來電響鈴狀態)
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i("onCallStateChanged", "CALL_STATE_RINGING");
                
                // get incoimg number (獲取來電號碼)
                incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                
                Log.i("TAG", "get incoimg number" + incomingNumber);
                
                break;
            // incoming or outgoing call idle state (無論來電或去電的掛斷閒置狀態)
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i("onCallStateChanged", "CALL_STATE_IDLE");
                
                break;
            
	        }
	    }
	}
}