package xu.ye.view;
import java.util.ArrayList;  
import java.util.HashMap;  

import xu.ye.R;
  
import android.app.AlertDialog;  
import android.content.Context;  
import android.content.DialogInterface;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.View.OnClickListener;  
import android.widget.BaseAdapter;  
import android.widget.Button;  
import android.widget.ImageView;  
import android.widget.TextView;  
public class HomeSettingAdapter extends BaseAdapter {  
    
  private ArrayList<HashMap<String, Object>> data;  
  /** 
   * LayoutInflater 类是代码实现中获取布局文件的主要形式 
   *LayoutInflater layoutInflater = LayoutInflater.from(context); 
   *View convertView = layoutInflater.inflate(); 
   *LayoutInflater的使用,在实际开发种LayoutInflater这个类还是非常有用的,它的作用类似于 findViewById(), 
  不同点是LayoutInflater是用来找layout下xml布局文件，并且实例化！ 
  而findViewById()是找具体xml下的具体 widget控件(如:Button,TextView等)。 
   */  
  private LayoutInflater layoutInflater;  
  private Context context;  
    
    
  public HomeSettingAdapter(Context context,ArrayList<HashMap<String, Object>> data) {  
        
      this.context = context;  
      this.data = data;  
      this.layoutInflater = LayoutInflater.from(context);  
  }  
 public void setData( ArrayList<HashMap<String, Object>> data) {
	 this.data = data;
	 this.notifyDataSetChanged();
 }
  /** 
   *获取列数  
   */  
  public int getCount() {  
      return data.size();  
  }  
  /** 
   *获取某一位置的数据  
   */  
  public Object getItem(int position) {  
      return data.get(position);  
  }  
  /** 
   *获取唯一标识 
   */  
  public long getItemId(int position) {  
      return position;  
  }  

  /** 
   * android绘制每一列的时候，都会调用这个方法 
   */  
  public View getView(int position, View convertView, ViewGroup parent) {  
	  HomeSettingItem zuJian = null;  
      if(convertView==null){  
          zuJian = new HomeSettingItem();  
          // 获取组件布局  
          convertView = layoutInflater.inflate(R.layout.home_setting_item, null);  
          zuJian.imageView = (ImageView) convertView.findViewById(R.id.image);  
          zuJian.titleView = (TextView) convertView.findViewById(R.id.title);  
          zuJian.infoView = (TextView) convertView.findViewById(R.id.info);  
          //zuJian.button = (Button) convertView.findViewById(R.id.view_btn);  
          // 这里要注意，是使用的tag来存储数据的。  
          convertView.setTag(zuJian);  
      }  
      else {  
          zuJian = (HomeSettingItem) convertView.getTag();  
      }  
      // 绑定数据、以及事件触发  
      switch(position)
      {
      case 0:
    	  zuJian.imageView.setBackgroundResource(R.drawable.answernum); 
    	  break;
      case 1:
    	  zuJian.imageView.setBackgroundResource(R.drawable.answerphone); 
    	  break;
      case 2:
    	  zuJian.imageView.setBackgroundResource(R.drawable.account_query); 
    	  break;
      }
       
      zuJian.titleView.setText((String)data.get(position).get("title"));  
      zuJian.infoView.setText((String)data.get(position).get("info"));  
//      zuJian.button.setOnClickListener(new OnClickListener(){  
//
//          public void onClick(View v) {  
//              showInfo();  
//          }  
//            
//      });  
      return convertView;  
  }  

  /** 
   *当用户点击按钮时触发的事件，会弹出一个确认对话框 
   */  
   public void showInfo(){    

               new AlertDialog.Builder(context)    

               .setTitle("我的listview")    

              .setMessage("介绍...")    

              .setPositiveButton("确定", new DialogInterface.OnClickListener() {    

              public void onClick(DialogInterface dialog, int which) {    

                   }    

               })    

             .show();    

                   

          }    

}
