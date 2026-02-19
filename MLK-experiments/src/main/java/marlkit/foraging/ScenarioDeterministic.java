package marlkit.foraging;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import environment.state.State2DGridInt;
import util.Pair;

/**
 * Scenario that places food and agents at fixed deterministic positions.
 */
public class ScenarioDeterministic extends Scenario {
	
	private Pair<Integer, Integer>[] foodPositions;
	private Pair<Integer, Integer>[] agentPositions;
	
	public ScenarioDeterministic(Pair<Integer, Integer>[] foodPositions, Pair<Integer, Integer>[] agentPositions) {
		this.foodPositions = foodPositions;
		this.agentPositions = agentPositions;
	}

	@Override
	public void initState(RandomGenerator prng, State2DGridInt state) {
		for (Pair<Integer, Integer> pos : foodPositions) {
			int i = pos.getFirst();
			int j = pos.getSecond();
			
			if (i >= 0 && i < state.getWidth() && j >= 0 && j < state.getHeight()) {
				state.addValue(i, j, 1);
			}
		}
	}

	@Override
	public void initAgents(RandomGenerator prng, State2DGridInt state, List<MLKAgent> agents) {
		for(int k = 0; k < agents.size(); k++){
			MLKAgent ag = agents.get(k);
			Pair<Integer, Integer> pos = agentPositions[k];
			int i = pos.getFirst();
			int j = pos.getSecond();
			
			if (i >= 0 && i < state.getWidth() && j >= 0 && j < state.getHeight()) {
				state.addAgent(ag, i, j);
			}
		}
		
	}
}
