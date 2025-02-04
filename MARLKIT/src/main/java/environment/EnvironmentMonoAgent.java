package environment;

import agent.*;
import agent.action.Action;
import environment.observation.Observation;
import environment.reward.*;
import environment.state.State;
import util.*;

/**
 * Represents a mono-agent environment.
 */
public abstract class EnvironmentMonoAgent implements Environment {
	public State state;
	public Agent agent;
	
	/**
     * Resets the environment to its initial state.
     * 
     * @return the initial observation
     */
	public abstract Observation reset();

	/**
     * Takes a step in the environment based on the given action.
     * 
     * @param action the action to take
     * @return a pair of the resulting observation and reward
     */
	public abstract Pair<Observation, Reward> step(Action action);
}
