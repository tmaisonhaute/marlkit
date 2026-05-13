package marlkit.trade2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.EnvironmentStandard;
import environment.state.State;
import marlkit.trade.ActionRequestResource;
import marlkit.trade.ResourceQuantify;
import marlkit.trade.ResourceType;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;
import util.Position;

/**
 * Spatialized trade environment with distance-based penalties.
 */
public class EnvTrade2D extends EnvironmentStandard {
	private static final int QUANTITY_PER_REQUEST = 1;
	private static final double DEFAULT_DISTANCE_PENALTY_PER_UNIT = 1.0;

	private final double distancePenaltyPerUnit;
	private Map<ResourceType, Float> basePrices;
	private ScenarioTrade2D scenario;
	private StateUnites2D state;
	protected Map<MLKAgent, Action> lastActions;

	/**
	 * Create a Trade2D environment with default distance penalty.
	 *
	 * @param width Environment width.
	 * @param height Environment height.
	 * @param rewardModel Reward model used by the environment.
	 * @param scenario Scenario defining units and base prices.
	 */
	public EnvTrade2D(int width, int height, RewardModel rewardModel, ScenarioTrade2D scenario) {
		this(width, height, rewardModel, scenario, DEFAULT_DISTANCE_PENALTY_PER_UNIT);
	}

	/**
	 * Create a Trade2D environment with custom distance penalty.
	 *
	 * @param width Environment width.
	 * @param height Environment height.
	 * @param rewardModel Reward model used by the environment.
	 * @param scenario Scenario defining units and base prices.
	 * @param distancePenaltyPerUnit Penalty applied per Euclidean distance unit.
	 */
	public EnvTrade2D(int width, int height, RewardModel rewardModel, ScenarioTrade2D scenario,
			double distancePenaltyPerUnit) {
		super(width, height, rewardModel);
		this.scenario = scenario;
		this.basePrices = scenario.getBasePrices();
		this.distancePenaltyPerUnit = distancePenaltyPerUnit;
	}

	/**
	 * Initialize scenario, state, and initial placements.
	 */
	@Override
	protected void onActivation() {
		super.onActivation();
		this.scenario.setup(prng(), getWidth(), getHeight());
		this.state = new StateUnites2D(agents.getAgents(), scenario.createUnites(prng()));
		setupState();
	}

	/**
	 * Reset the environment state and reposition entities.
	 */
	@Override
	public void reset() {
		setupState();
		setupAgents();
	}

