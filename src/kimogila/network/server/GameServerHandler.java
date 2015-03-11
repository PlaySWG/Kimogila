package kimogila.network.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kimogila.managers.ServerManager;
import kimogila.network.helpers.MessageRegistry;
import kimogila.network.helpers.MessageRegistryFactory;
import kimogila.network.protocol.IMessageCallback;
import kimogila.network.protocol.Message;
import kimogila.network.protocol.soe.packets.NetworkStatusRequest;
import kimogila.network.protocol.soe.packets.NetworkStatusResponse;
import kimogila.network.protocol.soe.packets.SessionRequest;
import kimogila.network.protocol.soe.packets.SessionResponse;
import kimogila.network.protocol.swg.SwgMessage;
import kimogila.network.protocol.swg.packets.LoginServerId;
import kimogila.network.protocol.swg.packets.ServerString;
import kimogila.network.server.client.ClientSession;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Acts as the core functionality between the server and connected clients. Handles the creation of new sessions, closing sessions,
 * and handling sending and receiving packets.
 * @author Waverunner
 *
 */
public final class GameServerHandler extends IoHandlerAdapter {

	protected ExecutorService threads;
	protected MessageRegistry registry;
	private boolean isZone;
	
	public GameServerHandler() {
		threads = Executors.newCachedThreadPool();
		registry = new MessageRegistry();
		isZone = false;
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		//System.out.println("Session created.");
		session.setAttribute("connectionId", 0);
		session.setAttribute("crc", 0xDEADBABE); // TODO: Randomize
		session.setAttribute("sequence", 0);
		session.setAttribute("serverSent", (long) 0);
		session.setAttribute("serverReceived", (long) 0);
		session.setAttribute("respSent", false);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		//System.out.println("Session closed.");
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Idle session!");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		//System.out.println("Session opened.");
	}

	// Handles any receiving Message in a cached thread after Iobuffer decoded via SoeProtocolDecoder
	@Override
	public void messageReceived(final IoSession session, final Object message) throws Exception {
		if (!(message instanceof Message))
			return;
		
		session.setAttribute("serverReceived", ((long) session.getAttribute("serverReceived")) + 1);
		
		threads.execute(() -> {
			handleMessageResponse(session, (Message) message);
		});
		
	}
	
	private void handleMessageResponse(IoSession session, Message message) {
		Message packet = (Message) message;
		
		switch (packet.getProtocol()) {
		
		case 0x01: // SessionRequest
			SessionRequest request = (SessionRequest) packet;
			
			SessionResponse response = new SessionResponse(request.getConnectionId(), (Integer) session.getAttribute("crc"));
			session.write(response);
			
			if (isZone) {
				
			} else {
				ServerString respString = new ServerString("LoginServer:" + 0);
				LoginServerId respId = new LoginServerId(0);
				session.write(respString);
				session.write(respId);
			}
			
			break;
			
		case 0x03: // MultiProtocols - Split up packet into SwgMessages to be sent.
			System.out.println("HANDL MSG RESPONSE SHOULDNT OF MADE IT HERE");
			break;
			
		case 0x07:
			NetworkStatusRequest netReq = (NetworkStatusRequest) packet;
			// TODO: Manage this better
			NetworkStatusResponse netResp = new NetworkStatusResponse(netReq.getTickCount(), netReq.getTickCount(), 
					netReq.getSentPackets(), netReq.getRecievedPackets(), 
					(Long) session.getAttribute("serverSent") + 1, (Long) session.getAttribute("serverReceived"));

			session.write(netResp);
			//System.out.println("Responded.");
			break;
			
		case 0x09: // ChannelData packet converted to SwgMessage
			SwgMessage packetSwg = (SwgMessage) packet;
			SwgMessage handlePacket = registry.get(packetSwg.getOpcode());
			handlePacket.decode(packetSwg.getBuffer());
			
			if (handlePacket instanceof IMessageCallback) {

				ClientSession client = ServerManager.getInstance().getConnection((int) session.getAttribute("connectionId"));
				if (client == null)
					System.out.println("!! NULL CLIENT FOR CALLBACK");
				
				((IMessageCallback) handlePacket).respond(client);
				
				System.out.println("Responded via a callback");
			}
			
			break;
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		System.out.println("Input closed.");
	}

	public void populateRegistry(MessageRegistryFactory factory) {
		factory.registerMessages(registry);
	}
}
