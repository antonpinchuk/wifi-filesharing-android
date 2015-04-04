package ua.kr.programming.filesharing;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import ua.kr.programming.filesharing.protocol.UdpPacket;

public class Protocol {
	public static final int PORT = 9999;
	public static final String TAG = "Protocol";

	private boolean started;

	private DhcpInfo dhcp;
	private DatagramSocket udpSocket;

	private IAmAliveThreadThread iAmAliveThreadThread;
	DatagramPacket iAmAliveDatagramPacket;

	private static Random random = new Random();

	public Protocol() {
	}

	public void start(Context context) {
		started = true;

		// Create I'm alive packet
		UdpPacket iAmAlivePacket = new UdpPacket();
		iAmAlivePacket.id = random.nextInt();
		iAmAlivePacket.type = UdpPacket.TYPE_IAMALIVE;
		iAmAlivePacket.name = "Anton";
		byte[] iAmAliveBuffer = iAmAlivePacket.toBuffer();
		try {
			networkStateChanged(context);

			iAmAliveDatagramPacket = new DatagramPacket(iAmAliveBuffer, iAmAliveBuffer.length,
				getBroadcastAddress(), PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		// stop
		stopIAmAliveThread();

		started = false;
	}

	public void sendImAlive() {
		if (dhcp == null) {
			return;
		}
		try {
			udpSocket.setBroadcast(true);
			udpSocket.send(iAmAliveDatagramPacket);
			// If you want to listen for a response ...
			//byte[] buf = new byte[1024];
			//DatagramPacket iAmAliveDatagramPacket = new DatagramPacket(buf, buf.length);
			//udpSocket.receive(iAmAliveDatagramPacket);
			Log.d(TAG, "IAmAlive");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void networkStateChanged(Context context) throws SocketException {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (started) {
			if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI) {
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				dhcp = wifi.getDhcpInfo();
				udpSocket = new DatagramSocket(PORT);
				iAmAliveThreadThread = new IAmAliveThreadThread();
				iAmAliveThreadThread.start();
			} else {
				stopIAmAliveThread();
			}
		}
	}

	private void stopIAmAliveThread() {
		if (iAmAliveThreadThread != null) {
			iAmAliveThreadThread.interrupt(); // TODO: let it finish work
			iAmAliveThreadThread = null;
		}
		if (udpSocket != null) {
			udpSocket.close();
			udpSocket = null;
		}
		dhcp = null;
	}

	private class IAmAliveThreadThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					sleep(3000);
					sendImAlive();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};


	private InetAddress getBroadcastAddress() throws IOException {
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

}
