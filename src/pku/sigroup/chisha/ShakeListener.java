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
	
	
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	
	// 定义sensor管理器	
	private SensorManager mSensorManager; 
	// 震动
	private Context mContext;
	private OnShakeListener mShakeListener;

	
	public interface OnShakeListener {
		public void onShake();
	}
	
	public ShakeListener(Context context) {
		mContext = context;
		onResume();
	}
	
	public void setOnShakeListener(OnShakeListener listener) {
	    mShakeListener = listener;
	}
	
	public void onResume() {
		// 获取传感器管理服务
		mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
	    if (mSensorManager == null) {
	    	throw new UnsupportedOperationException("Sensors not supported");
	    }
	    
	    // 加速度传感器
	    boolean supported =  mSensorManager.registerListener(this, 
        		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
        		SensorManager.SENSOR_DELAY_NORMAL);
	    // 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，根据不同应用，需要的反应速率不同，具体根据实际情况设定
	    
	    if (!supported) {
	    	mSensorManager.unregisterListener(this);
	    	throw new UnsupportedOperationException("Accelerometer not supported");
	    }
	}
	
	public void onStop() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
			mSensorManager = null;
	    }
	}
	
	public void onPause() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
			mSensorManager = null;
	    }
	}
	
	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		// 当传感器精度改变时回调该方法，Do nothing.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
				
		int sensorType = event.sensor.getType();
		if(sensorType != Sensor.TYPE_ACCELEROMETER) {
			return;
		}
	      
		long now = System.currentTimeMillis();
		if ((now - mLastForce) > SHAKE_TIMEOUT) {
		      mShakeCount = 0;
	    }

		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
	    float[] values = event.values;
		
	    
	    // 和reference不同，三个方向振动幅度之后超过门限，且摇动次数和持续时间超过门限即可触发摇一摇
	    if ((now - mLastTime) > TIME_THRESHOLD) {
	    	long diff = now - mLastTime;
	    	float speed = Math.abs(values[0] + values[1] + values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
	    	if (speed > FORCE_THRESHOLD) {
	    		if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
	    			mLastShake = now;
	    			mShakeCount = 0;
	    			if (mShakeListener != null) { 
	    				mShakeListener.onShake(); 
	    			}
	    		}
	    		mLastForce = now;
	    	}
	    	mLastTime = now;
	    	mLastX = values[0];
	    	mLastY = values[1];
	    	mLastZ = values[2];
	    }
   
	}
	
}