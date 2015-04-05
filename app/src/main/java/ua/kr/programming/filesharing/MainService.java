package ua.kr.programming.filesharing;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.joda.time.DateTime;

import java.net.InetAddress;
import java.sql.Time;
import java.util.ArrayList;

import ua.kr.programming.filesharing.models.User;
import ua.kr.programming.filesharing.protocol.ImAlivePacket;

public class MainService extends Service {

	public static final String TAG = "Service";

	public static Protocol protocol;

	public static ArrayList<User> users = new ArrayList<User>();

	public MainService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Service started");

		if (protocol == null) {
			protocol = new Protocol(listener);
			protocol.start(getApplicationContext());

			onlineHandler.post(onlineRunnable);
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
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				if (user.name == packet.name && user.ip == address.getHostAddress()) {
					return;
				}
			}
			User user = new User();
			user.name = packet.name;
			user.ip = address.getHostAddress();
			user.timestamp = DateTime.now().getMillis();
			users.add(user);
		}
	};


	private Handler onlineHandler = new Handler();
	private Runnable onlineRunnable = new Runnable() {
		@Override
		public void run() {
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				if (user.timestamp + 5000 < DateTime.now().getMillis()) {
					users.remove(i);
				}
			}
			onlineHandler.postDelayed(onlineRunnable, 5000);
		}
	};

}

