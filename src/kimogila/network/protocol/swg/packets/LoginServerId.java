package kimogila.network.protocol.swg.packets;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.swg.SwgMessage;

public class LoginServerId extends SwgMessage {

	private int id;
	
	public LoginServerId(int serverId) {
		id = serverId;
	}
	
	@Override
	public IoBuffer encode() {
		IoBuffer buffer = createBuffer(10);
		
		buffer.putShort((short)2);
		buffer.putInt(0x58C07F21);
		buffer.putInt(id);
		
		System.out.println("OUT: LoginServerId");
		return buffer.flip();
	}

}
