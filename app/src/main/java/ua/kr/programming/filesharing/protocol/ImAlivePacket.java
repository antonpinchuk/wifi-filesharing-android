package ua.kr.programming.filesharing.protocol;

public class ImAlivePacket extends UdpPacket {
	public Class getJsonClass() { return ImAlivePacket.class; }

	public String name;

	public static ImAlivePacket fromJson(String jsonString) {
		try {
			return gson.fromJson(jsonString, ImAlivePacket.class);
		} catch (Exception ex) {
			return null;
		}
	}

}
