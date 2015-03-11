package kimogila.network.protocol.soe.packets;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.soe.SoeMessage;

public class SessionRequest extends SoeMessage {

	private int connectionId;
	private int maxUdpSize;
	
	public SessionRequest(short protocol) {
		super(protocol, false);
	}

	@Override
	public void decode(IoBuffer buffer) {
		System.out.println("IN: SessionRequest");
		buffer.getInt(); // crcLength, always 2 for SWG
		
		connectionId = buffer.getInt();
		maxUdpSize = buffer.getInt(); // 496
	}

	public int getConnectionId() {
		return connectionId;
	}

	public int getMaxUdpSize() {
		return maxUdpSize;
	}

}
