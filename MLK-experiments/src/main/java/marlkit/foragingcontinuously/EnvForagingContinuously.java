package marlkit.foragingcontinuously;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.state.State2DGridInt;
import marlkit.foraging.EnvForaging;
import marlkit.foragingcontinuously.scenario.ScenarioContinuously;
import reward.ReactionEvent;
import rewardmodelimplementation.FullyCooperativeReward;
import util.Pair;

public class EnvForagingContinuously extends EnvForaging {
	
	protected int requiredAgentsToCollectFood;
	protected int viewRange;

	public EnvForagingContinuously(ScenarioContinuously scenario) {
        this(5, 5, scenario);
	}
	
	public EnvForagingContinuously(int width, int height, ScenarioContinuously scenario) {
		this(width, height, scenario, 2, 3);
	}
	
	public EnvForagingContinuously(int width, int height, ScenarioContinuously scenario, int requiredAgentsToCollectFood, int viewRange) {
		super(width, height, scenario, new FullyCooperativeReward());
		this.requiredAgentsToCollectFood = requiredAgentsToCollectFood;
		this.viewRange = viewRange;
	}
	
	@Override
	protected void initState() {
		state = new State2DGridInt(getWidth(), getHeight(), viewRange, true, true);
    }
	
	
	@Override
	protected void handlePositionEvents(Pair<Integer, Integer> position, List<MLKAgent> agentsAtPosition,
			Map<MLKAgent, List<ReactionEvent>> reactionEventsAction) {
		int numberOfAgents = agentsAtPosition.size();
		int foodValue;
		if (numberOfAgents < requiredAgentsToCollectFood) {
			foodValue = 0;
		} else {
			foodValue = state.getValue(position);
			state.setValue(position, 0);
		}
		
		for(MLKAgent ag : agentsAtPosition) {
			reactionEventsAction.putIfAbsent(ag, new ArrayList<>());
			List<ReactionEvent> events = new ArrayList<>();
			computeEvents(events, foodValue, numberOfAgents);
			reactionEventsAction.get(ag).addAll(events);
		}
		
		for (int i = 0; i < foodValue; i++) {
			((ScenarioContinuously) scenario).respawn(prng(), state);
		}
	}
	
}
