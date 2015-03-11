package kimogila.network.protocol.soe.packets;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.soe.SoeMessage;

public class ChannelData extends SoeMessage {

	private List<IoBuffer> messages = new ArrayList<IoBuffer>();
	private short sequence;
	
	public ChannelData() {
		super((short) 0x09, true);
		this.sequence = 0;
	}
	
	public ChannelData(short sequence) { 
		super((short) 0x09, true);
		this.sequence = sequence;
	}

	@Override
	public IoBuffer encode() {
		IoBuffer buffer = IoBuffer.allocate(496); // always 
		buffer.putShort(protocol);
		buffer.putShort(sequence); // TODO: sequence
		
		for (IoBuffer message : messages)
			buffer.put(message);

		int size = buffer.position();
		IoBuffer ret = IoBuffer.allocate(size).put(buffer.array(), 0, size); // TODO: Use shrink?
		return ret.flip();
	}

	@Override
	public void decode(IoBuffer buffer) {
		// TODO : Cleanup - Refactor
		buffer.position(4);
		
		if (buffer.getShort() == 0x19) {
			while (buffer.hasRemaining()) {
				short length = (short)(buffer.get() & 0xFF);
				if (length == 255)
					length = buffer.getShort();
				//if (length > Utilities.getActiveLengthOfBuffer(buffer) - buffer.position())
				//	break;
				if (length > buffer.remaining() || !buffer.hasArray() || length < 0)
					break;

				messages.add(IoBuffer.allocate(length).put(buffer.array(), buffer.position(), length));
				buffer.position(buffer.position() + length);
			}
		} else {
			int length = buffer.array().length - 4;
			messages.add(IoBuffer.allocate(length).put(buffer.array(), 4, length));
		}
	}
	
	public void addMessage(IoBuffer message) {
		messages.add(message);
	}
	
	public List<IoBuffer> getEncodedData() {
		return messages;
	}
	
}
