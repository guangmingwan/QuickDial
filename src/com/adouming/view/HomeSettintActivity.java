package com.adouming.view;

import java.util.ArrayList;
import java.util.HashMap;

import com.adouming.quickdial.R;
import com.adouming.uitl.SettingSingleTon;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class HomeSettintActivity extends Activity {
	
	
public ListView listview;	
 	public static String TAG = "homesetting";
 	private Intent intent = new Intent("com.adouming.refreshcalllog.RECEIVER");
 	public static String TEL_CALLIN = "844604";
 	public static String TEL_CUSTOMSERVICE = "18613016788";
 	public static String TEL_ACCOUNTCHECK = "66680123,2";
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_setting_page);
		// 获取虚拟的数据，数据的格式有严格的要求哦  
        ArrayList<HashMap<String, Object>> data = getData();  
        //模仿SimpleAdapter实现的自己的adapter  
        HomeSettingAdapter adapter = new HomeSettingAdapter(this, data); 
        listview = (ListView) findViewById(R.id.setting_list);
        listview.setAdapter(adapter); 
        listview.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                //Object o = listview.getItemAtPosition(position);
                //String pen = o.toString();
                //Toast.makeText(getApplicationContext(), "You have chosen the pen: " + " " + pen, Toast.LENGTH_LONG).show();
                if(position == 1) { //call 客服
                	intent.putExtra(HomeDialActivity.BR_ACION, HomeDialActivity.BR_DIAL_NORMAL);
    		        intent.putExtra(HomeDialActivity.BR_PAYLOAD, TEL_CUSTOMSERVICE);
    		        Context ctx = getApplicationContext();
    		        ctx.sendBroadcast(intent);
                }
                else if(position == 2) { //查话费
                	intent.putExtra(HomeDialActivity.BR_ACION, HomeDialActivity.BR_DIAL_NORMAL);
    		        intent.putExtra(HomeDialActivity.BR_PAYLOAD, TEL_ACCOUNTCHECK);
    		        Context ctx = getApplicationContext();
    		        ctx.sendBroadcast(intent);
                }
            }
  
        }       
		);
	}
	@Override  
    protected void onStart() {  
        super.onStart();  
        Log.e(TAG, "start onStart~~~");  
    }  
      
    @Override  
    protected void onRestart() {  
        super.onRestart();  
        Log.e(TAG, "start onRestart~~~");  
    }  
      
    @Override  
    protected void onResume() {  
        super.onResume();  
        HomeSettingAdapter adapter = (HomeSettingAdapter)listview.getAdapter();
        adapter.setData(getData());
        Log.e(TAG, "start onResume~~~");  
    }  
      
    @Override  
    protected void onPause() {  
        super.onPause();  
        Log.e(TAG, "start onPause~~~");  
    }  
      
    @Override  
    protected void onStop() {  
        super.onStop();  
        Log.e(TAG, "start onStop~~~");  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        Log.e(TAG, "start onDestroy~~~");  
    }  
	  private ArrayList<HashMap<String, Object>> getData(){  
		  
		  SettingSingleTon setting = SettingSingleTon.getInstance(getApplicationContext());
		  String callinnumber = setting.getValue(SettingSingleTon.CALLIN_NUMBER);
	        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String,Object>>();  
	         
            HashMap<String, Object> tempHashMap = new HashMap<String, Object>();  
              
            tempHashMap.put("image", R.drawable.icon);  
            tempHashMap.put("title", "接入号码");
            if(callinnumber.length()<=0) {
            	callinnumber = TEL_CALLIN;
            }
            tempHashMap.put("info", "当前接入号码:" + callinnumber);
            
            arrayList.add(tempHashMap);
            
            tempHashMap = new HashMap<String, Object>(); 
            tempHashMap.put("image", R.drawable.icon);  
            tempHashMap.put("title", "客服电话");  
            tempHashMap.put("info", "一键拨打客服电话 " + TEL_CUSTOMSERVICE);  
            arrayList.add(tempHashMap);
            
            tempHashMap = new HashMap<String, Object>();  
            tempHashMap.put("image", R.drawable.icon);  
            tempHashMap.put("title", "查话费");  
            tempHashMap.put("info", "一键拨打查话费电话 " + TEL_ACCOUNTCHECK);  
            arrayList.add(tempHashMap); 
            
	        
	        return arrayList;  
	    } 
	
	   
	
	
	
}
