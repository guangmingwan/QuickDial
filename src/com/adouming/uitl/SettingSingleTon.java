package com.adouming.uitl;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingSingleTon {
	 private static SettingSingleTon instance;  
     private SettingSingleTon (){}  
     public static final String PREFS_NAME = "MyPrefsFile";
     public static final String CALLIN_NUMBER = "callinnumber";
     private Context context;
     public static SettingSingleTon getInstance(Context _context) {  
     if (instance == null) {  
         instance = new SettingSingleTon();
         instance.context = _context;
     }  
     return instance;  
     }  
     public void setValue(String key,String value)
     {
    	// set preference  
    	 SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);  
    	 SharedPreferences.Editor editor = settings.edit();  
    	 editor.putString(key, value);  
    	 editor.commit(); 
    	 
     }
     public String getValue(String key)
     {
    	// get preference  
    	 SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);  
    	 String value = settings.getString(key, ""); 
    	 return value;
     }
}
