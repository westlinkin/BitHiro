package com.westlinkin.bithiro;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.westlinkin.bithiro.R;
@SuppressWarnings("rawtypes")

public class MyItemizedOverlay extends ItemizedOverlay implements OnFocusChangeListener,OnClickListener{
    private static final String TAG = "MyItemizedOverlay";
    private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
    private MyMapActivity mContext;
    private GeoPoint point = null;
    private String desc = "";
    private int layout_x = 0; // 用于设置popview 相对某个位置向x轴偏移
    private int layout_y = -30; // 用于设置popview 相对某个位置向x轴偏移
    
    private MapView mMapView;
    private MapController mMapCtrl;
    private View mPopView;
    
    private Drawable itemDrawable;
    private Drawable itemSelectDrawable;
    private OverlayItem selectItem;
    private OverlayItem lastItem;
    public String address_info = "";
    public void setItemSelectDrawable(Drawable itemSelectDrawable) {
        this.itemSelectDrawable = itemSelectDrawable;
    }
    public MyItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }
    public MyItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView, View popView, MapController mapCtrl) {
        super(boundCenterBottom(defaultMarker));
        itemDrawable = defaultMarker;
        itemSelectDrawable = defaultMarker;
        mContext = (MyMapActivity) context;
        setOnFocusChangeListener(this);
        layout_x = itemDrawable.getBounds().centerX();
        layout_y = - itemDrawable.getBounds().height();
        mMapView =  mapView;
        mPopView = popView;
        mMapCtrl = mapCtrl;
    }
    @Override
    protected OverlayItem createItem(int i) {
        return overlays.get(i);
    }
    @Override
    public int size() {
        return overlays.size();
    }
    public void addOverlay(OverlayItem item) {
        overlays.add(item);
        populate();
    }
    public void removeOverlay(int location) {
        overlays.remove(location);
    }
    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        return super.onTap(p, mapView);
    }
    @Override
    protected boolean onTap(int index) {
        return super.onTap(index);
    }
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
    }
    @Override
    public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
        Log.d(TAG , "item focus changed!");
        if (null != newFocus) {
            Log.d(TAG , "centerY : " + itemDrawable.getBounds().centerY() + "; centerX :" + itemDrawable.getBounds().centerX());
            Log.d(TAG , " height : " + itemDrawable.getBounds().height());
            MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
            params.x = this.layout_x;//Y轴偏移
            params.y = this.layout_y;//Y轴偏移
            point = newFocus.getPoint();
            params.point = point;
            mMapCtrl.animateTo(point);
            TextView title_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleTitle);
            title_TextView.setText(newFocus.getTitle());
            TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
            TextView popup_run_TextView = (TextView)mPopView.findViewById(R.id.popup_run);
            if(MyLocation.isMenuShow_1(mContext)){
            	popup_run_TextView.setBackgroundResource(R.drawable.device_access_location_off_light);
            }
            if(null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())){
                desc_TextView.setVisibility(View.GONE);
            }else{
                this.desc = newFocus.getSnippet();
                desc_TextView.setText(this.desc);
                desc_TextView.setVisibility(View.VISIBLE);
            }
            RelativeLayout button = (RelativeLayout) mPopView.findViewById(R.id.map_bubblebtn);
            button.setOnClickListener(this);
            mMapView.updateViewLayout(mPopView, params);
            mPopView.setVisibility(View.VISIBLE);
            selectItem = newFocus;
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.map_bubblebtn:
        	Log.v("map_bubblebtn", "map_bubblebtn");
        	//判断是否有网络连接
        	if (!(MyLocation.hasNetworkConnection(mContext))){
        		Toast.makeText(mContext, mContext.getResources()
        				.getString(R.string.networkError), Toast.LENGTH_SHORT).show();
        		return;
        	}

           TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
           address_info = desc_TextView.getText().toString();
           if (address_info.equals("正在加载地址...") || address_info.equals("loading location name...")){
        	   Toast.makeText(mContext, mContext.getResources().getString(R.string.warning_loading_name)
        			   		, Toast.LENGTH_SHORT).show();
        	   return;
            }
           //service 在运行，先关闭
           if(MyLocation.isMenuShow_1(mContext)){
        	   Intent i_service_stop = new Intent(mContext, MockLocation.class);
        	   mContext.stopService(i_service_stop);
	           	
	           	//清除提示
	           NotificationManager mNotificationManager_stop 
	   					= (NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
	   			Notification mNotification_stop 
	   					= new Notification(R.drawable.location_place
	   							, mContext.getResources().getString(R.string.stop_loaction)
	   							, System.currentTimeMillis());
	   			PendingIntent contentIntent_stop 
	   					= PendingIntent.getActivity(mContext, 0, mContext.getIntent(), 0);
	   			mNotification_stop.setLatestEventInfo(mContext,
	   					mContext.getResources().getString(R.string.stop_loaction), /*address_info*/"", contentIntent_stop);
	   			//mNotification.defaults = Notification.DEFAULT_SOUND;
	   			mNotification_stop.flags = Notification.FLAG_AUTO_CANCEL; 
	   			mNotificationManager_stop.notify(R.drawable.location_place, mNotification_stop);
	   			mNotificationManager_stop.cancel( R.drawable.location_place);
	           	
	   		
	           	//刷新界面
	           Intent i_reflesh_stop = new Intent(mContext, MyMapActivity.class);
	           i_reflesh_stop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	           mContext.startActivity(i_reflesh_stop);
	           mContext.overridePendingTransition(0, 0);
	           return ;	
           }
           Intent i_service = new Intent(mContext, MockLocation.class);
           Bundle bundle = new Bundle();  
           bundle.putString("virtual_address", address_info);  
        //   Log.v(TAG, address_info);
           i_service.putExtras(bundle);  
             
           mContext.startService(i_service);   
          
           //启动提示
			NotificationManager mNotificationManager 
					= (NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
			Notification mNotification 
					= new Notification(R.drawable.location_place
							, mContext.getResources().getString(R.string.begin_loaction)
							, System.currentTimeMillis());
			PendingIntent contentIntent 
					= PendingIntent.getActivity(mContext, 0, mContext.getIntent(), 0);
			mNotification.setLatestEventInfo(mContext,
					mContext.getResources().getString(R.string.begin_loaction), address_info, contentIntent);
			mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_NO_CLEAR;
			mNotificationManager.notify(R.drawable.location_place, mNotification);
			
			//关闭整个activity
			Intent i_finish = new Intent(mContext,FinishActivity.class);
			i_finish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(i_finish);
			mContext.overridePendingTransition(0, 0);
			mContext.finish();
        	break;
        }
    }
    
}
