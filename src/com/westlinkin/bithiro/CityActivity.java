package com.westlinkin.bithiro;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class CityActivity extends Activity {

	private AutoCompleteTextView input_city = null;
//	private Menu menu_show = null;
	private String address_info = "";
	private final static String TAG = "CityActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        //使用数据库存储城市名 
	    DBService dbService = new DBService(this);
	    Cursor cursor = dbService.getCurcor("select * from cities;", null);
        String[] cityItems = new String[cursor.getCount()];
        
        Log.v("111",String.valueOf(cityItems.length));
        
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
        	cityItems[i] = cursor.getString(0);
            cursor.moveToNext();
           // Log.v("211", cityItems[i]);
        }
        cursor.close();
        dbService.close();
        input_city = (AutoCompleteTextView)findViewById(R.id.tv_cities_list);
        
        input_city.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,cityItems));
        Log.v(TAG, address_info);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater menuInflater = getMenuInflater();
    	menuInflater.inflate(R.menu.action_bar_input, menu);
    	menu.getItem(1).setVisible(MyLocation.isMenuShow_1(this));
    	menu.getItem(0).setVisible(MyLocation.isMenuShow_0(this));
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
        	//判断是否有网络连接
        	if (!(MyLocation.hasNetworkConnection(this))){
        		Toast.makeText(this, getResources()
        				.getString(R.string.networkError), Toast.LENGTH_SHORT).show();
        		return true;
        	}
        	if(input_city.getText().toString().trim().equals("")){
        		return true;
        	}
        	address_info = input_city.getText().toString().trim();
        	Intent i_service = new Intent(CityActivity.this, MockLocation.class);
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
					= PendingIntent.getActivity(CityActivity.this, 0, getIntent(), 0);
			mNotification.setLatestEventInfo(CityActivity.this,
					getResources().getString(R.string.begin_loaction), address_info, contentIntent);
			mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_NO_CLEAR;
			mNotificationManager.notify(R.drawable.location_place, mNotification);
			
			//关闭整个activity
			Intent i_finish = new Intent(CityActivity.this,FinishActivity.class);
			i_finish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(i_finish);
		    overridePendingTransition(0, 0);
			this.finish();
        	return true;
        	
        case R.id.menu_stop: 
        	Intent i_service_stop = new Intent(CityActivity.this, MockLocation.class);
        	stopService(i_service_stop);
        	
        	//清除提示
        	NotificationManager mNotificationManager_stop 
					= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification_stop 
					= new Notification(R.drawable.location_place
							, getResources().getString(R.string.stop_loaction)
							, System.currentTimeMillis());
			PendingIntent contentIntent_stop 
					= PendingIntent.getActivity(CityActivity.this, 0, getIntent(), 0);
			mNotification_stop.setLatestEventInfo(CityActivity.this,
					getResources().getString(R.string.stop_loaction), address_info, contentIntent_stop);
			//mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification_stop.flags = Notification.FLAG_AUTO_CANCEL; 
			mNotificationManager_stop.notify(R.drawable.location_place, mNotification_stop);
			mNotificationManager_stop.cancel( R.drawable.location_place);
        	
		
        	//刷新界面
        	 Intent i_reflesh_stop = new Intent(CityActivity.this,CityActivity.class);
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
