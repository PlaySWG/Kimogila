package kimogila.network.protocol.soe.packets;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.soe.SoeMessage;

public class SessionResponse extends SoeMessage {

	private int connectionId;
	private int crc;
	
	public SessionResponse(int connectionId, int crc) {
		super((short) 0x02, false);
		
		this.connectionId = connectionId;
		this.crc = crc;
	}
	
	@Override
	public IoBuffer encode() {
		System.out.println("OUT: SessionResponse");
		IoBuffer buffer = IoBuffer.allocate(17); // TODO: Is SessionResponse allocated as big or little endian? (Using none right now)
		
		buffer.putShort((short) 0x02);
		buffer.putInt(connectionId);
		buffer.putInt(crc);
		buffer.put((byte) 2); // crc length, always 2
		buffer.put((byte) 1); // 0 = no compression, 1 = use compression
		buffer.put((byte) 4); // crc seed size
		buffer.putInt(496); // Servers max UDP size
		
		return buffer.flip();
	}

}
