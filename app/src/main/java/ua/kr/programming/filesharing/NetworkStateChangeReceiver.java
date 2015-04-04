package ua.kr.programming.filesharing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.net.SocketException;

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainService.protocol != null) {
            try {
                MainService.protocol.networkStateChanged(context);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

}
