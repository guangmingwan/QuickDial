package com.adouming.view.sms;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adouming.quickdial.R;
import com.adouming.bean.ContactBean;
import com.adouming.bean.MessageBean;
import com.adouming.uitl.BaseIntentUtil;
import com.adouming.view.HomeContactActivity;
import com.adouming.view.HomeDialActivity;
import com.adouming.view.adapter.MessageBoxListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageBoxList extends Activity {

	private ListView talkView;
	private List<MessageBean> list = null;
	private Button fasong;
	private Button btn_return;
	private Button btn_call;
	private EditText neirong;
	private SimpleDateFormat sdf;
	private AsyncQueryHandler asyncQuery;
	private String address;
	private Intent intent = new Intent("com.adouming.refreshcalllog.RECEIVER");
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_messageboxlist);

		btn_return=(Button) findViewById(R.id.btn_return);
		btn_call=(Button) findViewById(R.id.btn_call);
		fasong=(Button) findViewById(R.id.fasong);
		neirong=(EditText) findViewById(R.id.neirong);

		String thread=getIntent().getStringExtra("threadId");
		address=getIntent().getStringExtra("phoneNumber");
		TextView tv=(TextView) findViewById(R.id.topbar_title);
		tv.setText(getPersonName(address));
		
		sdf= new SimpleDateFormat("MM-dd HH:mm");
		
		init(thread);

		btn_return.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageBoxList.this.setResult(RESULT_OK);
				MessageBoxList.this.finish();
			}
		});
		btn_call.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				showContactDialog(lianxiren1, address);
			}
		});
		fasong.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String nei=neirong.getText().toString();
				ContentValues values = new ContentValues();
				values.put("address", address);
				values.put("body", nei);
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				Toast.makeText(MessageBoxList.this, nei, Toast.LENGTH_SHORT).show();
			}
		});
	}
	private String[] lianxiren1 = new String[] { "使用“快拨”通话", "直接拨打电话" };
	//群组联系人弹出页
	private void showContactDialog(final String[] arg ,final String phoneNum){
		new AlertDialog.Builder(this).setTitle(address).setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){

				Uri uri = null;
				Context ctx = getApplicationContext();
				switch(which){
				case 0://快拨模式打电话
					String toProPhone = address;
					intent.putExtra(HomeDialActivity.BR_ACION, HomeDialActivity.BR_DIAL_PRO);
    		        intent.putExtra(HomeDialActivity.BR_PAYLOAD, toProPhone);
    		        
    		        ctx.sendBroadcast(intent);
    		        
					break;
				case 1://直接打电话
						String toNormalPhone = address;
						intent.putExtra(HomeDialActivity.BR_ACION, HomeDialActivity.BR_DIAL_NORMAL);
	    		        intent.putExtra(HomeDialActivity.BR_PAYLOAD, toNormalPhone); 
	    		        ctx.sendBroadcast(intent);
						break;
 
				}
			}
		}).show();
	}
	private void init(String thread){
		
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		
		talkView = (ListView) findViewById(R.id.list);
		list = new ArrayList<MessageBean>();
		
		Uri uri = Uri.parse("content://sms"); 
		String[] projection = new String[] {
				"date",
				"address",
				"person",
				"body",
				"type"
		};// 查询的列
		asyncQuery.startQuery(0, null, uri, projection, "thread_id = " + thread, null,
				"date asc");
	}
	
	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String date = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
					if(cursor.getInt(cursor.getColumnIndex("type")) == 1){ 
						MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")), 
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_he_item);
						list.add(d);
					}else{ 
						MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_me_item);
						list.add(d);
					}
				}
				if (list.size() > 0) {
					talkView.setAdapter(new MessageBoxListAdapter(MessageBoxList.this, list));
					talkView.setDivider(null);
					talkView.setSelection(list.size());
				}else{
					Toast.makeText(MessageBoxList.this, "没有短信进行操作", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	public String getPersonName(String number) {   
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, };   
		Cursor cursor = this.getContentResolver().query(   
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
				projection,    
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",  
				null,        
				null);  
		if( cursor == null ) {   
			return number;   
		}   
		String name = number;
		for( int i = 0; i < cursor.getCount(); i++ ) {   
			cursor.moveToPosition(i);   
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));   
		}
		cursor.close();
		return name;   
	} 
	
}
