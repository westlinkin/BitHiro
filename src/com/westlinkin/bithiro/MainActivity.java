package com.westlinkin.bithiro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.waps.AdView;
import com.waps.AppConnect;
import com.westlinkin.bithiro.R;

public class MainActivity extends Activity {
	private final String DATABASE_PATH = "/data/data/com.westlinkin.bithiro/databases";
	private final String DATABASE_NAME = "bithiro";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
         // 不存在，写入数据库文件 
        final String databaseFile = DATABASE_PATH + "/" + DATABASE_NAME;
        File dir = new File(DATABASE_PATH);
         // 若没目录，创建这个目录
        if (!dir.exists()){
        	 try {
				 dir.mkdir();
			 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				 Log.v("Exception", "make dir");
			 }
         }

        if(!(new File(databaseFile).exists())){
        	
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						InputStream is = getResources().openRawResource(R.raw.bithiro);
						FileOutputStream fos = new FileOutputStream(databaseFile);
						byte[] buffer = new byte[8192];
						int count = 0;
						while((count = is.read(buffer)) > 0){
							fos.write(buffer, 0, count);
						}
						fos.close();
						is.close();
					}catch(Exception e){
						e.printStackTrace();
						Log.v("Exception", "write database");
					}
				}
			}).start();
         }
        
        String[] data = super.getResources().getStringArray(R.array.all_func);

        ListView lv_main = (ListView)findViewById(R.id.listView_main);
        
        ArrayAdapter<String> aaFunc = new ArrayAdapter<String>(this, R.layout.main_list_textview, data);
        
        lv_main.setAdapter(aaFunc);
        
        lv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					Intent i_myAdress = new Intent(MainActivity.this, MyAddressActivity.class);
					i_myAdress.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i_myAdress);
					break;
				case 1:
					Intent i_city = new Intent(MainActivity.this, CityActivity.class);
					i_city.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i_city);
					break;
					
				case 2:
					Intent i_input = new Intent(MainActivity.this, InputActivity.class);
					i_input.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i_input);
					break;
					
				case 3:
					Intent i_map = new Intent(MainActivity.this, MyMapActivity.class);
					i_map.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i_map);
					
					break;
					
				case 4: //about activity
					Intent i_about = new Intent(MainActivity.this, AboutActivity.class);
					i_about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i_about);
					break;
				
				}
			}
		});
        // 初始化统计器，并通过代码设置WAPS_ID, WAPS_PID
     	AppConnect.getInstance("19826d49f3d7a9769f1ed2609f30c9d9", 
     									/*WAPS_PID 针对每个市场不同，做不同设置*/"QQ", this);
     	AppConnect.getInstance(this).setAdViewClassName("com.westlinkin.bithiro.MyAdView");
     	// 互动广告调用方式
     	LinearLayout container = (LinearLayout) findViewById(R.id.AdLinearLayout_main);
     	new AdView(this, container).DisplayAd();
    	
    }
	
	
    @Override
    protected void onResume(){
   // 	if(count != 1){
    		 boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver()
											, LocationManager.GPS_PROVIDER );
			//boolean networkEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver()
			//								, LocationManager.NETWORK_PROVIDER );
			/*if (gpsEnabled || networkEnabled){
				LinearLayout dialogView = (LinearLayout)getLayoutInflater().inflate(R.layout.setting_warning_dialog, null);
				TextView tv_tips = (TextView)dialogView.findViewById(R.id.tv_setting_warning);
				
				if (gpsEnabled && networkEnabled){
					tv_tips.setText(MainActivity.this.getResources().getString(R.string.warning_bothOn));
				}
				else if(gpsEnabled && (!networkEnabled)){
					tv_tips.setText(MainActivity.this.getResources().getString(R.string.warning_gpsOn));
				}
				else if((!gpsEnabled) && networkEnabled){
					tv_tips.setText(MainActivity.this.getResources().getString(R.string.warning_networkOn));
				}
				new AlertDialog.Builder(MainActivity.this).setView(dialogView)
				.setTitle(MainActivity.this.getResources().getString(R.string.tips))
				.setPositiveButton(R.string.possitiveBtnSettings, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						
					}
				})
				.setNegativeButton(R.string.negBtnSettings, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						MainActivity.this.finish();
					}
				})
				.show();
			}*/
			if(gpsEnabled){
				Toast.makeText(this, getResources().getString(R.string.warning_gpsOn), Toast.LENGTH_LONG).show();
			}
			
	    	 String mockLocationSetting = Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
	
	         Log.v("1111",mockLocationSetting);
	         if(mockLocationSetting.equals("0")){
	        	 Log.v("dfajfkd","dffd");
	         	//Settings.Secure.putString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, "1");
	         	LinearLayout dialogView = (LinearLayout)getLayoutInflater().inflate(R.layout.setting_wrong_dialog, null);
	         	new AlertDialog.Builder(MainActivity.this).setView(dialogView)
				.setTitle(MainActivity.this.getResources().getString(R.string.tips))
	     		.setPositiveButton(R.string.possitiveBtnSettings, new OnClickListener() {
	 				
	 				@Override
	 				public void onClick(DialogInterface dialog, int which) {
	 					// TODO Auto-generated method stub
	 					Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
	 					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 					startActivity(i);
	 					
	 				}
	 			})
	 			.setNegativeButton(R.string.negBtnSettings, new OnClickListener() {
	 				
	 				@Override
	 				public void onClick(DialogInterface dialog, int which) {
	 					// TODO Auto-generated method stub
	 					MainActivity.this.finish();
	 				}
	 			})
	     		.show();
	         //	mockLocationSetting = Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
	         	
	         }
    	//}
    	
    	super.onResume();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }
    
    @Override
	protected void onDestroy() {
		AppConnect.getInstance(this).finalize();
		super.onDestroy();
	}
}
