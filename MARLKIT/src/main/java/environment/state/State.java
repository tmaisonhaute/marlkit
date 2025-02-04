package environment.state;
import java.util.Map;

import agent.*;
import environment.observation.Observation;

/**
 * Represents the state of the environment.
 */
public interface State {
	public Map<Agent, Observation> getObservations();
	
	public void print();
}
