package xu.ye.view;

import java.util.ArrayList;
import java.util.HashMap;

import xu.ye.R;
import xu.ye.uitl.SettingSingleTon;
import android.app.Activity;
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
                Object o = listview.getItemAtPosition(position);
                String pen = o.toString();
                Toast.makeText(getApplicationContext(), "You have chosen the pen: " + " " + pen, Toast.LENGTH_LONG).show();

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
            	tempHashMap.put("info", "还未设置接入号码");
            }
            else
            {
            	tempHashMap.put("info", "当前接入号码:" + callinnumber);
            }
            arrayList.add(tempHashMap);
            
            tempHashMap = new HashMap<String, Object>(); 
            tempHashMap.put("image", R.drawable.icon);  
            tempHashMap.put("title", "客服电话");  
            tempHashMap.put("info", "一键拨打客服电话");  
            arrayList.add(tempHashMap);
            
            tempHashMap = new HashMap<String, Object>();  
            tempHashMap.put("image", R.drawable.icon);  
            tempHashMap.put("title", "查话费");  
            tempHashMap.put("info", "一键查话费");  
            arrayList.add(tempHashMap); 
            
	        
	        return arrayList;  
	    } 
	
	   
	
	
	
}
