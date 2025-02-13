package environment;

import java.util.Map;

import agent.MLKAgent;
import learning.Experience;

/**
 * Represents an environment in which agents operate.
 */
public interface MLKEnvironment {
	public abstract void reset();
	
	public abstract void receiveAgentInfo(MLKAgent agent);
	
	public abstract Map<MLKAgent, Experience> step();

	void setupAgent(MLKAgent agent);
}
