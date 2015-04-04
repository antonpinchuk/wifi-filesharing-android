package ua.kr.programming.filesharing.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.CRC32;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Packet {
	public Class getJsonClass() { return Packet.class; }

	protected static Gson gson = new Gson();
	protected static CRC32 crc = new CRC32();


	public Packet fromJson(String jsonString) {
		Packet json = null;
		try {
			json = (Packet) gson.fromJson(jsonString, getJsonClass());
		} catch (Exception ex) {
		}
		return json;
	}

	public String toJson() {
		String json = null;
		try {
			json = gson.toJson(this, getJsonClass());
		} catch (Exception ex) {
		}
		return json;
	}


	// --- Helpers to pet / put data to buffer

	protected static byte getByte(byte[] buffer, int offset) {
		return buffer[offset];
	}

	protected static void putByte(byte[] buffer, byte value, int offset) {
		buffer[offset] = value;
	}

	protected static int getInt(byte[] buffer, int offset) {
		//return ByteBuffer.wrap(Arrays.copyOfRange(buffer, offset, offset + 4)).getInt();
		return ByteBuffer.wrap(buffer, offset, 4)/*.order(ByteOrder.nativeOrder())*/.getInt();
	}

	protected static void putInt(byte[] buffer, int value, int offset) {
		System.arraycopy(ByteBuffer.allocate(4)/*.order(ByteOrder.nativeOrder())*/.putInt(value).array(), 0, buffer, offset, 4);
		//buffer = ByteBuffer.wrap(buffer).putInt(offset, value).array();
	}

	protected static int getShortInt(byte[] buffer, int offset) {
		return ByteBuffer.wrap(buffer, offset, 2)/*.order(ByteOrder.nativeOrder())*/.getInt();
	}

	protected static void putShortInt(byte[] buffer, int value, int offset) {
		System.arraycopy(ByteBuffer.allocate(2)/*.order(ByteOrder.nativeOrder())*/.putInt(value).array(), 0, buffer, offset, 2);
	}

	protected static byte[] getArray(byte[] buffer, int offset, int length) {
		if (offset < 0 || buffer.length < offset + length) {
			throw new IllegalArgumentException("Offset is out of buffer size.");
		}
		return Arrays.copyOfRange(buffer, offset, offset + length);
	}

	protected static void putArray(byte[] buffer, byte[] value, int offset) {
		if (offset < 0 || buffer.length < offset + value.length) {
			throw new IllegalArgumentException("Offset is out of buffer size.");
		}
		System.arraycopy(value, 0, buffer, offset, value.length);
	}

}
