package kimogila.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The core class for the initialization, startup, and shutdown of all managers.
 * @author Waverunner
 */
public abstract class ManagerFactory {

	private List<Manager> managers;
	
	public ManagerFactory() {
		managers = new ArrayList<Manager>();
	}
	
	protected abstract void init();
	
	protected void add(Manager manager) {
		managers.add(manager);
	}
	
	public void run() {
		init();
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for (Manager manager : managers) {
			executor.execute(() -> {
				manager.start();
			});
		}
	}
	
	public void shutdown() {
		for (Manager manager : managers) {
			manager.shutdown();
		}
	}
}
