package kimogila.network.helpers;

/**
 * Factory for populating the {@link MessageRegistry} inside the {@link GameServerHandler}.
 * @author Waverunner
 *
 */
public abstract class MessageRegistryFactory {

	public abstract void registerMessages(MessageRegistry registry);
	
}
