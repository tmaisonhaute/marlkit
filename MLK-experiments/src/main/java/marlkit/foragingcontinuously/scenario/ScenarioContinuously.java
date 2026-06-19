package marlkit.foragingcontinuously.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import environment.state.State2DGridInt;
import marlkit.foraging.scenario.Scenario;
import util.Pair;

public class ScenarioContinuously implements Scenario {
	protected int step;
	protected int stepBeforeSpawn;
	protected int initNumberOfResources;
	
	public ScenarioContinuously(int stepBeforeSpawn, int initNumberOfResources) {
		step = 0;
		this.stepBeforeSpawn = stepBeforeSpawn;
		this.initNumberOfResources = initNumberOfResources;
	}
	

	@Override
	public void initState(RandomGenerator prng, State2DGridInt state) {
		for (int i = 0; i < initNumberOfResources; i++) {
			Pair<Integer, Integer> pos = getRandomEmptyPosition(prng, state);
			state.setValue(pos, 1);
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
	
	protected int stepBeforeNewResources() {
		return stepBeforeSpawn;
	}
	
	public void updateState(RandomGenerator prng, State2DGridInt state) {
		step++;
		if (step % stepBeforeNewResources() == 0) {
			Pair<Integer, Integer> pos = getRandomEmptyPosition(prng, state);
			state.setValue(pos, 1);
		}
		
	}

	protected Pair<Integer, Integer> getRandomEmptyPosition(RandomGenerator prng, State2DGridInt state){
		List<Pair<Integer, Integer>> available = new ArrayList<>();
		for (int i = 0; i < state.getWidth(); i++) {
			for (int j = 0; j < state.getHeight(); j++) {
				if (state.getValue(j, i) == 0) {
					available.add(new Pair<>(i, j));
				}
			}
		}
		return available.get(prng.nextInt(available.size()));
	}
	

}
