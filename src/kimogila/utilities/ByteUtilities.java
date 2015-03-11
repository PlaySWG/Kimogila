package kimogila.utilities;

import org.apache.mina.core.buffer.IoBuffer;

public class ByteUtilities {

	final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		
		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		
		return new String(hexChars);
	}
	
	public static int getBufferLength(IoBuffer buffer) {
		int length = 0;
		int i = 1;
		for (byte b : buffer.array()) {
			if (b != 0) {
				length = i;
			}
			i++;
		}
		return length;
	}
}
