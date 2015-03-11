package kimogila.network.protocol;

import kimogila.network.server.client.ClientSession;

/**
 * Interface class for responding to a request packet when it is received by the server.
 * @author Waverunner
 *
 */
public interface IMessageCallback {
	/**
	 * How the server should respond when packet is received.
	 */
	public void respond(ClientSession session);
}
