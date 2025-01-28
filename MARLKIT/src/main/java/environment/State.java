package environment;
import java.util.Map;

import agent.Agent;

/**
 * Represents the state of the environment.
 */
public interface State {
	public Map<Agent, Observation> getObservations();
}
