package kimogila.network.server.client;

import java.util.List;

import kimogila.network.protocol.Message;

import org.apache.mina.core.session.IoSession;

public class ClientSession {
	private IoSession session;
	private int crc;
	@SuppressWarnings("unused")
	private List<byte[]> packetQueue;
	private int server;
	
	public ClientSession(IoSession session, int crc) {
		this.session = session;
		this.crc = crc;
	}
	
	/**
	 * Adds {@link Message} to the packet queue for sending to the client on the next flush.
	 * @param message Message to send to the client
	 * @see Message
	 */
	public void send(Message message) {
		session.write(message);
		// TODO: Add packets to packet queue instead of writing it to the IoSession
	}
	
	public final int getConnectionCRC() {
		return crc;
	}
	
	public int getServer() {
		return server;
	}
}
