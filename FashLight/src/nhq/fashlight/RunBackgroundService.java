package nhq.fashlight;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class RunBackgroundService extends Service {
	private NotificationManager mNM;
	Bundle b;
	Intent notificationIntent;
	private final IBinder mBinder = new LocalBinder();
	private String newtext;

	public class LocalBinder extends Binder {
		RunBackgroundService getService() {
			return RunBackgroundService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
//		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//
//		newtext = "BackGroundApp Service Running";
//		
//		Notification notification = new Notification(R.drawable.ic_launcher, newtext,System.currentTimeMillis());
//		PendingIntent contentIntent = PendingIntent.getActivity(RunBackgroundService.this, 0, new Intent(RunBackgroundService.this,	MainActivity.class), 0);
//		notification.setLatestEventInfo(RunBackgroundService.this,"BackgroundAppExample", newtext, contentIntent);
//		mNM.notify(R.string.local_service_started, notification);
//		notificationIntent = new Intent(this, RunBackgroundService.class);
//		showNotification();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	@Override
	public void onDestroy() {
		//mNM.cancel(R.string.local_service_started);
		stopSelf();
	}
	private void showNotification() {
		CharSequence text = getText(R.string.local_service_started);
		
		Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);
		notification.setLatestEventInfo(this, "BackgroundAppExample",newtext, contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;     
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		mNM.notify("abc", 0, notification);
	}
}
