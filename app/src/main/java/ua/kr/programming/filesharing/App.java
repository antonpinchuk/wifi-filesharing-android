package ua.kr.programming.filesharing;

import android.app.Application;
import android.content.Intent;

import java.net.SocketException;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// start
		startService(new Intent(this, MainService.class));
	}
}
