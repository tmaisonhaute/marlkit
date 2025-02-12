package environment;

import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;
import util.Triple;

/**
 * Represents an environment in which agents operate.
 */
public interface MLKEnvironment {
	public abstract void reset();
	
	public abstract Map<MLKAgent, Triple<Observation, Action, Reward>> step();
}
