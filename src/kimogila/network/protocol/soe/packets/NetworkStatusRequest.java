package kimogila.network.protocol.soe.packets;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.soe.SoeMessage;
import kimogila.utilities.ByteUtilities;

public class NetworkStatusRequest extends SoeMessage {

	private short tickCount;
	private long sentPackets;
	private long recievedPackets;
	
	public NetworkStatusRequest() {
		super((short) 0x07, true);
	}

	@Override
	public void decode(IoBuffer buffer) {
		System.out.println("IN: NetworkStatusRequest");
		tickCount = buffer.getShort(); // tick count
		buffer.getInt(); // lastUpdate
		buffer.getInt(); // avgUpdate
		buffer.getInt(); // shortestUpdate
		buffer.getInt(); // longestUpdate
		buffer.getInt(); // lastServerUpdate
		sentPackets = buffer.getLong(); // sentPackets
		recievedPackets = buffer.getLong(); // receivedPackets
		
		//System.out.println("NetworkStatusRequest: sentPackets " + sentPackets + " Received Packets: " + recievedPackets);
	}
	
	public short getTickCount() { return tickCount; }
	public long getSentPackets() { return sentPackets; }
	public long getRecievedPackets() { return recievedPackets; }
}
