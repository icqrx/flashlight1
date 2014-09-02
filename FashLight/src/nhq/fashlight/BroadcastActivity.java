package nhq.fashlight;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BroadcastActivity extends Activity {
	private Camera camera;
	private boolean isLighOn = false;
	private Parameters parameters;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getPackageManager().getLaunchIntentForPackage("nhq.flashlight");
		if (intent != null) {
			/* we found the activity now start the activity */
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		Intent intent1 = new Intent();
		intent1.setAction("my.com.flashlight");
		sendBroadcast(intent1);
		
//		
//		if(camera == null) {
//			initCamera();
//			camera = Camera.open();
//			parameters = camera.getParameters();
//		}
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		if (prefs.getBoolean("isLightOn", false)) {
//			//btnSwitch.setText("Tab to TURN ON FlashLight");
//			turnOffFlash();
//		} else {
//			//btnSwitch.setText("Tab to TURN OFF FlashLight");
//			turnOnFlash();
//			SharedPreferences.Editor editor = prefs.edit();
//			editor.putBoolean("isLightOn", true);
//			editor.commit();		
//		}
		
		finish();
	}

	private void turnOnFlash() {
		Log.i("info", "torch is turn on!");
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);

		camera.setParameters(parameters);
		camera.startPreview();
		isLighOn = true;

	}
	
	/**
	 * turnOffFlash
	 */
	 private void turnOffFlash(){ 
         Log.i("info", "torch is turn off!"); 

		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
		camera.stopPreview();
		isLighOn = false;
	 }
	@SuppressWarnings("deprecation")
	public void initCamera(){
		SurfaceView preview = (SurfaceView)findViewById(R.id.PREVIEW);
        SurfaceHolder mHolder = preview.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
}
