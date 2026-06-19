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
	
	protected static final int REQUIRED_AGENTS_TO_COLLECT_FOOD = 2;
	protected static final int VIEW_RANGE = 3;

	public EnvForagingContinuously(ScenarioContinuously scenario) {
        this(5, 5, scenario);
	}
	
	public EnvForagingContinuously(int width, int height, ScenarioContinuously scenario) {
		super(width, height, scenario, new FullyCooperativeReward());
	}
	
	@Override
	protected void initState() {
		state = new State2DGridInt(getWidth(), getHeight(), VIEW_RANGE, true, true);
    }
	
	
	@Override
	protected void handlePositionEvents(Pair<Integer, Integer> position, List<MLKAgent> agentsAtPosition,
			Map<MLKAgent, List<ReactionEvent>> reactionEventsAction) {
		int numberOfAgents = agentsAtPosition.size();
		int foodValue;
		if (numberOfAgents < REQUIRED_AGENTS_TO_COLLECT_FOOD) {
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
	}
	
}
