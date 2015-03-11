package kimogila.network.protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.Message;
import kimogila.network.server.client.ClientSession;

public class SwgMessage extends Message {
	
	private IoBuffer buffer;
	private int opcode;
	private short operandCount;
	
	public SwgMessage() {
		this.protocol = (short) 0x09; // Datachannel, only used for handling in the ServerHandler messageRecieved.
	}
	
	@Override
	public IoBuffer encode() { 
		IoBuffer buffer = createBuffer(6);
		buffer.putShort(operandCount);
		buffer.putInt(opcode);
		return buffer.flip(); 
	}

	@Override
	public void decode(IoBuffer buffer) {
		operandCount = buffer.getShort();
		opcode = buffer.getInt();
	}
	
	public final void setBuffer(IoBuffer buffer) {
		this.buffer = buffer;
	}
	
	public final IoBuffer getBuffer() {
		return this.buffer;
	}

	public final int getOpcode() {
		return this.opcode;
	}
}
