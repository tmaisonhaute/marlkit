package environment;
import java.util.Map;

import agent.Action;
import agent.Agent;
import agent.AgentsGroup;
import util.Pair;

/**
 * Represents a multi-agent environment.
 */
public abstract class EnvironmentMultiAgent implements Environment {
	public State state;
	public AgentsGroup agents;
	
	/**
     * Resets the environment to its initial state.
     * 
     * @return a map of agents to their initial observations
     */
	public abstract Map<Agent,Observation> reset();

	/**
     * Takes a step in the environment based on the given action.
     * 
     * @param action the action to take
     * @return a map of agents to pairs of resulting observations and rewards
     */
	public abstract Map<Agent, Pair<Observation, Reward>> step(Action action);
}
