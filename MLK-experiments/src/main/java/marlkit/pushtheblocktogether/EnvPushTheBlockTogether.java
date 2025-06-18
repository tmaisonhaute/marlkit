package marlkit.pushtheblocktogether;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import marlkit.pushtheblock.EnvPushTheBlock;
import util.Pair;

public class EnvPushTheBlockTogether extends EnvPushTheBlock {
	

	protected static final double REWARDTRYPUSH = 0;
	protected Map<Pair<Integer, Integer>, List<Action2DMove>> agentsForcePush;
	protected Map<Pair<Integer, Integer>, Integer> numberOfBlocksPushed;
	protected static final int FORCEPUSHEDTRESHOLD = 2;
	
	public EnvPushTheBlockTogether() {
		super();
		numberOfBlocks = 1; 
	}
	
	@SuppressWarnings("exports")
	@Override
	public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
		agentsForcePush = new HashMap<>();
		numberOfBlocksPushed = new HashMap<>();
		
		Reward rewardOfAgents = new RewardStandard(0);
		
		for (MLKAgent ag : agents.getAgents()) {
			Action2DMove action = (Action2DMove) actions.get(ag);
			
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			
			checkIfPushBlock(rewardOfAgents, action, newPosition);
			
		}
		
		rewardOfAgents.add(pushBlockForce());
		for (MLKAgent ag : agents.getAgents()) {
			results.put(ag, new Pair<>(actions.get(ag), rewardOfAgents.clone()));
		}
		return results;
	}
	
	@Override
	protected void checkIfPushBlock(Reward reward, Action2DMove action, Pair<Integer, Integer> position) {
		if (state.getValue(position) >= 1){
			agentsForcePush.putIfAbsent(position, new ArrayList<>());
			agentsForcePush.get(position).add(action);
			numberOfBlocksPushed.putIfAbsent(position, state.getValue(position));
		}
		else {
			reward.add(REWARDMOVE);
		}
	}
	
	protected Reward pushBlockForce() {
		double rewardValue = 0;
		for (Pair<Integer, Integer> oldPosition : agentsForcePush.keySet()) {
			int nbBlocks = numberOfBlocksPushed.get(oldPosition);
			List<Action2DMove> actions = agentsForcePush.get(oldPosition);
			
			Action2DMove globalAction = gatherActionsPush(actions);
			if (!globalAction.equals(new Action2DMove(new Pair<>(0, 0)))) {
				Pair<Integer, Integer> newBlockPosition = newBlockPosition(oldPosition, globalAction);
				boolean blockPushedOut = pushTheBlock(oldPosition, newBlockPosition, nbBlocks);
				if (blockPushedOut) {
					rewardValue += nbBlocks * REWARDBLOCKPUSHEDOUT;
				} else {
					rewardValue += nbBlocks * REWARDBLOCKPUSHED;
				}
			}
			else {
				rewardValue += actions.size() * REWARDTRYPUSH;
			}
		}
		return new RewardStandard(rewardValue);
	}
	
	protected Action2DMove gatherActionsPush(List<Action2DMove> actions) {
		Action2DMove globalAction = new Action2DMove(new Pair<>(0, 0));
		for (Action2DMove action : actions) {
			globalAction.add(action);
		}
		
		Pair<Integer, Integer> actionValue = globalAction.getValue();
		int ax = (int) (Math.abs(actionValue.getFirst()) >= FORCEPUSHEDTRESHOLD ? Math.signum(actionValue.getFirst()) : 0);
		int ay = (int) (Math.abs(actionValue.getSecond()) >= FORCEPUSHEDTRESHOLD ? Math.signum(actionValue.getSecond()) : 0);
		
		globalAction.setValue(new Pair<>(ax, ay));
		return globalAction;
		
	}

}
