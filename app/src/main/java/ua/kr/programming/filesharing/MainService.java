package ua.kr.programming.filesharing;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.net.InetAddress;

import ua.kr.programming.filesharing.protocol.ImAlivePacket;

public class MainService extends Service {

	public static final String TAG = "Service";

	public static Protocol protocol;


	public MainService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Service started");

		if (protocol == null) {
			protocol = new Protocol(listener);
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


	private Protocol.EventListener listener = new Protocol.EventListener() {
		@Override
		public void iAmAlive(ImAlivePacket packet, InetAddress address) {
			Log.d(Protocol.TAG, "Received: " + packet.name + ", " + address.getHostAddress());
		}
	};

}
