package kimogila.network.protocol.swg.packets;

import kimogila.network.protocol.swg.SwgMessage;

import org.apache.mina.core.buffer.IoBuffer;

public class ServerString extends SwgMessage {

	private String str;
	
	public ServerString(String str) {
		this.str = str;
	}
	
	@Override
	public IoBuffer encode() {
		IoBuffer buffer = createBuffer(10 + str.length());
		
		buffer.putShort((short)2);
		buffer.putInt(0x0E20D7E9);
		buffer.put(getAsciiString(str));

		System.out.println("OUT: LoginServerString");
		return buffer.flip();
	}

}
