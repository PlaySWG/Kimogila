package kimogila.network;

import kimogila.managers.ServerManager;
import kimogila.network.helpers.MessageCRC;
import kimogila.network.helpers.MessageCompression;
import kimogila.network.helpers.MessageEncryption;
import kimogila.network.protocol.soe.packets.ChannelData;
import kimogila.network.protocol.soe.packets.MultiProtocols;
import kimogila.network.protocol.soe.packets.NetworkStatusRequest;
import kimogila.network.protocol.soe.packets.NetworkStatusResponse;
import kimogila.network.protocol.soe.packets.SessionRequest;
import kimogila.network.protocol.swg.SwgMessage;
import kimogila.utilities.ByteUtilities;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes incoming SOE and SWG packets before using them in the IoHandler
 * @author Waverunner
 *
 */
public class SoeProtocolDecoder implements ProtocolDecoder {

	@Override
	public void decode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output)
			throws Exception {
		
		buffer.position(0);
		
		handlePacketProtocol(session, buffer, output);
	}

	// Sends a form of Message to the IoHandler
	private void handlePacketProtocol(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output) {

		// TODO: REMBER TO INFLATE COMPRESSED PACKETS
		
		short protocol = buffer.getShort();

		switch(protocol) {
		
		case 0x01: // SessionRequest
			//System.out.println("Got a session request");
			SessionRequest request = new SessionRequest(protocol);
			request.decode(buffer);
			
			session.setAttribute("connectionId", request.getConnectionId());
			session.setAttribute("crc", 0xDEADBABE); // TODO: Randomize Session CRC
			
			ServerManager.getInstance().createClientSession(session, request.getConnectionId());
			output.write(request);
			break;
		
		case 0x03: // MultiSoeProtocols 

			//if ((boolean) session.getAttribute("respSent") == true)
				//return;
			
			//System.out.println("Got a MultiSoeProtocols packet to decode");
			MultiProtocols multiPacket = new MultiProtocols();
			multiPacket.decode(buffer);
			
			for (IoBuffer packetData : multiPacket.getMessages(buffer)) {
				System.out.println("PACKETDATA");
				handlePacketProtocol(session, packetData, output);
			}
			session.setAttribute("respSent", true);
			//output.write(multiPacket); //<< done via handlePacketProtocol
			break;

		//case 0x05: // Disconnect
			//break;
			
		//case 0x06: // KeepAlive (Ping)
			//break;

		case 0x07: // NetworkStatusRequest
			if ((boolean) session.getAttribute("respSent") == true)
				return;
			
			//System.out.println("Network status requested.");

			NetworkStatusRequest netReq = new NetworkStatusRequest();
			
			//System.out.println("NetRequest: " + ByteUtilities.bytesToHex(buffer.array()));
			netReq.decode(inflate(buffer, (int) session.getAttribute("crc")).position(2));
			
			session.setAttribute("respSent", true);
			output.write(netReq);
			break;

		case 0x09: // ChannelDataA
			System.out.println("Got a ChannelDataA packet to decode");
			ChannelData dataPacket = new ChannelData();
			dataPacket.decode(buffer);
			
			int count = 0;
			for (IoBuffer msgData : dataPacket.getEncodedData()) {
				if (msgData == null) {
					continue;
				}
				
				msgData.flip();
				
				SwgMessage message = new SwgMessage(); // TODO: Optimization
				message.decode(msgData);
				message.setBuffer(msgData.position(0));
				
				count++;
				output.write(message);
			}
			break;
			/*
		case 0x0D: // DataFragmentA
			break;
			
		case 0x11: // OutOfOrderPacketA
			break;
			
		case 0x15: // AcknowledgePacketA
			break;*/
			
		default: System.out.println("Unsure how to handle decoding for soe protocol " + protocol);
		}
		
	}
	
	private IoBuffer inflate(IoBuffer orig, int seed) {

		byte[] data = getArrayForInflation(orig, seed);

		data = MessageCompression.decompress(MessageEncryption.decrypt(MessageCRC.validate(data, seed), seed));
		
		IoBuffer ret = IoBuffer.wrap(data);
		ret.position(0);
		
		return ret;
	}
	
	private byte[] getArrayForInflation(IoBuffer orig, int seed) {
		int length = ByteUtilities.getBufferLength(orig);
		
		byte[] data = new byte[length];
		System.arraycopy(orig.array(), 0, data, 0, length); // Copy bytes to data
		return data;
	}
	
	@Override
	public void dispose(IoSession session) throws Exception {

	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput output) throws Exception {
		System.out.println("Finish Decode Called");

	}

}
