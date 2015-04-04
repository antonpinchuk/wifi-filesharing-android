package ua.kr.programming.filesharing;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

	public static final String TAG = "Service";

	public static Protocol protocol;


	public MainService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Service started");

		if (protocol == null) {
			protocol = new Protocol();
			protocol.start(getApplicationContext());
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		protocol.stop();
		protocol = null;
	}
}
