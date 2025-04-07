package marlkit.pushtheblock;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.EnvironmentStandard;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import util.Pair;

public class EnvPushTheBlock extends EnvironmentStandard {

	protected State2DGridInt state;
	
	public EnvPushTheBlock() {
		super(12, 12);
	}
	

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight());
		setupState();
	}
	
	@Override
	public void setupState() {
		placeBlock();
	}
	
	@Override
	public void setupAgent(MLKAgent agent) {
		agents.addAgent(agent);
		RandomGenerator rg = prng();
		int i = rg.nextInt(getWidth()-1)+1;
		int j = rg.nextInt(getHeight()-1)+1;
        state.addAgent(agent, i, j);
	}
	
	protected void placeBlock() {
		RandomGenerator rg = prng();
		int i = rg.nextInt(getWidth() - 1) + 1;
		int j = rg.nextInt(getHeight() - 1) + 1;
		state.setValue(i, j, 1);
	}
	
	@Override
	public void reset() {
		state.reset();
		RandomGenerator rg = prng();
		for(MLKAgent ag : agents.getAgents()) {
			int i = rg.nextInt(getWidth()-1)+1;
			int j = rg.nextInt(getHeight()-1)+1;
            state.addAgent(ag, i, j);
		}
		int i = rg.nextInt(getWidth()-1)+1;
		int j = rg.nextInt(getHeight()-1)+1;
		state.setValue(i, j, 1);
	}
	

	@SuppressWarnings("exports")
	@Override
	public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
		for (MLKAgent ag : agents.getAgents()) {
			Reward reward = new RewardStandard(0);
			Action2DMove action = (Action2DMove) actions.get(ag);
			
			state.moveAgent(ag, action.getValue());
			Pair<Integer, Integer> newPosition = state.getAgentPosition(ag).clone();
			
			if (state.getValue(newPosition) == 1){
				pushTheBlock(reward, action, newPosition);
			}
			
			results.put(ag, new Pair<>(action, reward));
		}
		return results;
	}


	protected void pushTheBlock(Reward reward, Action2DMove action, Pair<Integer, Integer> newPosition) {
		state.setValue(newPosition, 0);
		Pair<Integer, Integer> newBlockPos = newPosition.clone();
		newBlockPos.setFirst(newBlockPos.getFirst() + action.getValue().getFirst());
		newBlockPos.setSecond(newBlockPos.getSecond() + action.getValue().getSecond());
		if (newBlockPos.getFirst() < 0 || newBlockPos.getFirst() >= getWidth() 
				|| newBlockPos.getSecond() < 0|| newBlockPos.getSecond() >= getHeight()) {
			reward.setReward(1);
			placeBlock();
		} else {
			state.setValue(newBlockPos, 1);
		}
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
		return state.getAgentsPositions();
	}
	
	protected State getState() {
		return state;
	}
}
