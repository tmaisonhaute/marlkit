package marlkit.foraging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.EnvironmentStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import marlkit.foraging.events.FoodCollectedEvent;
import marlkit.foraging.events.MoveEvent;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;
import rewardmodels.MixedReward;
import util.Pair;

public class EnvForaging extends EnvironmentStandard {

	protected State2DGridInt state;
	protected Scenario scenario;
	
	public EnvForaging() {
        this(10, 10, new ScenarioUniform(5));
	}
	
	public EnvForaging(int width, int height, Scenario scenario) {
		this(width, height, scenario, new MixedReward());
	}

    public EnvForaging(int width, int height, Scenario scenario, RewardModel rewardModel) { 
        super(width, height, rewardModel);
        this.scenario = scenario;
    }  

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight(), true, true);
		setupState();
	}
	
	
	@Override
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
	}
	
	@Override
	public void setupState() {
		scenario.initState(prng(), state);
	}
	
	@Override
	public void setupAgents() {
		scenario.initAgents(prng(), state, agents.getAgents());
	}
	
	@Override
	public void reset() {
		state.reset();
		setupState();
		setupAgents();
	}
	

	@Override
	public Map<MLKAgent, Pair<Action, List<ReactionEvent>>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, Pair<Action, List<ReactionEvent>>> results = new HashMap<>();
		Map<Pair<Integer, Integer>, List<MLKAgent>> newAgentsPositions = new HashMap<>();
		
		moveAgents(actions, newAgentsPositions);
		computeResultsForPositions(actions, results, newAgentsPositions);
		
		return results;
	}
	
	protected void moveAgents(Map<MLKAgent, Action> actions, Map<Pair<Integer, Integer>, List<MLKAgent>> newAgentsPositions) {
		for (MLKAgent ag : agents.getAgents()) {
			Action2DMove action = (Action2DMove) actions.get(ag);
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			newAgentsPositions.putIfAbsent(newPosition, new ArrayList<>());
			newAgentsPositions.get(newPosition).add(ag);
		} 
	}
	
	
	protected void computeResultsForPositions(Map<MLKAgent, Action> actions, 
			Map<MLKAgent, Pair<Action, List<ReactionEvent>>> results, 
			Map<Pair<Integer, Integer>, List<MLKAgent>> newAgentsPositions) {
		
		for(Map.Entry<Pair<Integer, Integer>, List<MLKAgent>> entry : newAgentsPositions.entrySet()) { 
			Pair<Integer, Integer> position = entry.getKey(); 
			List<MLKAgent> agentsAtPosition = entry.getValue(); 
			handlePositionEvents(position, agentsAtPosition, actions, results);
		}
	}

	protected void handlePositionEvents(Pair<Integer, Integer> position, List<MLKAgent> agentsAtPosition,
			Map<MLKAgent, Action> actions, Map<MLKAgent, Pair<Action, List<ReactionEvent>>> results) {
		int foodValue = state.getValue(position);
		state.setValue(position, 0);

		for(MLKAgent ag : agentsAtPosition) {
			List<ReactionEvent> events = new ArrayList<>();
			computeEvents(events, foodValue);
			Action action = actions.get(ag);
			results.put(ag, new Pair<>(action, events));
		}
	}
	
	protected void computeEvents(List<ReactionEvent> events, int foodValue) {
		if(foodValue > 0) {
			for(int k = 0; k < foodValue; k++) {
				events.add(new FoodCollectedEvent());
			}
		}
		else {
			events.add(new MoveEvent());
		}
	}
	
	protected Pair<Integer, Integer> stateMoveAgent(MLKAgent agent, Action2DMove action) {
		state.moveAgent(agent, action.getValue());
		return state.getAgentPosition(agent).clone();
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
		return state.getAgentsPositions();
	}
	
    @Override
	public State getState() {
		return state;
	}
	
	public Scenario getScenario() {
		return scenario;
	}
	
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