	/**
	 * Register an agent in the environment.
	 *
	 * @param agent Agent to add.
	 */
	@Override
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
	}

	/**
	 * Reset stocks and reassign all positions.
	 */
	@Override
	protected void setupState() {
		getLogger().info("Resetting State.");
		state.reset();
	}

	/**
	 * Assign positions to agents in the environment.
	 */
	@Override
	protected void setupAgents() {
		scenario.initAgents(prng(), getWidth(), getHeight(), state.getAgents());
	}

	/**
	 * Apply actions, process requests, and compute rewards.
	 *
	 * @param actions Actions proposed by agents.
	 * @return Reaction events generated per agent.
	 */
	@Override
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		this.lastActions = new HashMap<>(actions);

		state.updateState();
		Map<UniteProductionSpatial, List<MLKAgent>> requestingAgents = new HashMap<>();
		Map<MLKAgent, UniteProductionSpatial> requestedUnits = new HashMap<>();
		Map<UniteProductionSpatial, Integer> availableStock = new HashMap<>();
		Map<UniteProductionSpatial, Integer> requesterCounts = new HashMap<>();

		initUPRequestingAgents(requestingAgents);
		setupAgentsRequests(requestingAgents, requestedUnits, actions);

		Map<MLKAgent, ResourceQuantify> receivedResource = new HashMap<>();
		captureRequestStats(requestingAgents, availableStock, requesterCounts);
		upProcessRequests(receivedResource, requestingAgents);

		return computeEvents(receivedResource, requestedUnits, availableStock, requesterCounts);
	}

	/**
	 * Initialize the request list per production unit.
	 *
	 * @param requestingAgents Map to populate with empty lists.
	 */
	private void initUPRequestingAgents(Map<UniteProductionSpatial, List<MLKAgent>> requestingAgents) {
		for (UniteProductionSpatial up : state.getUnitesProductions()) {
			requestingAgents.put(up, new ArrayList<>());
		}
	}

	/**
	 * Collect unit requests for each agent.
	 *
	 * @param requestingAgents Output map of unit to requesting agents.
	 * @param requestedUnits Output map of agent to requested unit.
	 * @param actions Input actions by agent.
	 */
	private void setupAgentsRequests(Map<UniteProductionSpatial, List<MLKAgent>> requestingAgents,
			Map<MLKAgent, UniteProductionSpatial> requestedUnits,
			Map<MLKAgent, Action> actions) {
		for (MLKAgent agent : actions.keySet()) {
			ActionRequestResource action = (ActionRequestResource) actions.get(agent);
			UniteProductionSpatial unit = (UniteProductionSpatial) action.getUniteProduction();
			requestingAgents.get(unit).add(agent);
			requestedUnits.put(agent, unit);
		}
	}

	/**
	 * Execute unit allocation based on current requests.
	 *
	 * @param receivedResource Output map of agent to allocated resource.
	 * @param requestingAgents Map of unit to requesting agents.
	 */
	private void upProcessRequests(Map<MLKAgent, ResourceQuantify> receivedResource,
			Map<UniteProductionSpatial, List<MLKAgent>> requestingAgents) {
		for (UniteProductionSpatial up : state.getUnitesProductions()) {
			receivedResource.putAll(up.processRequests(requestingAgents.get(up), QUANTITY_PER_REQUEST));
		}
	}

	/**
	 * Capture stock and requester counts before allocation.
	 *
	 * @param requestingAgents Map of unit to requesting agents.
	 * @param availableStock Output map of unit to available stock.
	 * @param requesterCounts Output map of unit to requester count.
	 */
	private void captureRequestStats(Map<UniteProductionSpatial, List<MLKAgent>> requestingAgents,
			Map<UniteProductionSpatial, Integer> availableStock,
			Map<UniteProductionSpatial, Integer> requesterCounts) {
		for (UniteProductionSpatial up : state.getUnitesProductions()) {
			availableStock.put(up, up.getStockValue());
			List<MLKAgent> requesters = requestingAgents.get(up);
			requesterCounts.put(up, requesters == null ? 0 : requesters.size());
		}
	}

	/**
	 * Build reaction events for collected resources and distance penalties.
	 *
	 * @param receivedResource Allocated resources by agent.
	 * @param requestedUnits Requested unit per agent.
	 * @param availableStock Available stock per unit before allocation.
	 * @param requesterCounts Requester count per unit.
	 * @return Reaction events per agent.
	 */
	private Map<MLKAgent, List<ReactionEvent>> computeEvents(
			Map<MLKAgent, ResourceQuantify> receivedResource,
			Map<MLKAgent, UniteProductionSpatial> requestedUnits,
			Map<UniteProductionSpatial, Integer> availableStock,
			Map<UniteProductionSpatial, Integer> requesterCounts) {
		Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
		for (Map.Entry<MLKAgent, ResourceQuantify> entry : receivedResource.entrySet()) {
			MLKAgent agent = entry.getKey();
			List<ReactionEvent> events = new ArrayList<>();
			UniteProductionSpatial requestedUnit = requestedUnits.get(agent);
			int available = requestedUnit == null ? 0 : availableStock.getOrDefault(requestedUnit, 0);
			int requesters = requestedUnit == null ? 0 : requesterCounts.getOrDefault(requestedUnit, 0);
			events.add(new CollectResourceEventTrade2D(basePrices, entry.getValue(), available, requesters));
			Position agentPos = state.getAgentPosition(agent);
			Position unitPos = requestedUnit == null ? null : requestedUnit.getPosition();
			if (agentPos != null && unitPos != null) {
				double distance = agentPos.distancePoint(unitPos);
				events.add(new DistancePenaltyEvent(distance, distancePenaltyPerUnit));
			}
			results.put(agent, events);
		}
		return results;
	}


	/**
	 * Return the current environment state.
	 *
	 * @return Current state.
	 */
	@Override
	public State getState() {
		return state;
	}

	/**
	 * Return the most recent actions map.
	 *
	 * @return Last actions per agent.
	 */
	public Map<MLKAgent, Action> getLastActions() {
		return lastActions;
	}

	/**
	 * Return base prices for each resource type.
	 *
	 * @return Base price map.
	 */
	public Map<ResourceType, Float> getBasePrices() {
		return basePrices;
	}
}
