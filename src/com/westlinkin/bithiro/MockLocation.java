package com.westlinkin.bithiro;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class MockLocation extends Service implements LocationListener {
	private final static String TAG = "MockLocation";
	private String virtual_address = "";
	private MockLocationProvider mMockLocationProvider;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 0:
				Log.v(TAG, virtual_address);

				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

				String mocLocationProvider = LocationManager.NETWORK_PROVIDER;
				locationManager.addTestProvider(mocLocationProvider, false,
						false, false, false, false, false, false,
						Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
				locationManager.setTestProviderEnabled(mocLocationProvider,
						true);
				locationManager.requestLocationUpdates(mocLocationProvider, 0,
						0, MockLocation.this);

				try {
					mMockLocationProvider = new MockLocationProvider(
							locationManager, mocLocationProvider,
							virtual_address, true);

					mMockLocationProvider.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.v(TAG, e.toString());
				}

				break;
			case 1:
				LocationManager locationManager_stop = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

				String mocLocationProvider_stop = LocationManager.NETWORK_PROVIDER;
				locationManager_stop.addTestProvider(mocLocationProvider_stop,
						false, false, false, false, false, false, false,
						Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
				locationManager_stop.setTestProviderEnabled(
						mocLocationProvider_stop, true);
				locationManager_stop.requestLocationUpdates(
						mocLocationProvider_stop, 0, 0, MockLocation.this);
				// try {
				mMockLocationProvider.flag = false;
				mMockLocationProvider.mLocationListener = MockLocation.this;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// mMockLocationProvider = new
				// MockLocationProvider(locationManager_stop,
				// mocLocationProvider_stop
				// , virtual_address, true);
				// mMockLocationProvider.flag = false;
				// mMockLocationProvider.mLocationListener = MockLocation.this;
				//
				// mMockLocationProvider.start();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// Log.v(TAG, e.toString());
				// }

				break;
			}
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "onStart");
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				virtual_address = bundle.getString("virtual_address");
				virtual_address = virtual_address.replace(" ", "%20");

				Message msg = handler.obtainMessage();
				if (virtual_address.equals("")) {
					return;
				} else {
					msg.arg1 = 0;
				}
				handler.sendMessage(msg);

			}
		}

		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		// Log.v(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Message msg = handler.obtainMessage();
		msg.arg1 = 1;

		handler.sendMessage(msg);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onLocationChanged");
		Log.v("onLocationChanged ; latitude",
				String.valueOf(location.getLatitude()));
		Log.v("onLocationChanged : longitude",
				String.valueOf(location.getLongitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onProviderDisabled");

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onProviderEnabled");

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onStatusChanged");

	}
}
