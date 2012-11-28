package com.westlinkin.bithiro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MyAddressActivity extends Activity {

	private int delete_pos = -1;
	private ListView lv_address = null;
//	private Menu menu_show = null;
	private String address_info = "";
	private final static String TAG = "MyAddressActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        
        lv_address = (ListView)findViewById(R.id.listView_my_address);
                
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        //载入保存的虚拟地址文件
        Log.v("098",String.valueOf(fileIsExists()));
        String addressData = "";
        if(fileIsExists()){
        	addressData = readFileData("address_data.txt");
        }
        Log.v("11<",addressData);
        String[] from = new String[]{"addName","addInfo"};
        int[] to = new int[]{R.id.addName,R.id.addInfo};
        List<Map<String,String>> data = new ArrayList<Map<String,String>>();//消息数据
         	
        if( addressData.trim() != "" && addressData != null ){
	        String[] addressDataInfo = addressData.split(";");
	         //Log.v("666","999");                     
	        for(int i = 0; i < addressDataInfo.length; i++){
	        	Map<String, String> item = new HashMap<String, String>();
	            item.put("addName", addressDataInfo[i].split(",")[0].toString());
	            item.put("addInfo", addressDataInfo[i].split(",")[1].toString());
	            data.add(item);
	            
	            Log.v("addName",addressDataInfo[i].split(",")[0].toString());
	         }
	                           	
	
			 SimpleAdapter saAddName= new SimpleAdapter(this, data, R.layout.my_address_list_textview, from, to);

			 lv_address.setAdapter(saAddName);
			 
        }
   	
		 lv_address.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		 lv_address.setMultiChoiceModeListener(new MultiChoiceModeListener() {

		     @Override
		     public void onItemCheckedStateChanged(ActionMode mode, int position,
		                                           long id, boolean checked) {
		         // Here you can do something when items are selected/de-selected,
		         // such as update the title in the CAB
		    	 
		    	 delete_pos = position;
		    			    	 
		    	 String addressData = readFileData("address_data.txt");
	             String[] tokens = addressData.split(";");
	             String title = tokens[delete_pos].split(",")[0];
	             address_info = tokens[delete_pos].split(",")[1];
		    	 mode.setTitle(getResources().getString(R.string.pick) + " " + title);
		    	 
		     }

		     @Override
		     public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		         // Respond to clicks on the actions in the CAB
		         switch (item.getItemId()) {
		             case R.id.menu_delete:
		            	 String addressData = readFileData("address_data.txt");
		            	 Log.v("000",addressData);
			             String[] tokens = addressData.split(";");
			             addressData = "";
			             if(delete_pos > -1){
			 	            tokens[delete_pos] = "";
			 	            for(int i = 0; i < tokens.length; i++){
			 	            	if(tokens[i].length() > 0){
			 	            		addressData += (tokens[i].toString() + ";");
			 	            	}
			 	            }
			 	            if(addressData.trim() == "" ){
			 	            	//删除文件
			 	            	deleteFile("address_data.txt");
			 	            }
			 	            else{
			 	            	//写入
			 	            	 writeData("address_data.txt", addressData);
			 	            }
			            	 Log.v("001",addressData);
			 				//保存之后，要重新刷一下当前活动
			 				Intent i_reflesh = new Intent(MyAddressActivity.this,MyAddressActivity.class);
			 			    i_reflesh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 			    startActivity(i_reflesh);
			 			    overridePendingTransition(0, 0);
			             }
		                // deleteSelectedItems();
		                 mode.finish(); // Action picked, so close the CAB
		                 return true;
		             
		             case R.id.menu_run: //启动服务
		            	 Log.v(TAG, "1");
		             	//判断是否有网络连接
		             	if (!(MyLocation.hasNetworkConnection(MyAddressActivity.this))){
		             		Toast.makeText(MyAddressActivity.this, getResources()
		             				.getString(R.string.networkError), Toast.LENGTH_SHORT).show();
		             		mode.finish();
		             		return true;
		             	}
		            	 Intent i_service = new Intent(MyAddressActivity.this, MockLocation.class);
		            	 Bundle bundle = new Bundle();  
		                 bundle.putString("virtual_address", address_info);  
		                 Log.v(TAG, address_info);
		                 i_service.putExtras(bundle);  
		                   
		                 startService(i_service);   
		                 mode.finish();
		                
		                 //启动提示
		 				NotificationManager mNotificationManager 
		 						= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		 				Notification mNotification 
		 						= new Notification(R.drawable.location_place
		 								, getResources().getString(R.string.begin_loaction)
		 								, System.currentTimeMillis());
		 				PendingIntent contentIntent 
		 						= PendingIntent.getActivity(MyAddressActivity.this, 0, getIntent(), 0);
		 				mNotification.setLatestEventInfo(MyAddressActivity.this,
		 						getResources().getString(R.string.begin_loaction), address_info, contentIntent);
		 				mNotification.defaults = Notification.DEFAULT_SOUND;
		 				mNotification.flags = Notification.FLAG_NO_CLEAR;
		 				mNotificationManager.notify(R.drawable.location_place, mNotification);
		 				
		 				//关闭整个activity
		 				Intent i_finish = new Intent(MyAddressActivity.this,FinishActivity.class);
		 				i_finish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 			    startActivity(i_finish);
		 			    overridePendingTransition(0, 0);
		 				MyAddressActivity.this.finish();
		            	 return true;
		             default:
		                 return false;
		         }
		     }

		     @Override
		     public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		         // Inflate the menu for the CAB
		         MenuInflater inflater = mode.getMenuInflater();
		         inflater.inflate(R.menu.contextual_action_mode_my_adress, menu);
		         menu.getItem(1).setVisible(MyLocation.isMenuShow_0(MyAddressActivity.this));
		         return true;
		     }

		     @Override
		     public void onDestroyActionMode(ActionMode mode) {
		         // Here you can make any necessary updates to the activity when
		         // the CAB is removed. By default, selected items are deselected/unchecked.
		     }

		     @Override
		     public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		         // Here you can perform updates to the CAB due to
		         // an invalidate() request
		         return false;
		     }
		 });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_my_adress, menu);
