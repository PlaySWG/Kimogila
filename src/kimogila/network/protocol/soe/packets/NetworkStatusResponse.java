package kimogila.network.protocol.soe.packets;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.soe.SoeMessage;

public class NetworkStatusResponse extends SoeMessage {

	private short clientTick;
	private int serverTick;
	private long clientSent;
	private long clientReceived;
	private long serverSent;
	private long serverReceived;
	
	public NetworkStatusResponse(short clientTick, int serverTick, long clientSent, long clientReceived, long serverSent, long serverReceived) {
		super((short) 0x08, true);
		
		this.clientTick = clientTick;
		this.serverTick = serverTick;
		this.clientSent = clientSent;
		this.clientReceived = clientReceived;
		this.serverSent = serverSent;
		this.serverReceived = serverReceived;
	}

	@Override
	public IoBuffer encode() {
		System.out.println("OUT: NetworkStatusResponse");
		IoBuffer buffer = IoBuffer.allocate(40);
		
		buffer.putShort((short) 8);
		buffer.putShort(clientTick);
		buffer.putInt(0);
		buffer.putLong(clientSent);
		buffer.putLong(clientReceived);
		buffer.putLong(serverSent);
		buffer.putLong(serverReceived);
		
		//System.out.println("NetworkStatusResponse: " + clientTick + " server tick " + serverTick + " clientSent " + clientSent + " clientrec" + clientReceived + " serverSent"
				//+ serverSent + " serverRec " + serverReceived);

		return buffer.flip();
	}

}
