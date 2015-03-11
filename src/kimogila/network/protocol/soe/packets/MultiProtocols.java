package kimogila.network.protocol.soe.packets;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.soe.SoeMessage;

public class MultiProtocols extends SoeMessage {

	public MultiProtocols() {
		super((short) 0x09, false);
	}

	@Override
	public IoBuffer encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decode(IoBuffer buffer) {}

	public List<IoBuffer> getMessages(IoBuffer buffer) {
		List<IoBuffer> encodedMsgs = new ArrayList<IoBuffer>();

		while(buffer.hasRemaining()) {
			short length = (short) (buffer.get() & 0xFF);

			if (length == 255)
				length = buffer.getShort();

			if (buffer.remaining() < length || length <= 0)
				break;

			IoBuffer message = IoBuffer.allocate(length);
			message.put(buffer.array(), buffer.position(), length);
			message.flip();

			encodedMsgs.add(message);

			buffer.position(buffer.position() + length);
		}
		
		return encodedMsgs;
	}
}
