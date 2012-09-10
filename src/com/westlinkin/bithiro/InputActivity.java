package com.westlinkin.bithiro;

import com.waps.AdView;
import com.westlinkin.bithiro.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InputActivity extends Activity {

	private  EditText et_input = null;
//	private Menu menu_show = null;
	private String address_info = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        et_input = (EditText)findViewById(R.id.et_input_address);
        
        // 互动广告调用方式
     	LinearLayout container = (LinearLayout) findViewById(R.id.AdLinearLayout_input);
     	new AdView(this, container).DisplayAd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_input, menu);
//    	menu_show = menu;
    	menu.getItem(1).setVisible(MyLocation.isMenuShow_1(this));
    	menu.getItem(0).setVisible(MyLocation.isMenuShow_0(this));
    	Log.v("isServiceRunning", String.valueOf(MyLocation.isMenuShow_1(this)));
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
         
        case R.id.menu_run: 
        	if(et_input.getText().toString().trim().equals("")){
        		return true;
        	}
        	//判断是否有网络连接
        	if (!(MyLocation.hasNetworkConnection(this))){
        		Toast.makeText(this, getResources()
        				.getString(R.string.networkError), Toast.LENGTH_SHORT).show();
        		return true;
        	}
        	address_info = et_input.getText().toString().trim();
        	Intent i_service = new Intent(InputActivity.this, MockLocation.class);
       	 	Bundle bundle = new Bundle();  
            bundle.putString("virtual_address", address_info);  
         //   Log.v(TAG, address_info);
            i_service.putExtras(bundle);  
              
            startService(i_service);   
           
            //启动提示
			NotificationManager mNotificationManager 
					= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification 
					= new Notification(R.drawable.location_place
							, getResources().getString(R.string.begin_loaction)
							, System.currentTimeMillis());
			PendingIntent contentIntent 
					= PendingIntent.getActivity(InputActivity.this, 0, getIntent(), 0);
			mNotification.setLatestEventInfo(InputActivity.this,
					getResources().getString(R.string.begin_loaction), address_info, contentIntent);
			mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_NO_CLEAR;
			mNotificationManager.notify(R.drawable.location_place, mNotification);
			
			//关闭整个activity
			Intent i_finish = new Intent(InputActivity.this,FinishActivity.class);
			i_finish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(i_finish);
		    overridePendingTransition(0, 0);
			this.finish();
			
        	return true;
        	
        case R.id.menu_stop: 
        	Intent i_service_stop = new Intent(InputActivity.this, MockLocation.class);
        	stopService(i_service_stop);
        	
        	//清除提示
        	NotificationManager mNotificationManager_stop 
					= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification_stop 
					= new Notification(R.drawable.location_place
							, getResources().getString(R.string.stop_loaction)
							, System.currentTimeMillis());
			PendingIntent contentIntent_stop 
					= PendingIntent.getActivity(InputActivity.this, 0, getIntent(), 0);
			mNotification_stop.setLatestEventInfo(InputActivity.this,
					getResources().getString(R.string.stop_loaction), address_info, contentIntent_stop);
			//mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification_stop.flags = Notification.FLAG_AUTO_CANCEL; 
			mNotificationManager_stop.notify(R.drawable.location_place, mNotification_stop);
			mNotificationManager_stop.cancel( R.drawable.location_place);
        	
		
        	//刷新界面
        	 Intent i_reflesh_stop = new Intent(InputActivity.this,InputActivity.class);
        	 i_reflesh_stop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     startActivity(i_reflesh_stop);
			 overridePendingTransition(0, 0);
        	return true;	
        	
      //  case R.id.menu_run:
        	// begin traval
        	
        //	return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    	
    }
    
}
