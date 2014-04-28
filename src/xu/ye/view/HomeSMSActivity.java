package xu.ye.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xu.ye.R;
import xu.ye.bean.SMSBean;
import xu.ye.uitl.BaseIntentUtil;
import xu.ye.uitl.RexseeSMS;
import xu.ye.view.adapter.HomeSMSAdapter;
import xu.ye.view.sms.MessageBoxList;
import xu.ye.view.sms.NewSMSActivity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class HomeSMSActivity extends Activity {

	private ListView listView;
	private HomeSMSAdapter adapter;
	private RexseeSMS rsms;
	private Button newSms;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		init();
		
	}

	public void init(){

		setContentView(R.layout.home_sms_page);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		listView = (ListView) findViewById(R.id.list);
		adapter = new HomeSMSAdapter(HomeSMSActivity.this);
		
		rsms = new RexseeSMS(HomeSMSActivity.this);
		List<SMSBean> list_mmt = rsms.getThreadsNum(rsms.getThreads(0));
		adapter.assignment(list_mmt);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> map = new HashMap<String, String>();
				SMSBean sb = adapter.getItem(position);
				map.put("phoneNumber", sb.getAddress());
				map.put("threadId", sb.getThread_id());
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, MessageBoxList.class, map);
			}
		});
		
		newSms = (Button) findViewById(R.id.newSms);
		newSms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, NewSMSActivity.class, null);
			}
		});
		
		
	}






}
