package marlkit.pushtheblock;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import agent.interaction.FictitiousPlay;
import environment.EnvironmentStandard;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import util.Pair;

public class EnvPushTheBlock extends EnvironmentStandard {

	protected State2DGridInt state;
	protected static final double REWARDBLOCKPUSHEDOUT = 10;
	protected static final double REWARDBLOCKPUSHED = 0.1;
	protected static final double REWARDMOVE = -0.1;
	protected static final boolean SHARED_REWARDS = false;
	protected int numberOfBlocks;
	
	public EnvPushTheBlock() {
		super(5, 5, new FictitiousPlay());
		numberOfBlocks = 1;
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight(), true, true);
		setupState();
	}
	
	@Override
	public void setupState() {
		placeBlocks();
	}
	
	@Override
	public void setupAgent(MLKAgent agent) {
		agents.addAgent(agent);
		RandomGenerator rg = prng();
		int i = rg.nextInt(getWidth());
		int j = rg.nextInt(getHeight());
        state.addAgent(agent, i, j);
	}
	
	protected void placeBlocks() {
		for (int k = 0; k < getNumberOfBlocks(); k++) {
			placeBlock();
		}
	}
	
	protected void placeBlock() {
		RandomGenerator rg = prng();
		int i = rg.nextInt(getWidth());
		int j = rg.nextInt(getHeight());
		state.addValue(i, j, 1);
	}
	
	@Override
	public void reset() {
		state.reset();
		RandomGenerator rg = prng();
		for(MLKAgent ag : agents.getAgents()) {
			int i = rg.nextInt(getWidth());
			int j = rg.nextInt(getHeight());
            state.addAgent(ag, i, j);
		}
		
		setupState();
	}
	

	@SuppressWarnings("exports")
	@Override
	public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
		
		double rewardTotal = 0;
		for (MLKAgent ag : agents.getAgents()) {
			Reward reward = new RewardStandard(0);
			Action2DMove action = (Action2DMove) actions.get(ag);
			
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			
			checkIfPushBlock(reward, action, newPosition);
			
			if (! SHARED_REWARDS) {
				results.put(ag, new Pair<>(action, reward));
			} 
			else {
				rewardTotal += reward.getValue();
			}
		}
		if (SHARED_REWARDS) {
			for (MLKAgent ag : agents.getAgents()) {
				results.put(ag, new Pair<>(actions.get(ag), new RewardStandard(rewardTotal)));
			}
		}
		return results;
	}
	
	protected Pair<Integer, Integer> stateMoveAgent(MLKAgent agent, Action2DMove action) {
		state.moveAgent(agent, action.getValue());
		return state.getAgentPosition(agent).clone();
	}
	
	protected void checkIfPushBlock(Reward reward, Action2DMove action, Pair<Integer, Integer> position) {
		if (state.getValue(position) >= 1){
			int nbBlocks = state.getValue(position);
			Pair<Integer, Integer> newBlockPosition = newBlockPosition(position, action);
			boolean blockPushedOut = pushTheBlock(position, newBlockPosition, nbBlocks);
			
			if (blockPushedOut) {
				reward.setReward(nbBlocks * REWARDBLOCKPUSHEDOUT);
			} else {
				reward.setReward(nbBlocks * REWARDBLOCKPUSHED);
			}
		}
		else {
			reward.setReward(REWARDMOVE);
		}
	}
	
	protected Pair<Integer, Integer> newBlockPosition(Pair<Integer, Integer> blockPosition, Action2DMove action) {
		return new Pair<>(blockPosition.getFirst() + action.getValue().getFirst(),
				blockPosition.getSecond() + action.getValue().getSecond());
	}

	protected boolean pushTheBlock(Pair<Integer, Integer> oldBlockPosition, 
			Pair<Integer, Integer> newBlockPosition, int nbBlocks) {
		
		state.setValue(oldBlockPosition, 0);
		
		if (newBlockPosition.getFirst() < 0 || newBlockPosition.getFirst() >= getWidth() 
				|| newBlockPosition.getSecond() < 0|| newBlockPosition.getSecond() >= getHeight()) {
			
			for (int k = 0; k < nbBlocks; k++) {
				placeBlock();
			}
			return true;
			
		} else {
			state.addValue(newBlockPosition, nbBlocks);
			return false;
		}
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
		return state.getAgentsPositions();
	}
	
	protected State getState() {
		return state;
	}
	
	protected int getNumberOfBlocks() {
		return numberOfBlocks;
	}
}
