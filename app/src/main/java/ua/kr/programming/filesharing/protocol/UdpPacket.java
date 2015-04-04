package ua.kr.programming.filesharing.protocol;

public class UdpPacket extends Packet {
	public Class getJsonClass() { return UdpPacket.class; }

	public static final String TYPE_IAMALIVE = "iamalive";
	public static final String TYPE_CONFIRM = "confirm";

	public int id;
	public String type;
	public String name;


	public byte[] toBuffer() {
		byte[] jsonBuffer = toJson().getBytes();
		crc.update(jsonBuffer);

		byte[] buffer = new byte[8 + jsonBuffer.length];
		putInt(buffer, 0xDEADBABA, 0);
		putInt(buffer, (int)crc.getValue(), 4);
		putArray(buffer, jsonBuffer, 8);
		return buffer;
	}


}
