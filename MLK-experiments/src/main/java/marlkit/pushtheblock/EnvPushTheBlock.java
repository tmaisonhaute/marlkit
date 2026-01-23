package marlkit.pushtheblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.EnvironmentStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import marlkit.pushtheblock.events.BlockPushedEvent;
import marlkit.pushtheblock.events.BlockPushedOutEvent;
import marlkit.pushtheblock.events.MoveEvent;
import rewardmodeling.Event;
import rewardmodels.MixedReward;
import util.Pair;

public class EnvPushTheBlock extends EnvironmentStandard {

	protected State2DGridInt state;
	
	protected int numberOfBlocks;
	
	public EnvPushTheBlock() {
		super(5, 5, new MixedReward());
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
	

	@Override
	public Map<MLKAgent, Pair<Action, List<Event>>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, Pair<Action, List<Event>>> results = new HashMap<>();
		
		for (MLKAgent ag : agents.getAgents()) {
			List<Event> events = new ArrayList<>();
			Action2DMove action = (Action2DMove) actions.get(ag);
			
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			
			checkIfPushBlock(events, action, newPosition);
			
			results.put(ag, new Pair<>(action, events));
		}
		return results;
	}
	
	protected Pair<Integer, Integer> stateMoveAgent(MLKAgent agent, Action2DMove action) {
		state.moveAgent(agent, action.getValue());
		return state.getAgentPosition(agent).clone();
	}
	
	protected void checkIfPushBlock(List<Event> events, Action2DMove action, Pair<Integer, Integer> position) {
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
	
    @Override
	protected State getState() {
		return state;
	}
	
	protected int getNumberOfBlocks() {
		return numberOfBlocks;
	}
}
