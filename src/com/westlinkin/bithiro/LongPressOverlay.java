package com.westlinkin.bithiro;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.westlinkin.bithiro.MyMapActivity;
public class LongPressOverlay extends Overlay implements OnDoubleTapListener,OnGestureListener{
    private MyMapActivity mContext;
    private MapView mMapView;
    private Handler mHandler;
    private MapController mMapCtrl;
    private GestureDetector gestureScanner = new GestureDetector(this);
    private int level = 0;
    
    public LongPressOverlay(MyMapActivity context, MapView mapView, Handler handler,MapController mapCtrl){
        mContext = context;
        mMapView = mapView;
        mHandler = handler;
        mMapCtrl = mapCtrl;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        return gestureScanner.onTouchEvent(event);
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if(++level % 3 == 0){
            mMapCtrl.zoomIn();
            level = 0;
        }
        return false;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }
    @Override
    public void onShowPress(MotionEvent e) {
        
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        return false;
    }
    @Override
    public void onLongPress(MotionEvent e) {
        mContext.locPoint = mMapView.getProjection().fromPixels((int) e.getX(),
                (int) e.getY());
        mHandler.sendEmptyMessage(mContext.MSG_VIEW_LONGPRESS);
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        return false;
    }
}
