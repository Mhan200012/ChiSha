// http://www.apkbus.com/android-20761-1-1.html

package pku.sigroup.chisha;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class ChiShaActivity extends Activity {
	private ShakeListener mShaker;
	String[] canteen;
	TextView text;
	int last;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        text = (TextView) findViewById(R.id.textview);
        canteen = getResources().getStringArray(R.array.canteen);
        
        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
	    mShaker = new ShakeListener(this);
	    mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
		    public void onShake() {
		    	int r;
		    	do {
		    		r =  (int)(Math.random() * canteen.length);
		    	} while(r==last);
		    	last = r;
		    	text.setText(canteen[r]);
		    	vibe.vibrate(100);
		    }
	    });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onResume(){
    	text.setText(R.string.welcome);
		mShaker.onResume();
		super.onResume();
    }
    @Override
    protected void onStop(){
    	mShaker.onPause();
	    super.onPause();
    }

    @Override
    protected void onPause(){
    	mShaker.onStop();
    	super.onStop();
    }

}
