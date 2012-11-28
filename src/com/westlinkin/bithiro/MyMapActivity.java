package com.westlinkin.bithiro;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.westlinkin.bithiro.FzLocationManager.LocationCallBack;


@SuppressLint("HandlerLeak")
public class MyMapActivity extends MapActivity implements LocationCallBack {

    private final String TAG = "MyMapActivity";
	// 地图显示控制相关变量定义	
	private MapView mapView;
    private MapController mMapCtrl;
    private View popView;
    private Drawable myLocationDrawable;
    private Drawable mylongPressDrawable;
    private FzLocationManager fzLocation;
    private MyItemizedOverlay myLocationOverlay;
    private MyItemizedOverlay mLongPressOverlay;
    private List<Overlay> mapOverlays;
    private OverlayItem overlayitem = null;
    public GeoPoint locPoint;
    
    public final int MSG_VIEW_LONGPRESS = 10001;
    public final int MSG_VIEW_ADDRESSNAME = 10002;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
         
        myLocationDrawable = getResources().getDrawable(R.drawable.point_where);
        mylongPressDrawable = getResources().getDrawable(R.drawable.point_start);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        initPopView();
        mMapCtrl = mapView.getController();
        myLocationOverlay = new MyItemizedOverlay(myLocationDrawable,this, mapView, popView, mMapCtrl);
        mLongPressOverlay = new MyItemizedOverlay(mylongPressDrawable,this, mapView, popView, mMapCtrl);
        mapOverlays = mapView.getOverlays();
        mapOverlays.add(new LongPressOverlay(this, mapView, mHandler, mMapCtrl));
        
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
        	 GeoPoint cityLocPoint = new GeoPoint((int)(location.getLatitude() * 1E6)
        			 							, (int)(location.getLongitude() * 1E6));
	        mMapCtrl.animateTo(cityLocPoint);
	        mMapCtrl.setZoom(10);
         }
        else{
	        GeoPoint cityLocPoint = new GeoPoint(39909230, 116397428);
	        mMapCtrl.animateTo(cityLocPoint);
	        mMapCtrl.setZoom(10);
         }
        FzLocationManager.init(MyMapActivity.this.getApplicationContext() , MyMapActivity.this);
        fzLocation = FzLocationManager.getInstance();
    
       
    }
    
    private void initPopView(){
        if(null == popView){
            popView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
            mapView.addView(popView, new MapView.LayoutParams(
                    MapView.LayoutParams.WRAP_CONTENT,
                    MapView.LayoutParams.WRAP_CONTENT, null,
                    MapView.LayoutParams.BOTTOM_CENTER));
            popView.setVisibility(View.GONE);
        }
       
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_map, menu);

    	 menu.getItem(0).setVisible(MyLocation.isMenuShow_1(this));
    	 Log.v("isServiceRunning", String.valueOf(MyLocation.isMenuShow_1(this)));
        return true;
    }
    @Override
    public void onCurrentLocation(Location location) {
        Log.d(TAG, "onCurrentLocationy");
        GeoPoint point = new GeoPoint(
                (int) (location.getLatitude() * 1E6),
                (int) (location.getLongitude() * 1E6));
        overlayitem = new OverlayItem(point, getResources().getString(R.string.MyLocation), "");
        mMapCtrl.setZoom(16);
        if(myLocationOverlay.size() > 0){
            myLocationOverlay.removeOverlay(0);
        }
        myLocationOverlay.addOverlay(overlayitem);
        mapOverlays.add(myLocationOverlay);
        mMapCtrl.animateTo(point);
    }
    
    private String getLocationAddress(GeoPoint point){
        String add = "";
        Geocoder geoCoder = new Geocoder(getBaseContext(),
                Locale.getDefault());
        try {
        	double lat = point.getLatitudeE6() / 1E6;
        	double log = point.getLongitudeE6() / 1E6;
        	Log.v("lat", String.valueOf(lat));
//        	Log.v("log", String.valueOf(log));
//        	DecimalFormat df = new DecimalFormat( "###,###.0000"); 
//        	double latitude = Double.valueOf(df.format(lat));
//        	double longitude = Double.valueOf(df.format(log));
//
//        	Log.v("latitude", String.valueOf(latitude));
//        	Log.v("longitude", String.valueOf(longitude));
        	
            List<Address> addresses = geoCoder.getFromLocation(lat, log, 1);
            Address address = addresses.get(0);
            int maxLine = address.getMaxAddressLineIndex();
            if(maxLine >= 2){
                add =  address.getAddressLine(1) + address.getAddressLine(2);
            }else {
                add = address.getAddressLine(1);
            }
        } catch (IOException e) {
            add = "";
            e.printStackTrace();
            Log.v(TAG, e.toString());
        }
        return add;
    }
    
    Runnable getAddressName = new Runnable() {
        @Override
        public void run() {
            String addressName = "";
            while(true){
                addressName = getLocationAddress(locPoint);
                Log.d(TAG, "获取地址名称");
                if(!"".equals(addressName)){
                    break;
                }
            }
            
            Message msg = new Message();
            msg.what = MSG_VIEW_ADDRESSNAME;
            msg.obj = addressName;
            mHandler.sendMessage(msg);
        }
    };
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_VIEW_LONGPRESS:
                {
                    if(null == locPoint) return;
                    new Thread(getAddressName).start();
                    overlayitem = new OverlayItem(locPoint, getResources().getString(R.string.LocationName),
                            getResources().getString(R.string.loading_location_name));
                    if(mLongPressOverlay.size() > 0){
                        mLongPressOverlay.removeOverlay(0);
                    }
                    popView.setVisibility(View.GONE);
                    mLongPressOverlay.addOverlay(overlayitem);
                    mLongPressOverlay.setFocus(overlayitem);
                    mapOverlays.add(mLongPressOverlay);
                    mMapCtrl.animateTo(locPoint);
                    mapView.invalidate();
                }
                break;
            case MSG_VIEW_ADDRESSNAME:
                TextView desc = (TextView) popView.findViewById(R.id.map_bubbleText);
                desc.setText((String)msg.obj);
                popView.setVisibility(View.VISIBLE);
                break;
            }
        }
    };
            
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//        case R.id.popup_run:
//         {
//             
//         }
//        break;
//        
//        default:
//            break;
//        }
//    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        fzLocation.destoryLocationManager();
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
			
		case R.id.menu_stop: //点击时设true
			Intent i_service_stop = new Intent(MyMapActivity.this, MockLocation.class);
        	stopService(i_service_stop);
        	
        	//清除提示
        	NotificationManager mNotificationManager_stop 
					= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification_stop 
					= new Notification(R.drawable.location_place
							, getResources().getString(R.string.stop_loaction)
							, System.currentTimeMillis());
			PendingIntent contentIntent_stop 
					= PendingIntent.getActivity(MyMapActivity.this, 0, getIntent(), 0);
			mNotification_stop.setLatestEventInfo(MyMapActivity.this,
					getResources().getString(R.string.stop_loaction), /*address_info*/"", contentIntent_stop);
			//mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification_stop.flags = Notification.FLAG_AUTO_CANCEL; 
			mNotificationManager_stop.notify(R.drawable.location_place, mNotification_stop);
			mNotificationManager_stop.cancel( R.drawable.location_place);
        	
		
        	//刷新界面
        	 Intent i_reflesh_stop = new Intent(MyMapActivity.this, MyMapActivity.class);
        	 i_reflesh_stop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     startActivity(i_reflesh_stop);
			 overridePendingTransition(0, 0);
        	return true;	
			
		default:
			return super.onOptionsItemSelected(item);
		}
    	
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
    
}
