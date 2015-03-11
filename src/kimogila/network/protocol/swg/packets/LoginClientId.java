package kimogila.network.protocol.swg.packets;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.IMessageCallback;
import kimogila.network.protocol.swg.SwgMessage;
import kimogila.network.server.client.ClientSession;

public class LoginClientId extends SwgMessage implements IMessageCallback {

	private String account;
	private String passcode;
	//private String version; // TODO: Version checks for possible older clients
	
	@Override
	public void decode(IoBuffer buffer) {
		
	}

	@Override
	public void respond(ClientSession session) {
		
	}
}
