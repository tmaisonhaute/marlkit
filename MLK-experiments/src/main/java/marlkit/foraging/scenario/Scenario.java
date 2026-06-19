package marlkit.foraging.scenario;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import environment.state.State2DGridInt;

/**
 * Abstract class representing a scenario for the foraging environment. 
 * A scenario defines how the environment state is initialized and how agents are placed in the environment at each reset.
 * 
 */
public interface Scenario {
	
	/**
	 * Initialize the state of the environment. This method is called at each reset of the environment. 
	 * The state is a 2D grid of integers, where each cell can represent different elements (e.g., food, obstacles, etc.) depending on the scenario. 
	 * @param prng a pseudo-random number generator that can be used to randomize the initialization of the state.
	 * @param state the state of the environment to be initialized.
	 */
	public void initState(RandomGenerator prng, State2DGridInt state);
	
	/**
	 * Initialize the agents in the environment. This method is called at each reset of the environment, after the state has been initialized.
	 * @param prng
	 * @param state
	 * @param agents
	 */
	public void initAgents(RandomGenerator prng, State2DGridInt state, List<MLKAgent> agents);
}
