package com.adouming.service;

import com.adouming.uitl.SettingSingleTon;
import com.adouming.view.HomeDialActivity;
import com.adouming.view.HomeSettintActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
public class PhoneReceiver extends BroadcastReceiver {
    // debug TAG
    String TAG = "TelephonyReceiver";
    
    String incomingNumber = "";
    private static String outgoingNumber = "";
	private Context mContext;
	private Intent intent = new Intent("com.adouming.refreshcalllog.RECEIVER");  
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	mContext = context;
            
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        // call state when outgoing (去電撥出狀態)
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // get the number of outgoing (獲得撥出號碼)
        	if(this.getResultData() != null) {
        		outgoingNumber = this.getResultData();
        	}
            Log.i("TAG", "new outgoing number:" + outgoingNumber);
            
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
                if(outgoingNumber.length()>0) {
                	this.retriveLastCallSummary();
                }
                else
                {
                	
                }
                break;
            
	        }
	    }
	}
    void retriveLastCallSummary() {

        Log.i("*****retriveCallSummary******","Call retrive method worked");
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        Cursor managedCursor = mContext.getContentResolver().query(
                contacts, null, null, null, null);
        int cid = managedCursor.getColumnIndex( CallLog.Calls._ID ); 
        int name =  managedCursor.getColumnIndex( CallLog.Calls.CACHED_NAME ); 
        int number_label = managedCursor.getColumnIndex( CallLog.Calls.CACHED_NUMBER_LABEL );
        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        if( managedCursor.moveToLast() == true ) {
        	
        	 String [] temp = null;  
             temp = outgoingNumber.split(",");
             String newPhoneNumber = "";
             SettingSingleTon setting = SettingSingleTon.getInstance( mContext );
   		  	 String callinnumber = setting.getValue(SettingSingleTon.CALLIN_NUMBER);
			  if(callinnumber.length()<=0) {
			  	callinnumber =  HomeSettintActivity.TEL_CALLIN;
			  }
			 
             if(temp.length>1 && outgoingNumber.indexOf(callinnumber) >=0) {
             	newPhoneNumber = temp[1].trim();
             	newPhoneNumber = newPhoneNumber.replace("#", "");
             }
             else
             {
            	 //this.delayRefreshCallLog();
            	 //return ;
            	 newPhoneNumber = outgoingNumber;
             }
             
        	String id  = managedCursor.getString(cid); 
        	String cached_name  = this.getContactName(mContext, newPhoneNumber);
            String phNumber = managedCursor.getString( number );
            String phNumLabel = managedCursor.getString( number_label );
            String callDuration = managedCursor.getString( duration1 );
            String dir = null;
            sb.append( "\nid:---" + id + "---\nCached_name:--- "+cached_name +" \nphNumLabel :--- "+phNumLabel );
            sb.append( "\nPhone Number:--- "+newPhoneNumber +" \nCall duration in sec :--- "+callDuration );
            sb.append("\n----------------------------------");
            Log.i("*****Call Summary******", ""+sb);
           
            ContentValues newValues = new ContentValues();
            newValues.put(CallLog.Calls.CACHED_NAME, cached_name);
            newValues.put(CallLog.Calls.CACHED_NUMBER_LABEL, phNumLabel);
            newValues.put(CallLog.Calls.NUMBER, newPhoneNumber); 
            newValues.put(CallLog.Calls.DURATION, callDuration);
            //Uri uri = mContext.getContentResolver().insert(CallLog.Calls.CONTENT_URI, newValues);
			int result = mContext.getContentResolver().update(
			        Uri.parse("content://call_log/calls/" + id), 
			newValues,
			null,
			null);
			incomingNumber = "";
        }
        managedCursor.close();
        this.delayRefreshCallLog();
    }
    private void refreshcalllog()
    {
    	//发送Action广播  
        intent.putExtra(HomeDialActivity.BR_ACION, HomeDialActivity.BR_REFRESH_CALLLOG);
        intent.putExtra(HomeDialActivity.BR_PAYLOAD, 1);
        mContext.sendBroadcast(intent);  
    }
    private void delayRefreshCallLog()
    {
    	new Thread(new Runnable(){    
    		     public void run(){    
    		         try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
    		        refreshcalllog();    
    		     }    
    		 }).start(); 
    }
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}