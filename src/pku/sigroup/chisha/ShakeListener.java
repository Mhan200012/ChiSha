// http://android.hlidskialf.com/blog/code/android-shake-detection-listener

package pku.sigroup.chisha;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class ShakeListener implements SensorEventListener {

	private static final int FORCE_THRESHOLD = 350;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Context mContext;
	
	private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
	private long mLastTime;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	
	protected void resume() {
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	
		if (mSensorManager == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}
			
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		
		boolean supported = mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (!supported) {
			mSensorManager.unregisterListener(this, mSensor);
			throw new UnsupportedOperationException("Accelerometer not supported");
	    }
	}
	
	public void pause() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this, mSensor);
	    	mSensorManager = null;
	    }
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int rate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (mSensor != mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) 
			return;
	    long now = System.currentTimeMillis();

	    if ((now - mLastForce) > SHAKE_TIMEOUT) {
	    	mShakeCount = 0;
	    }

	    if ((now - mLastTime) > TIME_THRESHOLD) {
	    	long diff = now - mLastTime;
	    	float speed = Math.abs(event.values[0] + event.values[1] + event.values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
	    	if (speed > FORCE_THRESHOLD) {
	    		if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
	    			mLastShake = now;
	    			mShakeCount = 0;
//	    			if (mShakeListener != null) { 
//	    				mShakeListener.onShake(); 
//	    			}
	    		}
	    		mLastForce = now;
	    	}
	    	mLastTime = now;
	    	mLastX = event.values[0];
	        mLastY = event.values[1];
	        mLastZ = event.values[2];
	    }


	}
}
