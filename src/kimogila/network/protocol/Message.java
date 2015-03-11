package kimogila.network.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import kimogila.utilities.ByteUtilities;

import org.apache.mina.core.buffer.IoBuffer;

public abstract class Message {

	protected short protocol;
	//protected byte[] data;
	//protected IoBuffer buffer;
	
	public abstract IoBuffer encode();
	public abstract void decode(IoBuffer buffer);
	
	public final short getProtocol() {
		return protocol;
	}
	
	public IoBuffer createBuffer(int size) {
		return IoBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	protected String getAsciiString(IoBuffer buffer) {
		return getString(buffer, "US-ASCII");
	}
	
	protected byte[] getAsciiString(String string) {
		return getString(string, "US-ASCII");
	}
	
	private byte[] getString(String string, String charFormat) {

		// TODO: Use current IoBuffer
		ByteBuffer result;
		int length = 2 + string.length();

		if (charFormat == "UTF-16LE") {

			result = ByteBuffer.allocate(length * 2).order(
					ByteOrder.LITTLE_ENDIAN);
			result.putInt(string.length());

		} else {

			result = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
			result.putShort((short) string.length());

		}

		try {

			result.put(string.getBytes(charFormat));

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return new byte[] {};

		}

		return result.array();

	}
	
	protected String getString(IoBuffer buffer, String charFormat) {

		String result;

		int length;

		if (charFormat == "UTF-16LE")
			length = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
		else
			length = buffer.order(ByteOrder.LITTLE_ENDIAN).getShort();

		int bufferPosition = buffer.position();

		try {

			result = new String(buffer.array(), bufferPosition, length,
					charFormat);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return "";

		}

		// TODO: Verify if character position is set correctly after reading a
		// UTF-16 string
		buffer.position(bufferPosition + length);

		return result;

	}
	
	@Override
	public String toString() {
		return "Protocol " + getProtocol() + " : " +ByteUtilities.bytesToHex(encode().array());
	}
}
