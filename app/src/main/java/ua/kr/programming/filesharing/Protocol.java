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

import ua.kr.programming.filesharing.protocol.ImAlivePacket;
import ua.kr.programming.filesharing.protocol.UdpPacket;

public class Protocol {
	public static final int PORT = 9999;
	public static final String TAG = "Protocol";

	public EventListener listener;

	private boolean started;

	private DatagramSocket udpSocket;

	private ImAlivePacket iAmAlivePacket;
	private DatagramPacket iAmAliveDatagramPacket;
	private IAmAliveBroadcastThread iAmAliveThreadThread;
	private UdpReadThread udpReadThread;

	private static Random random = new Random();

	public Protocol(EventListener _listener) {
		listener = _listener;
	}

	public static interface EventListener {
		public void iAmAlive(ImAlivePacket packet, InetAddress address);
	}

	public void start(Context context) {
		started = true;

		// Create I'm alive packet
		iAmAlivePacket = new ImAlivePacket();
		iAmAlivePacket.id = random.nextInt();
		iAmAlivePacket.type = UdpPacket.TYPE_IAMALIVE;
		iAmAlivePacket.name = "Anton";

		// Start "I am Alive"
		try {
			networkStateChanged(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		// stop
		stopUDPThreads();

		started = false;
	}

	/**
	 * Called at start or if WiFi enabled / disabled
	 * @param context
	 * @throws SocketException
	 */
	public void networkStateChanged(Context context) throws SocketException {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (started) {
			// WiFi Enabled
			if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI) {
				// Network interface
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				DhcpInfo dhcp = wifi.getDhcpInfo();
				// Create broadcast packet
				try {
					byte[] iAmAliveBuffer = iAmAlivePacket.toBuffer();
					iAmAliveDatagramPacket = new DatagramPacket(iAmAliveBuffer, iAmAliveBuffer.length,
						getBroadcastAddress(dhcp), PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
				startUDPThreads();
			} else {
				stopUDPThreads();
			}
		}
	}

	private void startUDPThreads() {
		// Create socket
		try {
			udpSocket = new DatagramSocket(PORT);
			iAmAliveThreadThread = new IAmAliveBroadcastThread();
			iAmAliveThreadThread.start();
			udpReadThread = new UdpReadThread();
			udpReadThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void stopUDPThreads() {
//		if (iAmAliveThreadThread != null) {
//			iAmAliveThreadThread.interrupt(); // TODO: let it finish work
//			iAmAliveThreadThread = null;
//		}
		if (udpSocket != null) {
			udpSocket.close();
			udpSocket = null;
		}
	}

	private class IAmAliveBroadcastThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					sleep(3000);
					if (udpSocket == null) {
						return;
					}
					udpSocket.setBroadcast(true);
					udpSocket.send(iAmAliveDatagramPacket);
					Log.d(TAG, "IAmAlive");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private class UdpReadThread extends Thread {
		@Override
		public void run() {
			byte[] data = new byte[1024];
			UdpPacket packet;
			byte[] jsonData;
			try {
				while (true) {
					if (udpSocket == null) {
						return;
					}
					DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
					udpSocket.receive(datagramPacket);
					try {
						// Parse packet
						packet = UdpPacket.fromBuffer(datagramPacket.getData(), datagramPacket.getLength());
					} catch (Exception e) {
						Log.e(TAG, "UDP packet from (" + datagramPacket.getAddress().getHostAddress() + "): " + e.getMessage());
						Log.d(TAG, datagramPacket.getData().toString());
						continue;
					}
					switch (packet.type) {
						case UdpPacket.TYPE_IAMALIVE:
							listener.iAmAlive(
								ImAlivePacket.fromJson(packet.jsonData),
								datagramPacket.getAddress()
							);
							break;
						default:
							Log.d(TAG, "UDP unknown packet type: " + packet.type);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private InetAddress getBroadcastAddress(DhcpInfo dhcp) throws IOException {
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

}
