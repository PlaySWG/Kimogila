package kimogila.network.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import kimogila.network.SoeProtocolCodecFactory;
import kimogila.network.helpers.MessageRegistryFactory;
import kimogila.network.server.client.ClientSession;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class GameServer {

	// TODO: Boolean for login server?
	protected boolean active;
	private int port;

	private NioDatagramAcceptor acceptor;
	
	private GameServerHandler handler;
	
	public GameServer(int port) {
		this.handler = new GameServerHandler();
		this.port = port;
		this.active = false;
	}
	
	public GameServerHandler getHandler() {
		return handler;
	}
	
	public final void launch() throws IOException {
		if (active) {
			//System.err.println("Server is already running!");
			return;
		}
		
		acceptor = new NioDatagramAcceptor();
		acceptor.setHandler(handler);

		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		//chain.addLast("logger", new LoggingFilter()); // TODO: Disable when ready
		chain.addLast("codec", new ProtocolCodecFilter(new SoeProtocolCodecFactory()));
		
		DatagramSessionConfig sessionConfig = acceptor.getSessionConfig();
		sessionConfig.setReuseAddress(true);
		
		acceptor.bind(new InetSocketAddress(port));
	}
	
	public final void registerPackets(MessageRegistryFactory factory) {
		handler.populateRegistry(factory);
	}

}
