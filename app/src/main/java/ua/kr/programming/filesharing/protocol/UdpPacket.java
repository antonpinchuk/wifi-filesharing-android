package ua.kr.programming.filesharing.protocol;

public class UdpPacket extends Packet {
	public Class getJsonClass() { return UdpPacket.class; }
	public static final int SIGNATURE = 0xDEADBABA;

	public static final String TYPE_IAMALIVE = "iamalive";
	public static final String TYPE_CONFIRM = "confirm";

	public int id;
	public String type;


	public byte[] toBuffer() {
		byte[] jsonBuffer = toJson().getBytes();
		crc.reset();
		crc.update(jsonBuffer);

		byte[] buffer = new byte[8 + jsonBuffer.length];
		putInt(buffer, SIGNATURE, 0);
		putInt(buffer, (int)crc.getValue(), 4);
		putArray(buffer, jsonBuffer, 8);
		return buffer;
	}

	public static UdpPacket fromJson(String jsonString) {
		try {
			return gson.fromJson(jsonString, UdpPacket.class);
		} catch (Exception ex) {
			return null;
		}
	}


	/**
	 * Parse UDP packet
	 * @param buffer
	 * @return JSON sting
	 * @throws IllegalAccessException
	 */
	public static UdpPacket fromBuffer(byte[] buffer, int length) throws Exception {
		if (length < 8) {
			throw new Exception("Invalid packet size");
		}
		int signature = getInt(buffer, 0);
		if (signature != SIGNATURE) {
			throw new Exception("Wrong signature " + signature);
		}
		int jsonCrc = getInt(buffer, 4);
		byte[] jsonData = getArray(buffer, 8, length - 8);
		crc.reset();
		crc.update(jsonData);
		if (jsonCrc != (int)crc.getValue()) {
			throw new Exception("Invalid CRC " + jsonCrc + " != " + (int)crc.getValue());
		}
		String jsonString = new String(jsonData, "UTF-8");
		UdpPacket udpPacket = fromJson(jsonString);
		if (udpPacket == null) {
			throw new Exception("Invalid JSON " + jsonString);
		}
		udpPacket.jsonData = jsonString;
		return udpPacket;
	}

}
