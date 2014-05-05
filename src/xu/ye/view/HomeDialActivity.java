package xu.ye.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xu.ye.R;
import xu.ye.application.MyApplication;
import xu.ye.bean.CallLogBean;
import xu.ye.bean.ContactBean;
import xu.ye.uitl.SettingSingleTon;
import xu.ye.view.adapter.HomeDialAdapter;
import xu.ye.view.adapter.T9Adapter;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeDialActivity extends Activity implements OnClickListener {
	
	private AsyncQueryHandler asyncQuery;
	
	private HomeDialAdapter adapter;
	private ListView callLogList;
	
	private List<CallLogBean> list;
	
	private LinearLayout bohaopan;
	private LinearLayout keyboard_show_ll;
	private Button keyboard_show;
	
	private Button phone_view;
	private Button delete;
	private Button dialCallNormal;
	private Button dialCallPro;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private SoundPool spool;
	private AudioManager am = null;
	
	private MyApplication application;
	private ListView listView;
	private T9Adapter t9Adapter;
	
	private Intent mIntent; 
	private MsgReceiver msgReceiver; 
	private static int MAX_PHONENUMBER_LEN = 50;
	
	public static String BR_ACION = "action";
	public static String BR_PAYLOAD = "payload";
	public static String BR_REFRESH_CALLLOG = "refreshcalllist";
	public static String BR_REDIAL = "redial";
	 /** 
     * 广播接收器 
     * @author len 
     * 
     */  
    public class MsgReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            //拿到进度，更新UI
        	String action = intent.getStringExtra(BR_ACION);
        	if(action.equals(BR_REFRESH_CALLLOG)) {
	            int payload = intent.getIntExtra(BR_PAYLOAD, 0);  
	            Log.i("*****refresh call log list******", ""+payload);
	            init();
        	}
        	else if(action.equals(BR_REDIAL)) {
        		String redialNumber = intent.getStringExtra(BR_PAYLOAD);
        		phone_view.setText(redialNumber);
        		bohaopan.setVisibility(View.VISIBLE);
    			keyboard_show_ll.setVisibility(View.INVISIBLE);
        	}
            
        }  
          
    }  
    
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		//动态注册广播接收器  
        msgReceiver = new MsgReceiver();  
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction("com.adouming.refreshcalllog.RECEIVER");  
        registerReceiver(msgReceiver, intentFilter); 
        
        
		setContentView(R.layout.home_dial_page);
		
		application = (MyApplication)getApplication();
		listView = (ListView) findViewById(R.id.contact_list);
		
		bohaopan = (LinearLayout) findViewById(R.id.bohaopan);
		keyboard_show_ll = (LinearLayout) findViewById(R.id.keyboard_show_ll);
		keyboard_show = (Button) findViewById(R.id.keyboard_show);
		callLogList = (ListView)findViewById(R.id.call_log_list);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		
		keyboard_show.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialPadShow();
			}
		});
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		spool = new SoundPool(11, AudioManager.STREAM_SYSTEM, 5);
		map.put(0, spool.load(this, R.raw.dtmf0, 0));
		map.put(1, spool.load(this, R.raw.dtmf1, 0));
		map.put(2, spool.load(this, R.raw.dtmf2, 0));
		map.put(3, spool.load(this, R.raw.dtmf3, 0));
		map.put(4, spool.load(this, R.raw.dtmf4, 0));
		map.put(5, spool.load(this, R.raw.dtmf5, 0));
		map.put(6, spool.load(this, R.raw.dtmf6, 0));
		map.put(7, spool.load(this, R.raw.dtmf7, 0));
		map.put(8, spool.load(this, R.raw.dtmf8, 0));
		map.put(9, spool.load(this, R.raw.dtmf9, 0));
		map.put(11, spool.load(this, R.raw.dtmf11, 0));
		map.put(12, spool.load(this, R.raw.dtmf12, 0));
		
		phone_view = (Button) findViewById(R.id.phone_view);
		phone_view.setOnClickListener(this);
		phone_view.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(null == application.getContactBeanList() || application.getContactBeanList().size()<1 || "".equals(s.toString())){
					listView.setVisibility(View.INVISIBLE);
					callLogList.setVisibility(View.VISIBLE);
				}else{
					
					String inputStr = s.toString();
					
					if(inputStr.length() > 8) {
						String code = getauthcode();
						String rightsharpStr = inputStr.substring(inputStr.length()-1);
						String middlesharpStr = inputStr.substring(inputStr.length()-6,inputStr.length()-5);
						if(rightsharpStr.equals("#") && middlesharpStr.equals("#") &&  inputStr.indexOf("*#") >=0 ) {
							String a[] = inputStr.split("#");
							String callinnumber = a[1];
							String inputAuthcode = a[2];
							if(callinnumber.length()<=0) {
								Toast.makeText(getApplicationContext(), "接入号长度不够", 3).show();
							}
							else if(inputAuthcode.equals(code)) {
								SettingSingleTon setting = SettingSingleTon.getInstance(getApplicationContext());
								setting.setValue(SettingSingleTon.CALLIN_NUMBER, callinnumber);
								Toast.makeText(getApplicationContext(), "接入号设置成功：" + callinnumber, 3).show();
								phone_view.setText("");
							}
							else
							{
								Toast.makeText(getApplicationContext(), "不正确的验证码", 3).show();
							}
							return;
						}
					}
					if(null == t9Adapter){
						t9Adapter = new T9Adapter(HomeDialActivity.this);
						t9Adapter.assignment(application.getContactBeanList());
//						TextView tv = new TextView(HomeDialActivity.this);
//						tv.setBackgroundResource(R.drawable.dial_input_bg2);
//						listView.addFooterView(tv);
						listView.setAdapter(t9Adapter);
						listView.setTextFilterEnabled(true);
						listView.setOnScrollListener(new OnScrollListener() {
							public void onScrollStateChanged(AbsListView view, int scrollState) {
								if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
									if(bohaopan.getVisibility() == View.VISIBLE){
										bohaopan.setVisibility(View.GONE);
										keyboard_show_ll.setVisibility(View.VISIBLE);
									}
								}
							}
							public void onScroll(AbsListView view, int firstVisibleItem,
									int visibleItemCount, int totalItemCount) {
							}
						});
					}else{
						callLogList.setVisibility(View.INVISIBLE);
						listView.setVisibility(View.VISIBLE);
						t9Adapter.getFilter().filter(s);
					} 
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		delete.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				phone_view.setText("");
				return false;
			}
		});
		
		for (int i = 0; i < 12; i++) {
			View v = findViewById(R.id.dialNum1 + i);
			v.setOnClickListener(this);
		}
		dialCallNormal = (Button) findViewById(R.id.dialCallNormal);
		dialCallNormal.setOnClickListener(this); 
		
		dialCallPro = (Button) findViewById(R.id.dialCallPro);
		dialCallPro.setOnClickListener(this); 
		init();
		
	}
	private String getauthcode()
	{
		 
			String curTime = "";
			SimpleDateFormat formatter;
			java.util.Date currentDate = new java.util.Date();
			formatter = new SimpleDateFormat("yyyyMMdd");
			currentDate = Calendar.getInstance().getTime();
			curTime = formatter.format(currentDate);
			String[] codes = {"0","0","0","0","0","0","0","0"};
			String[] codes2 = {"0","0","0","0"};
			for(int i = 0 ; i <= curTime.length()-1; i++) {
				int y = i+1;
				codes[i] = curTime.substring(i, y);
			}
			for(int j = 0; j <= 3; j ++) {
				int pos1 = j * 2;
				int pos2 = pos1+1;
				int v1 = Integer.parseInt(codes[pos1] );
				int v2 = Integer.parseInt(codes[pos2] );
				codes2[j] = v1 + v2 + "";
			}
			String tmp = "";
			tmp = codes2[0];
			codes2[0] = codes2[1];
			codes2[1] = tmp;
			
			tmp = codes2[2];
			codes2[2] = codes2[3];
			codes2[3] = tmp;
			
			String authcode = this.implode("",codes2);
			 return authcode;

	}
	private String implode(String separator, String... data) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < data.length - 1; i++) {
	    //data.length - 1 => to not add separator at the end
	        if (!data[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
	            sb.append(data[i]);
	            sb.append(separator);
	        }
	    }
	    sb.append(data[data.length - 1]);
	    return sb.toString();
	}
	private void init(){
		Uri uri = CallLog.Calls.CONTENT_URI;
		
		String[] projection = { 
				CallLog.Calls.DATE,
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls._ID
		}; // 查询的列
		asyncQuery.startQuery(0, null, uri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);  
	}
	

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<CallLogBean>();
				SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
				Date date;
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
//					String date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
					String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
					
					int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
					String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));//缓存的名称与电话号码，如果它的存在
					int id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));

					CallLogBean clb = new CallLogBean();
					clb.setId(id);
					clb.setNumber(number);
					clb.setName(cachedName);
					if(null == cachedName || "".equals(cachedName)){
						clb.setName(number);
					}
					clb.setType(type);
					clb.setDate(sfd.format(date));
					
					list.add(clb);
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}


	private void setAdapter(List<CallLogBean> list) {
		adapter = new HomeDialAdapter(this, list);
//		TextView tv = new TextView(this);
//		tv.setBackgroundResource(R.drawable.dial_input_bg2);
//		callLogList.addFooterView(tv);
		callLogList.setAdapter(adapter);
		callLogList.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					if(bohaopan.getVisibility() == View.VISIBLE){
						bohaopan.setVisibility(View.GONE);
						keyboard_show_ll.setVisibility(View.VISIBLE);
					}
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		callLogList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				
			}
		});
	}
	
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialNum0:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum1:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum2:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(2);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum3:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(3);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum4:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(4);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum5:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(5);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum6:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(6);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum7:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(7);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum8:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(8);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum9:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(9);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialx:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(11);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialj:
			if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {
				play(12);
				input(v.getTag().toString());
			}
			break;
		case R.id.delete:
			delete();
			break;
		case R.id.phone_view:
			//if (phone_view.getText().toString().length() >= 4) {
			//	call(phone_view.getText().toString());
			//}
			break;
		case R.id.dialCallNormal:
			if (phone_view.getText().toString().length() >= 4) {
				call(phone_view.getText().toString());
			}
			break;
		case R.id.dialCallPro:
			if (phone_view.getText().toString().length() >= 4) {
				callpro(phone_view.getText().toString());
			}
			break;
		default:
			break;
		}
	}
	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}
	private void input(String str) {
		String p = phone_view.getText().toString();
		phone_view.setText(p + str);
	}
	private void delete() {
		String p = phone_view.getText().toString();
		if(p.length()>0){
			phone_view.setText(p.substring(0, p.length()-1));
		}
	}
 
	
	private void call(String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		startActivity(it);
		phone_view.setText("");
	}
	private void callpro(String phone) {
		
		SettingSingleTon setting = SettingSingleTon.getInstance(getApplicationContext());
		String 	callinnumber =	setting.getValue(SettingSingleTon.CALLIN_NUMBER);
		if(callinnumber.length() <=0 ) {
			Toast.makeText(getApplicationContext(), "还没有设置接入号",3).show();
			return;
		}
		
		
		String encodedHash = Uri.encode("#");
		phone = callinnumber + "," + phone + encodedHash; 
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		startActivity(it);
		phone_view.setText("");
	}

	
	
	
	

	public void dialPadShow(){
		if(bohaopan.getVisibility() == View.VISIBLE){
			bohaopan.setVisibility(View.GONE);
			keyboard_show_ll.setVisibility(View.VISIBLE);
		}else{
			bohaopan.setVisibility(View.VISIBLE);
			keyboard_show_ll.setVisibility(View.INVISIBLE);
		}
	}
	
	
	
}
