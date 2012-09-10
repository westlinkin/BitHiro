package com.westlinkin.bithiro;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class FzLocationManager {
    private final String TAG = "FzLocationManager";
    private static Context mContext;
    private LocationManager gpsLocationManager;
    private LocationManager networkLocationManager;
    private static final int MINTIME = 2000;
    private static final int MININSTANCE = 2;
    private static FzLocationManager instance;
    private Location lastLocation = null;
    private static LocationCallBack mCallback;
    
    public static void init(Context c , LocationCallBack callback) {
        mContext = c;
        mCallback = callback;
    }
    
    private FzLocationManager() {
        // Gps 定位
        gpsLocationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        Location gpsLocation = gpsLocationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINTIME, MININSTANCE, locationListener);
        // 基站定位
        networkLocationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        Location networkLocation = gpsLocationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        networkLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, MINTIME, MININSTANCE,
                locationListener);
    }
    public static FzLocationManager getInstance() {
        if (null == instance) {
            instance = new FzLocationManager();
        }
        return instance;
    }
    private void updateLocation(Location location) {
        lastLocation = location;
        mCallback.onCurrentLocation(location);
    }
    
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            updateLocation(location);
        }
    };
    public Location getMyLocation() {
        return lastLocation;
    }
    
    private static int ENOUGH_LONG = 1000 * 60;     
    
    public interface LocationCallBack{
        /**
         * 当前位置
         * @param location 
         */
        void onCurrentLocation(Location location);
    }
    
    
    public void destoryLocationManager(){
        Log.d(TAG, "destoryLocationManager");
        gpsLocationManager.removeUpdates(locationListener);
        networkLocationManager.removeUpdates(locationListener);
    }
}