//        menu_show = menu;
        menu.getItem(1).setVisible(MyLocation.isMenuShow_1(this));
        return true;
    }
    
   
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
         
        case R.id.menu_add: // 对话框  名称 和 地址
        	final LinearLayout dialogView = (LinearLayout)getLayoutInflater().inflate(R.layout.my_address_dialog, null);
        	
        	new AlertDialog.Builder(MyAddressActivity.this).setTitle(R.string.dialog_title)
        		.setView(dialogView)
        		.setPositiveButton(R.string.positiveBtn, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText et_addName = (EditText)dialogView.findViewById(R.id.et_addName);
						EditText et_addInfo = (EditText)dialogView.findViewById(R.id.et_addInfo);
						if (et_addName.getText().toString().trim().equals("") ||
								et_addInfo.getText().toString().trim().equals("")){
							Toast.makeText(MyAddressActivity.this, R.string.no_context_warning, Toast.LENGTH_SHORT).show();
							return;
						}
						 String addressData = readFileData("address_data.txt") 
								 				+ et_addName.getText().toString() + "," 
								 				+ et_addInfo.getText().toString() + ";";
						 
						 writeData("address_data.txt", addressData);
						//保存之后，要重新刷一下当前活动
						 Intent i_reflesh = new Intent(MyAddressActivity.this,MyAddressActivity.class);
				        i_reflesh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				        startActivity(i_reflesh);
		 			    overridePendingTransition(0, 0);
						
					}
				}).setNegativeButton(R.string.negBtn, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
        	
        	
//        	Intent i_reflesh = new Intent(MyAddressActivity.this,MyAddressActivity.class);
//        	i_reflesh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        	startActivity(i_reflesh);
        	return true;
        	
      //  case R.id.menu_run:
        	// begin traval
        	
        //	return true;
        case R.id.menu_stop: // 点击listView时设为true
        	Intent i_service = new Intent(MyAddressActivity.this, MockLocation.class);
        	stopService(i_service);
        	
        	//清除提示
        	NotificationManager mNotificationManager 
					= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification 
					= new Notification(R.drawable.location_place
							, getResources().getString(R.string.stop_loaction)
							, System.currentTimeMillis());
			PendingIntent contentIntent 
					= PendingIntent.getActivity(MyAddressActivity.this, 0, getIntent(), 0);
			mNotification.setLatestEventInfo(MyAddressActivity.this,
					getResources().getString(R.string.stop_loaction), address_info, contentIntent);
			//mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_AUTO_CANCEL; 
			mNotificationManager.notify(R.drawable.location_place, mNotification);
        	mNotificationManager.cancel(R.drawable.location_place);
        	
		
        	//刷新界面
        	 Intent i_reflesh = new Intent(MyAddressActivity.this,MyAddressActivity.class);
		     i_reflesh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     startActivity(i_reflesh);
			 overridePendingTransition(0, 0);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    	
    }

    public String readFileData(String fileName){  
        String result="";  
        try{  
            FileInputStream fin = openFileInput(fileName);//获得FileInputStream对象  
            int length = fin.available();//获取文件长度  
            byte [] buffer = new byte[length];//创建byte数组用于读入数据  
            fin.read(buffer);//将文件内容读入到byte数组中                    
            result = EncodingUtils.getString(buffer, "gbk");//将byte数组转换成指定格式的字符串  
            fin.close();                    //关闭文件输入流  
        }  
        catch(Exception e){  
            e.printStackTrace();//捕获异常并打印  
        }  
        return result;//返回读到的数据字符串  
       }     
    
    public void writeData(String filename,String msg){  

    	try {  
            FileOutputStream fos=this.openFileOutput(filename, MODE_PRIVATE);  
         //   byte[] buf=msg.getBytes();  
            byte[] buf = EncodingUtils.getBytes(msg, "gbk");
            fos.write(buf);  
            fos.close();  
        } catch (FileNotFoundException e) {  
          
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
    
    public boolean fileIsExists(){
        try{
        	//
                File f=new File("/data/data/com.westlinkin.bithiro/files/address_data.txt");
                if(!f.exists()){
                        return false;
                }
                
        }catch (Exception e) {
                // TODO: handle exception
                return false;
        }
        return true;
}
    
    public void deleteData(String filename){
    	File file = new File(filename);
    	file.delete();
    }
    
}
