package marlkit.pushtheblocktogether;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import marlkit.pushtheblock.EnvPushTheBlock;
import marlkit.pushtheblock.events.BlockPushedEvent;
import marlkit.pushtheblock.events.BlockPushedOutEvent;
import marlkit.pushtheblock.events.MoveEvent;
import rewardmodeling.ReactionEvent;
import util.Pair;

public class EnvPushTheBlockTogether extends EnvPushTheBlock {
	
	protected Map<Pair<Integer, Integer>, List<Pair<MLKAgent, Action2DMove>>> agentsForcePush;
	protected Map<Pair<Integer, Integer>, Integer> numberOfBlocksPushed;
	protected static final int FORCEPUSHEDTRESHOLD = 2;
	
	public EnvPushTheBlockTogether() {
		super();
		numberOfBlocks = 1; 
	}
	
	@Override
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
		Map<MLKAgent, List<ReactionEvent>> agentsEvents = new HashMap<>();
		agentsForcePush = new HashMap<>();
		numberOfBlocksPushed = new HashMap<>();
		
		List<ReactionEvent> events = new ArrayList<>();
		
		for (MLKAgent ag : agents.getAgents()) {
			agentsEvents.put(ag, new ArrayList<>());
			
			Action2DMove action = (Action2DMove) actions.get(ag);
			
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			
			checkIfPushBlockTogether(events, action, newPosition, ag);
			
		}
		
		pushBlockForce(agentsEvents);
		
		for (MLKAgent ag : agents.getAgents()) {
			List<ReactionEvent> agentEvents = new ArrayList<>(agentsEvents.get(ag));
			results.put(ag, agentEvents);
		}
		
		return results;
	}
	
	protected void checkIfPushBlockTogether(List<ReactionEvent> events, Action2DMove action, Pair<Integer, Integer> position, MLKAgent agent) {
		if (state.getValue(position) >= 1){
			agentsForcePush.putIfAbsent(position, new ArrayList<>());
			agentsForcePush.get(position).add(new Pair<>(agent, action));
			numberOfBlocksPushed.putIfAbsent(position, state.getValue(position));
		}
		else {
			events.add(new MoveEvent());
		}
	}
	
	protected void pushBlockForce(Map<MLKAgent, List<ReactionEvent>> agentsEvents) {
		for (Pair<Integer, Integer> oldPosition : agentsForcePush.keySet()) {
			int nbBlocks = numberOfBlocksPushed.get(oldPosition);
			List<Pair<MLKAgent, Action2DMove>> actionsAgents = agentsForcePush.get(oldPosition);
			List<MLKAgent> agents = Pair.extractFirstsFromList(actionsAgents);
			List<Action2DMove> actions = Pair.extractSecondsFromList(actionsAgents);
			
			Action2DMove globalAction = gatherActionsPush(actions);
			if (!globalAction.equals(Action2DMove.idle())) {
				Pair<Integer, Integer> newBlockPosition = newBlockPosition(oldPosition, globalAction);
				boolean blockPushedOut = pushTheBlock(oldPosition, newBlockPosition, nbBlocks);
				if (blockPushedOut) {
					for (MLKAgent ag : agents) {
						for (int k = 0; k < nbBlocks; k++) {
							agentsEvents.get(ag).add(new BlockPushedOutEvent());
						}
					}
				} else {
					for (MLKAgent ag : agents) {
						for (int k = 0; k < nbBlocks; k++) {
							agentsEvents.get(ag).add(new BlockPushedEvent());
						}
					}
				}
			}
			else {
				for (MLKAgent ag : agents) {
					agentsEvents.get(ag).add(new TryPushEvent());
				}
			}
		}
	}
	
	protected Action2DMove gatherActionsPush(List<Action2DMove> actions) {
		Action2DMove globalAction = Action2DMove.idle();
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
