package kimogila.network.helpers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kimogila.network.protocol.swg.SwgMessage;

/**
 * Contains a registry of {@link SwgMessage} classes and their corresponding opcodes for use by {@link GameServerHandler}
 * @author Waverunner
 *
 */
public class MessageRegistry {

	private Map<Integer, Class<? extends SwgMessage>> registry; // TODO: ObjControllerOpcodes?
	
	public MessageRegistry() {
		registry = new ConcurrentHashMap<Integer, Class<? extends SwgMessage>>();
	}
	
	public void register(Class<? extends SwgMessage> messageClass, int opcode) {
		registry.put(opcode, messageClass);
	}
	
	public SwgMessage get(int opcode) {
		if (!registry.containsKey(opcode))
			return null;
		
		SwgMessage message = null;
		
		try {
			message = registry.get(opcode).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("Message Opcode " + opcode + " doesn't have a 0-constructor argument!");
			e.printStackTrace();
		}
		
		return message;
	}
}
