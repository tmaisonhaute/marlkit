package marlkit.foraging;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import environment.state.State2DGridInt;

/**
 * Scenario that places food uniformly at random positions in the grid.
 */
public class ScenarioUniform extends Scenario {
	
	private int numberOfFoodItems;
	
	public ScenarioUniform(int numberOfFoodItems) {
		this.numberOfFoodItems = numberOfFoodItems;
	}

	@Override
	public void initState(RandomGenerator prng, State2DGridInt state) {
		for (int k = 0; k < numberOfFoodItems; k++) {
			int i = prng.nextInt(state.getWidth());
			int j = prng.nextInt(state.getHeight());
			state.addValue(i, j, 1);
		}
	}

	@Override
	public void initAgents(RandomGenerator prng, State2DGridInt state, List<MLKAgent> agents) {
		for (MLKAgent agent : agents) { 
			int i = prng.nextInt(state.getWidth());
			int j = prng.nextInt(state.getHeight());
			state.addAgent(agent, i, j);
		}
		
	}
}
