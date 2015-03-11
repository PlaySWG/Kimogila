package kimogila.managers;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

import kimogila.network.server.client.ClientSession;

/**
 * Core class for keeping track of connected clients to the server. This class must be initialized by Krayt in order for the server to function properly.
 * @author Waverunner
 *
 */
public class ServerManager extends Manager {

	private static ServerManager manager;
	
	private ConcurrentHashMap<Integer, ClientSession> connections;
	
	public ServerManager() {
		manager = this;
		connections = new ConcurrentHashMap<Integer, ClientSession>();
	}
	
	public ClientSession createClientSession(IoSession session, int connectionId) {
		ClientSession client = new ClientSession(session, 0xDEADBABE); // TODO: Change this CRC
		connections.put(connectionId, client);
		
		return client;
	}
	
	/**
	 * Ensures that the passed session exists in either the zone or login server.
	 * @param session
	 */
	public boolean validateSession(IoSession session) {
		if (session == null)
			return false;
		
		return connections.containsKey((Integer) session.getAttribute("connectionId"));
	}
	
	public ClientSession getConnection(int connectionId) {
		return connections.get(connectionId);
	}
	
	@Override
	public boolean start() {
		System.out.println("ServerManager loaded.");
		return true;
	}

	@Override
	public boolean shutdown() {
		System.out.println("ServerManager shutdown.");
		return true;
	}
	
	public static ServerManager getInstance() {
		if (manager == null) {
			System.err.println("FATAL: ServerManager is null!");
		}
		return manager;
	}

}
