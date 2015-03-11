package kimogila.network;

import kimogila.network.helpers.MessageCRC;
import kimogila.network.helpers.MessageCompression;
import kimogila.network.helpers.MessageEncryption;
import kimogila.network.protocol.Message;
import kimogila.network.protocol.soe.SoeMessage;
import kimogila.network.protocol.soe.packets.ChannelData;
import kimogila.network.protocol.swg.SwgMessage;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class SoeProtocolEncoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession session, Object input, ProtocolEncoderOutput output) throws Exception {
		if (!(input instanceof Message)) {
			System.err.println("Tried to encode a non-message object");
			return;
		}
		
		if (input instanceof SoeMessage) {
			handleEncodingSoeMessage(session, (SoeMessage) input, output);
			session.setAttribute("serverSent", (long) session.getAttribute("serverSent") + 1);
		} else if (input instanceof SwgMessage) {
			handleEncodingSwgMessage(session, (SwgMessage) input, output);
			session.setAttribute("serverSent", (long) session.getAttribute("serverSent") + 1);
		}

	}

	private void handleEncodingSoeMessage(IoSession session, SoeMessage packet, ProtocolEncoderOutput output) {
		//System.out.println("Encoding SoeMessage");
		int seed = (int) session.getAttribute("crc");

		if (packet.hasFooter()) {
			//System.out.println("SoeMessage Pre-Comp: " + ByteUtilities.bytesToHex(packet.encode().array()));
			byte[] data = MessageCRC.append(MessageEncryption.encrypt(MessageCompression.compress(packet.encode().array()), seed), seed);
			//System.out.println("SoeMessage Comp: " + ByteUtilities.bytesToHex(data));
			IoBuffer buffer = IoBuffer.wrap(data);
			output.write(buffer);
		} else {
			//System.out.println("No footer, leaving as is.");
			//byte[] enc = encryption.encrypt(packet.encode().array(), seed);
			//System.out.println("Encrypted: " + ByteUtilities.bytesToHex(enc));
			//byte[] data = messageCrc.append(enc, seed);
			//System.out.println("Data: "+ ByteUtilities.bytesToHex(data));
			
			//IoBuffer buffer = IoBuffer.wrap(data);
			//output.write(buffer);
			output.write(packet.encode());
		}
	}
	
	private void handleEncodingSwgMessage(IoSession session, SwgMessage message, ProtocolEncoderOutput output) {
		int sequence = (int) session.getAttribute("sequence");
		int seed = (int) session.getAttribute("crc");
		
		//System.out.println("Sequence: " + sequence);
		ChannelData packet = new ChannelData((short) sequence);
		packet.addMessage(message.encode());
		
		// TODO: Size checks for more than 496

		//System.out.println("Untouched: " + ByteUtilities.bytesToHex(untouched));
		byte[] compressed = MessageCRC.append(MessageEncryption.encrypt(MessageCompression.compress(packet.encode().array()), seed), seed); //TODO: Change deababe 
		
		//System.out.println("BytesCrcEncryptCompress: " + ByteUtilities.bytesToHex(compressed));

		IoBuffer buffer = IoBuffer.wrap(compressed);
		// No need to flip, just creating a new buffer with the compressed bytes.
		
		session.setAttribute("sequence", sequence + 1);
		output.write(buffer);
	}
	
	@Override
	public void dispose(IoSession session) throws Exception {}
}
