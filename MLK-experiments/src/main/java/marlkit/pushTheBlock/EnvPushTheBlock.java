package marlkit.pushTheBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.EnvironmentStandard;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State2DGridInt;
import util.Pair;

public class EnvPushTheBlock extends EnvironmentStandard {

	State2DGridInt state;
	private final int width = 6;
	private final int height = 6;
	
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(width, height);
	}
	
	@Override
	public void setup() {
		RandomGenerator rg = prng();
		for(MLKAgent ag : agents.getAgents()) {
			int i = rg.nextInt(width-2)+1;
			int j = rg.nextInt(height-2)+1;
            state.addAgent(ag, i, j);
		}
		placeBlock();
	}
	
	protected void placeBlock() {
		RandomGenerator rg = prng();
		int i = rg.nextInt(width - 2) + 1;
		int j = rg.nextInt(height - 2) + 1;
		state.setValue(i, j, 1);
	}
	
	@Override
	public void reset() {
		state.reset();
		RandomGenerator rg = prng();
		for(MLKAgent ag : agents.getAgents()) {
			int i = rg.nextInt(width-2)+1;
			int j = rg.nextInt(height-2)+1;
            state.addAgent(ag, i, j);
		}
		int i = rg.nextInt(width-2)+1;
		int j = rg.nextInt(height-2)+1;
		state.setValue(i, j, 1);
	}

	@Override
	public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
		for (MLKAgent ag : agents.getAgents()) {
			Reward reward = new RewardStandard(0);
			Action2DMove action = (Action2DMove) actions.get(ag);
			//Pair<Integer, Integer> oldPosition = state.getAgentPosition(ag).clone();
			state.moveAgent(ag, action.getValue());
			Pair<Integer, Integer> newPosition = state.getAgentPosition(ag).clone();
			if (state.getValue(newPosition) == 1){
				state.setValue(newPosition, 0);
				Pair<Integer, Integer> newBlockPos = newPosition.clone();
				newBlockPos.setFirst(newBlockPos.getFirst() + action.getValue().getFirst());
				newBlockPos.setSecond(newBlockPos.getSecond() + action.getValue().getSecond());
				if (newBlockPos.getFirst() < 0 || newBlockPos.getFirst() >= width 
						|| newBlockPos.getSecond() < 0|| newBlockPos.getSecond() >= height) {
					reward.setReward(1);
					placeBlock();
				} else {
					state.setValue(newBlockPos, 1);
				}
			}
			results.put(ag, new Pair<Action, Reward>(action, reward));
		}
		return results;
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
		return state.getAgentsPositions();
		
	}
}
