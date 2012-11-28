package com.westlinkin.bithiro;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MyLocation {
	private final static String TAG = "MyLocation";
	public MyLocation(){
		
	}
		
	//判断是否为一个地址
	public boolean isAddress(String address){
			
		return true;
	}
		
	public boolean isCity(String city, Context context){
		DBService dbService = new DBService(context);
		Cursor cursor = dbService.getCurcor("select * from cities where cities = '" + city + "';", null);
		if(cursor.getCount() > 0){
		    return true;
		}
		else{
		    return isAddress(city);
		}
	}
	
	public static boolean isMenuShow_1(Context context){ // 停止按钮显示与否
		return isServiceRunning(context);
	}
	
	public static boolean isMenuShow_0(Context context){ // run按钮显示与否
		return (!isServiceRunning(context));
	}
	
	public static boolean hasNetworkConnection(Context context){
		ConnectivityManager mConnectivity 
				= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		  Log.v(TAG, "hasNetworkConnection");
		 // 检查网络连接，如果无网络可用，就不需要进行连网操作等    
		 NetworkInfo info = mConnectivity.getActiveNetworkInfo();    
		 if (info == null ||   
		         !mConnectivity.getBackgroundDataSetting()) {   
		         return false;    
		 }   
		 return true;
	}
	
	public static double[] getLocationInfo(String address)  
    {  
		Log.v(TAG, address);
		//address 中，在所有答谢字母前，都加上%20
		
        // 定义一个HttpClient，用于向指定地址发送请求  
        HttpClient client = new DefaultHttpClient();  
        // 向指定地址发送GET请求  
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + address  
            + "&sensor=true");  
        StringBuilder sb = new StringBuilder();  
        try  
         {  
            // 获取服务器的响应  
            HttpResponse response = client.execute(httpGet); 
            
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	           
	            HttpEntity entity = response.getEntity();  
	            
	            // 获取服务器响应的输入流  
	            InputStream stream = entity.getContent();  
	            int b;  
	            // 循环读取服务器响应  
	            while ((b = stream.read()) != -1)  
	            {  
	                sb.append((char) b);  
	            }  
	            // 将服务器返回的字符串转换为JSONObject对象  
	            JSONObject jsonObject = new JSONObject(sb.toString());  
	            Log.v("MyLocation","2");
	            // 从JSONObject对象中取出代表位置的location属性  
	            JSONObject location = jsonObject.getJSONArray("results")  
	                .getJSONObject(0)     
	                .getJSONObject("geometry").getJSONObject("location");  
	            // 获取经度信息  
	            double longitude = location.getDouble("lng"); 
	            // 获取纬度信息  
	            double latitude = location.getDouble("lat");  
	            // 将经度、纬度信息组成double[]数组  
	            return new double[]{longitude , latitude}; 
            }
            else{
            	return new double[]{0, 0};
            }
        }catch (Exception e)  
         {  
            e.printStackTrace();  
            Log.v("MyLocation", e.toString());
         }  
        return null;  
    }   
	
	
	private static boolean isServiceRunning(Context context) {
	    boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
	    List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
	    if (serviceList.size() <= 0) {
	    //	Log.v(TAG, "serviceList.size() <= 0");
	        return false;
	    }
	    for (int i=0; i < serviceList.size(); i++) {
	    //	Log.v(TAG, "serviceList.size() > 0");
	        if (serviceList.get(i).service.getClassName().equals("com.westlinkin.bithiro.MockLocation")) {

		    	Log.v(TAG, "equals(MockLocation)");
	            isRunning = true;
	            break;
	        }
	    }
	    return isRunning;
	}
	
		
}
