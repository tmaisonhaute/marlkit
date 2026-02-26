package environment.state;
import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;

/**
 * Represents the state of the environment in a multi-agent system.
 */
public interface State {
	
	/**
	 * Returns observations for all agents based on the current state.
	 *
	 * @return a map of agents to their observations
	 */
	public Map<MLKAgent, Observation> getObservations();
	
	/**
	 * Resets the state to its initial configuration.
	 */
	public void reset();
	
	/**
	 * Prints a representation of the current state.
	 */
	public void print();
}
