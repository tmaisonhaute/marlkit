package environment.state;
import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;

/**
 * Represents the state of the environment.
 */
public interface State {
	
	public Map<MLKAgent, Observation> getObservations();
	
	public void print();
}
