package kimogila.network.protocol.soe;

import org.apache.mina.core.buffer.IoBuffer;

import kimogila.network.protocol.Message;

public abstract class SoeMessage extends Message {

	protected boolean footer;
	protected boolean compress;
	
	public SoeMessage(short protocol, boolean footer) {
		this.protocol = protocol;
		this.footer = footer;
	}
	
	@Override
	public IoBuffer encode() { return null; }
	
	@Override
	public void decode(IoBuffer buffer) { };
	
	public final boolean hasFooter() {
		return footer;
	}
	
	public final boolean isCompressed() {
		return compress;
	}
}
