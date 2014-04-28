package xu.ye.view.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xu.ye.R;
import xu.ye.bean.SMSBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomeSMSAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<SMSBean> list;
	private Context context;
	private Date d;
	private SimpleDateFormat sdf;
	
	public HomeSMSAdapter(Context context) {     
		mInflater = LayoutInflater.from(context); 
		this.list = new ArrayList<SMSBean>();
		this.context=context;
		this.d=new Date();
		this.sdf=new SimpleDateFormat("MM/dd HH:mm");
	}   

	public void assignment(List<SMSBean> list){
		this.list = list;
	}
	public void add(SMSBean bean) {
		list.add(bean);
	}
	public void remove(int position){
		list.remove(position);
	}
	public int getCount() {  
		return list.size();     
	}            
	public SMSBean getItem(int position) {    
		return list.get(position);     
	}          
	public long getItemId(int position) {
		return 0;   
	}           
	public View getView(int position, View convertView, ViewGroup parent) {   

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_sms_list_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);  
			holder.count = (TextView) convertView.findViewById(R.id.count);  
			holder.date = (TextView) convertView.findViewById(R.id.date);  
			holder.content = (TextView) convertView.findViewById(R.id.content);  
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(list.get(position).getAddress());
		holder.count.setText("(" + list.get(position).getMsg_count() + ")");
		
		this.d.setTime(list.get(position).getDate());
		holder.date.setText(this.sdf.format(d));
		
		holder.content.setText(list.get(position).getMsg_snippet());

		convertView.setTag(holder);
		return convertView;
	}   

	public final class ViewHolder {   
		public TextView name;        
		public TextView count;        
		public TextView date;        
		public TextView content;        
	}
}
