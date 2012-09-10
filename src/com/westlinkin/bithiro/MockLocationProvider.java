package com.westlinkin.bithiro;

import java.io.IOException;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class MockLocationProvider extends Thread{
    private LocationManager locationManager;

    private String mocLocationProvider;

    private String TAG = "MockLocationProvider";
    
    public boolean flag;
    
    private int i = 0;
    
    private String virtual_address;
    
    private double[] loc;
    
    public LocationListener mLocationListener;
    
    public MockLocationProvider(LocationManager locationManager, String mocLocationProvider,
            String virtual_address, boolean flag) throws IOException {

        this.locationManager = locationManager;
        this.mocLocationProvider = mocLocationProvider;
        this.virtual_address = virtual_address;
        this.flag = flag;
    }

    @Override
    public void run() {
    	while(flag){
	    	try {
	    		Thread.sleep(1000);
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	if (i == 0){
	    		loc = MyLocation.getLocationInfo(virtual_address);
	    		Log.v(TAG, "getLocationInfo from Internet");
	    		i++;
	    	}
	    	
    		Thread.yield();
	       // Set one position
	       Double latitude = loc[1];
	       Double longitude = loc[0];
	       Location location = new Location(mocLocationProvider);
	       location.setLatitude(latitude);
	       location.setLongitude(longitude);
	       location.setAltitude(0);
	
	       Log.v(TAG, location.toString());
	
	       // set the time in the location. If the time on this location
	       // matches the time on the one in the previous set call, it will be
	       // ignored
	       location.setTime(System.currentTimeMillis());
	       locationManager.setTestProviderStatus(mocLocationProvider, NORM_PRIORITY, null, 0);
	
	       locationManager.setTestProviderLocation(mocLocationProvider, location);
	       Log.v(TAG, "Thread over");
    	}
    	if(!flag){
    		Log.v(TAG, "stop mock location");
    		locationManager.clearTestProviderLocation(mocLocationProvider);
    		locationManager.clearTestProviderEnabled(mocLocationProvider);
    		locationManager.removeTestProvider(mocLocationProvider);
    		locationManager.removeUpdates(mLocationListener);
    	}
    }
}