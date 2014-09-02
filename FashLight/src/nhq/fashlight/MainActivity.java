package nhq.fashlight;

import java.io.IOException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends ActionBarActivity implements OnClickListener, Callback{
	private BootstrapButton btnSwitch;
	private BootstrapButton btnShortcut;
	private BootstrapButton btnShare;
	private BootstrapButton btnRate;
	//flag to detect flash is on or off 
	private boolean isLighOn = false;
	public static boolean isService = false;
	private Camera camera;
	private Parameters parameters;
	private SurfaceHolder mHolder;
	private IntentFilter mIntentFilter;
	private AdView mAdView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(R.color.bbutton_danger_pressed));
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		btnSwitch = (BootstrapButton)findViewById(R.id.btnSwitch);
		btnShortcut = (BootstrapButton)findViewById(R.id.btnShortcut);
		btnShare = (BootstrapButton)findViewById(R.id.btnShare);
		btnRate = (BootstrapButton)findViewById(R.id.btnRate);
		
		btnSwitch.setOnClickListener(this);
		btnShortcut.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnRate.setOnClickListener(this);
		
		Context context = this; 
	    PackageManager pm = context.getPackageManager(); 
	    
	    initCamera();
	    // if device support camera? 
	    if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) { 
	        Log.e("err", "Device has no camera!"); 
	        return; 
	    }
	    
	    camera = Camera.open(); 
	    parameters = camera.getParameters(); 
	    mIntentFilter = new IntentFilter("my.com.flashlight");
	    registerReceiver(broadcastReceiver, mIntentFilter);

		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();

		//Load the adView with the ad request.
		mAdView.loadAd(adRequest);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
		mAdView.destroy();
	}
	@Override
	protected void onPause() {
		mAdView.pause();
		super.onPause();
	}
	/**
	 * initCamera
	 */
	@SuppressWarnings("deprecation")
	public void initCamera(){
		SurfaceView preview = (SurfaceView)findViewById(R.id.PREVIEW);
        SurfaceHolder mHolder = preview.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (camera != null) {
			//camera.release();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		mAdView.resume();
		stopService(new Intent(MainActivity.this, RunBackgroundService.class));
		if (isService) {
			isService = false;
		}
	}
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		//showExitDialog();
		startService(new Intent(MainActivity.this, RunBackgroundService.class));
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		isService = true;
	}
	
	private void showExitDialog(){
		new AlertDialog.Builder(this) 
	    .setTitle("Delete entry") 
	    .setMessage("Are you sure you want to delete this entry?") 
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { 
	        public void onClick(DialogInterface dialog, int which) { 
	        	 android.os.Process.killProcess(android.os.Process.myPid());
                 System.exit(1);
	        } 
	     }) 
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing 
	        } 
	     }) 
	    .setIcon(android.R.drawable.ic_dialog_alert) 
	     .show(); 
		
	}
	
	/**
	 * turnOnFlash
	 */
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
	 
	/**
	 * make shortcut
	 */
	private void makeShortcut() {
		Intent shortcutIntent = new Intent(getApplicationContext(), BroadcastActivity.class);
		shortcutIntent.setAction(Intent.ACTION_MAIN);
		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"Flash Light");
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.drawable.flash));

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent); 
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.w("QUOC", "adsadad");
	    	if (isLighOn) {
				turnOffFlash();
			} else {
				turnOnFlash();
			}
	    }
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSwitch:
			if (isLighOn) {
				btnSwitch.setText("Tab to TURN ON FlashLight");
				turnOffFlash();
			} else {
				btnSwitch.setText("Tab to TURN OFF FlashLight");
				turnOnFlash();
			}
			break;
		case R.id.btnShortcut:
			makeShortcut();
			Toast.makeText(this, "Make shortcut success", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnShare:
			shareApp();
			break;
		case R.id.btnRate:
			btnRateAppOnClick();
			break;

		default:
			break;
		}
	}
	//On click event for rate this app button
	public void btnRateAppOnClick() {
		final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
		try { 
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
		} 
	}
	/**
	 * shareApp
	 */
	public void shareApp(){
		try {
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FLASH LIGHT");
			String shareMessage = getString(R.string.share_message);
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
			startActivity(Intent.createChooser(shareIntent, getString(R.string.share_message)));
		} catch (final ActivityNotFoundException e) {
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHolder = holder;
		try {
			Log.i("SurfaceHolder", "setting preview");
			camera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("SurfaceHolder", "stopping preview");
		camera.stopPreview();
		mHolder = null;		
	}

	
}
