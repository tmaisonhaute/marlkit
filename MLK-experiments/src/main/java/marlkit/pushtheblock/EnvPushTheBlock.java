package marlkit.pushtheblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DInt;
import environment.EnvironmentStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import marlkit.pushtheblock.events.BlockPushedEvent;
import marlkit.pushtheblock.events.BlockPushedOutEvent;
import marlkit.pushtheblock.events.MoveEvent;
import reward.ReactionEvent;
import reward.RewardModel;
import util.Pair;

public class EnvPushTheBlock extends EnvironmentStandard {

	protected State2DGridInt state;
	
	protected int numberOfBlocks;
	
	public EnvPushTheBlock(RewardModel rewardModel) {
		this(5, 5, rewardModel);
	}
	
	public EnvPushTheBlock(int width, int height, RewardModel rewardModel) {
		super(width, height, rewardModel);
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
	public void setupAgents() {
		for(MLKAgent ag : agents.getAgents()) {
			int i = prng().nextInt(getWidth());
			int j = prng().nextInt(getHeight());
	        state.addAgent(ag, i, j);
		}
	}
	
	@Override
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
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
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
		
		for (MLKAgent ag : agents.getAgents()) {
			List<ReactionEvent> events = new ArrayList<>();
			Move2DInt action = (Move2DInt) actions.get(ag);
			
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			
			checkIfPushBlock(events, action, newPosition);
			
			results.put(ag, events);
		}
		return results;
	}
	
	protected Pair<Integer, Integer> stateMoveAgent(MLKAgent agent, Move2DInt action) {
		state.moveAgent(agent, action.getValue());
		return state.getAgentPosition(agent).clone();
	}
	
	protected void checkIfPushBlock(List<ReactionEvent> events, Move2DInt action, Pair<Integer, Integer> position) {
		if (state.getValue(position) >= 1){
			int nbBlocks = state.getValue(position);
			Pair<Integer, Integer> newBlockPosition = newBlockPosition(position, action);
			boolean blockPushedOut = pushTheBlock(position, newBlockPosition, nbBlocks);
			
			if (blockPushedOut) {
				for (int k = 0; k < nbBlocks; k++) {
					events.add(new BlockPushedOutEvent());
				}
			} else {
				for (int k = 0; k < nbBlocks; k++) {
					events.add(new BlockPushedEvent());
				}
			}
		} else {
			events.add(new MoveEvent());
		}
	}
	
	protected Pair<Integer, Integer> newBlockPosition(Pair<Integer, Integer> blockPosition, Move2DInt action) {
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
	
    @Override
	public State getState() {
		return state;
	}
	
	protected int getNumberOfBlocks() {
		return numberOfBlocks;
	}
}
