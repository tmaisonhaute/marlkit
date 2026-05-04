package marlkit.foraging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2D;
import environment.EnvironmentStandard;
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
	protected MoveEvent moveEvent;
	
	public EnvForaging() {
        this(10, 10, new ScenarioUniform(5));
	}
	
	public EnvForaging(int width, int height, Scenario scenario) {
		this(width, height, scenario, new MixedReward());
	}

    public EnvForaging(int width, int height, Scenario scenario, RewardModel rewardModel) { 
        super(width, height, rewardModel);
        this.scenario = scenario;
        moveEvent = new MoveEvent();
    }  

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight(), true, true);
	}
	
	
	@Override	public void addAgent(MLKAgent agent) {
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
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, List<ReactionEvent>> reactionEventsAction = new HashMap<>();
		Map<Pair<Integer, Integer>, List<MLKAgent>> newAgentsPositions = new HashMap<>();
		
		moveAgents(actions, reactionEventsAction, newAgentsPositions);
		computeReactionEventsByPositions(reactionEventsAction, newAgentsPositions);
		
		return reactionEventsAction;
	}
	
	/**
	 * Moves agents according to their actions and fills the newAgentsPositions map with the new positions of the agents.
	 * Also fills the reactionEventsAction map with the move events for the agents that have moved.
	 * @param actions the actions of the agents
	 * @param reactionEventsAction a map between agents and their corresponding reaction events, to be filled
	 * @param newAgentsPositions a map between positions and the agents at those positions after the move phase, to be filled
	 */
	protected void moveAgents(Map<MLKAgent, Action> actions, Map<MLKAgent, List<ReactionEvent>> reactionEventsAction
			, Map<Pair<Integer, Integer>, List<MLKAgent>> newAgentsPositions) {
		for (MLKAgent ag : agents.getAgents()) {
			Move2D action = (Move2D) actions.get(ag);
			Pair<Integer, Integer> oldPosition = state.getAgentPosition(ag);
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			newAgentsPositions.putIfAbsent(newPosition, new ArrayList<>());
			newAgentsPositions.get(newPosition).add(ag);
			
			handleMoveEvent(ag, reactionEventsAction, oldPosition != newPosition);
		} 
	}
	
	/**
	 * Handles the move event for an agent. If the agent has moved, adds the move event to the reactionEventsAction map for the agent.
	 * @param ag the agent that has moved
	 * @param reactionEventsAction the map between agents and their corresponding reaction events
	 * @param hasMoved a boolean indicating whether the agent has moved or not.
	 */
	protected void handleMoveEvent(MLKAgent ag,
			Map<MLKAgent, List<ReactionEvent>> reactionEventsAction, boolean hasMoved) {
		List<ReactionEvent> events = new ArrayList<>();
		reactionEventsAction.putIfAbsent(ag, events);
		if (hasMoved) {
			reactionEventsAction.get(ag).add(moveEvent);	
		}
	}
	
	/**
	 * Computes the reaction events for the agents at their new positions. For each position, checks the value of the state at that position and computes the corresponding events (e.g., food collected) for the agents at that position. Also updates the state by setting the value at that position to 0 (i.e., removing the food).
	 * The reaction events are added to the reactionEventsAction map for each agent.
	 * @param reactionEventsAction a map between agents and their corresponding reaction events
	 * @param newAgentsPositions a map between positions and the agents at those positions after the move phase
	 */
	protected void computeReactionEventsByPositions(
			Map<MLKAgent, List<ReactionEvent>> reactionEventsAction, 
			Map<Pair<Integer, Integer>, List<MLKAgent>> newAgentsPositions) {
		
		for(Map.Entry<Pair<Integer, Integer>, List<MLKAgent>> entry : newAgentsPositions.entrySet()) { 
			Pair<Integer, Integer> position = entry.getKey(); 
			List<MLKAgent> agentsAtPosition = entry.getValue(); 
			handlePositionEvents(position, agentsAtPosition, reactionEventsAction);
		}
	}

	/**
	 * Computes the events based on the value of the state at that position.
	 * @param position the position of the agents
	 * @param agentsAtPosition the agents at that position
	 * @param reactionEventsAction the map between agents and their corresponding reaction events
	 */
	protected void handlePositionEvents(Pair<Integer, Integer> position, List<MLKAgent> agentsAtPosition,
			Map<MLKAgent, List<ReactionEvent>> reactionEventsAction) {
		int foodValue = state.getValue(position);
		state.setValue(position, 0);
		int numberOfAgents = agentsAtPosition.size();

		for(MLKAgent ag : agentsAtPosition) {
			reactionEventsAction.putIfAbsent(ag, new ArrayList<>());
			List<ReactionEvent> events = new ArrayList<>();
			computeEvents(events, foodValue, numberOfAgents);
			reactionEventsAction.get(ag).addAll(events);
		}
	}
	
	/**
	 * Computes the events based on the value of the state at that position. 
	 * @param events the list of events to be filled based on the value of the state at that position
	 * @param foodValue the value of the state at that position, indicating the amount of food collected.
	 */
	protected void computeEvents(List<ReactionEvent> events, int foodValue, int numberOfAgents) {
		for(int k = 0; k < foodValue; k++) {
			events.add(new FoodCollectedEvent(1.0/numberOfAgents));
		}
	}


	/**
	 * Moves the agent according to the action and returns the new position of the agent.
	 * @param agent the agent to be moved
	 * @param action the action indicating the direction of the move
	 * @return the new position of the agent after the move
	 */
	protected Pair<Integer, Integer> stateMoveAgent(MLKAgent agent, Move2D action) {
		state.moveAgent(agent, action.getValue());
		return state.getAgentPosition(agent).clone();
	}

	/**
	 * Returns the positions of the agents in the environment.
	 * @return a map between agents and their positions in the environment
	 */
	public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
		return state.getAgentsPositions();
	}
	
    @Override
	public State2DGridInt getState() {
		return state;
	}
	
	public Scenario getScenario() {
		return scenario;
	}
	
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
