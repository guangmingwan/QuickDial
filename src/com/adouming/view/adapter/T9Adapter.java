package com.adouming.view.adapter;

import java.util.ArrayList;
import java.util.List;

import com.adouming.quickdial.R;
import com.adouming.bean.CallLogBean;
import com.adouming.bean.ContactBean;
import com.adouming.view.HomeDialActivity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class T9Adapter extends BaseAdapter implements Filterable {

	private LayoutInflater mInflater;
	private List<ContactBean> list;
	private List<ContactBean> allContactList;
	private Context context;
	private String filterNum;
	private Intent intent = new Intent("com.adouming.refreshcalllog.RECEIVER");
	public T9Adapter(Context context) {     
		mInflater = LayoutInflater.from(context); 
		this.list = new ArrayList<ContactBean>();
		this.context=context;
	}   

	public void assignment(List<ContactBean> list){
		this.allContactList = list;
		this.list = this.allContactList;
	}
	public void add(ContactBean bean) {
		list.add(bean);
	}
	public void remove(int position){
		list.remove(position);
	}
	public int getCount() {  
		return list != null ? list.size() : -1;     
	}            
	public ContactBean getItem(int position) {    
		return list.get(position);     
	}          
	public long getItemId(int position) {
		return 0;   
	}           
	 
	 public static String implode(String separator, String[] data) {
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
	public View getView(int position, View convertView, ViewGroup parent) {   

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_t9_list_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);  
			holder.pinyin = (TextView) convertView.findViewById(R.id.pinyin);  
			holder.number = (TextView) convertView.findViewById(R.id.number);  
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(list.get(position).getDisplayName());
		String formattedNumber = list.get(position).getPinyin();
		String[] phones = (String[])list.get(position).getPhoneNum().toArray(new String[list.get(position).getPhoneNum().size()]);
		String impPhoneNum = implode(",", phones);
		if (null == filterNum || "".equals(filterNum)) {
//			holder.pinyin.setVisibility(View.INVISIBLE);
			
			holder.number.setText(impPhoneNum);
		} else {
//			holder.pinyin.setVisibility(View.VISIBLE);
			holder.number.setText(Html.fromHtml(impPhoneNum.replace(filterNum, "<font color='#6c8a20'>" + filterNum + "</font>")));
			if (!TextUtils.isEmpty(filterNum)) {
				for (int i = 0; i < filterNum.length(); i++) {
					char c = filterNum.charAt(i);
					if (TextUtils.isDigitsOnly(String.valueOf(c))) {
						char[] zms = digit2Char(Integer.parseInt(c + ""));
						if (zms != null) {
							for (char c1 : zms) {
								formattedNumber = formattedNumber.replaceAll(String.valueOf(c1).toUpperCase(), "%%" + String.valueOf(c1).toUpperCase() + "@@");
							}
							formattedNumber = formattedNumber.replaceAll("%%", "<font color='#6c8a20'>");
							formattedNumber = formattedNumber.replaceAll("@@", "</font>");
						}
					}
				}
				holder.pinyin.setText(Html.fromHtml(formattedNumber));
			}
		}

		convertView.setTag(holder);
		ContactBean cb = (ContactBean)list.get(position);
		String[] PhoneNums = (String[])cb.getPhoneNum().toArray(new String[cb.getPhoneNum().size()]);
		for (String phoneNum : PhoneNums) {
			addViewListener(convertView , phoneNum, position);
		}
		
		return convertView;
	}   
	private void addViewListener(View view, final String phoneNum, final int position){
		view.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {

				//发送Action广播  
		        intent.putExtra(HomeDialActivity.BR_ACION, HomeDialActivity.BR_REDIAL_PREDIAL);
		        intent.putExtra(HomeDialActivity.BR_PAYLOAD, phoneNum );
		        
		        context.sendBroadcast(intent); 
		        
			}
		});
	}
	public final class ViewHolder {   
		public TextView name;        
		public TextView pinyin;        
		public TextView number;        
	}

	public char[] digit2Char(int digit) {
		char[] cs = null;
		switch (digit) {
		case 0:
			cs = new char[] {};
			break;
		case 1:
			break;
		case 2:
			cs = new char[] { 'a', 'b', 'c' };
			break;
		case 3:
			cs = new char[] { 'd', 'e', 'f' };
			break;
		case 4:
			cs = new char[] { 'g', 'h', 'i' };
			break;
		case 5:
			cs = new char[] { 'j', 'k', 'l' };
			break;
		case 6:
			cs = new char[] { 'm', 'n', 'o' };
			break;
		case 7:
			cs = new char[] { 'p', 'q', 'r', 's' };
			break;
		case 8:
			cs = new char[] { 't', 'u', 'v' };
			break;
		case 9:
			cs = new char[] { 'w', 'x', 'y', 'z' };
			break;
		}
		return cs;
	}

	public Filter getFilter() {
		Filter filter = new Filter() {
			protected void publishResults(CharSequence constraint, FilterResults results) {
				list = (ArrayList<ContactBean>) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
			protected FilterResults performFiltering(CharSequence s) {
				String str = s.toString();
				filterNum = str;
				FilterResults results = new FilterResults();
				ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
				if (allContactList != null && allContactList.size() != 0) {
					for(ContactBean cb : allContactList){
						String[] phones = (String[])cb.getPhoneNum().toArray(new String[cb.getPhoneNum().size()]);
						for (String phonenumber : phones) {
							if(cb.getFormattedNumber().indexOf(str)>=0 || phonenumber.indexOf(str)>-1){
								contactList.add(cb);
							}
						}
						
					}
				}
				results.values = contactList;
				results.count = contactList.size();
				return results;
			}
		};
		return filter;
	}
}
