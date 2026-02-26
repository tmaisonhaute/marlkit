package marlkit.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.EnvironmentStandard;
import environment.state.State;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;
import rewardmodels.FullyCooperativeReward;

public class EnvTrade extends EnvironmentStandard {
	private static final int QUANTITY_PER_REQUEST = 1;
	private Map<ResourceType, Float> basePrices;
	private ScenarioUP scenario;
	private StateUnites state;
	protected Map<MLKAgent, Action> lastActions;

	public EnvTrade() {
		this(800, 600);
	}
	
	public EnvTrade(int width, int height) {
//		this(width, height, new MixedReward());
		this(width, height, new FullyCooperativeReward());
	}

	public EnvTrade(int width, int height, RewardModel rewardModel) {
		this(width, height, rewardModel, new Scenario4());
	}

	
	public EnvTrade(int width, int height, RewardModel rewardModel, ScenarioUP scenario) {
		super(width, height, rewardModel);
		this.scenario = scenario;
		this.basePrices = scenario.getBasePrices();
		
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		this.scenario.setup(prng());
		this.state = new StateUnites(agents.getAgents(), scenario.createUnites(prng()));
		setupState();
	}
	
	@Override
	public void reset() {
		setupState();
	}

	@Override
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
	}

	@Override
	protected void setupState() {
		getLogger().info("RestingState.");
		for (UniteProduction up : state.getUnitesProductions()) {
			up.reset();
		}
	}
	
	@Override 
	protected void setupAgents() {
		//Nothing to do
	}

	@Override
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		this.lastActions = new HashMap<>(actions);
		
		state.updateState();
		Map<UniteProduction, List<MLKAgent>> requestingAgents = new HashMap<>();

		initUPRequestingAgents(requestingAgents);
		setupAgentsRequests(requestingAgents, actions);

		Map<MLKAgent, ResourceQuantify> receivedResource = new HashMap<>();
		upProcessRequests(receivedResource, requestingAgents);
		
		return computeEvents(receivedResource);
	}
	
	private void initUPRequestingAgents(Map<UniteProduction, List<MLKAgent>> requestingAgents ) {
		for(UniteProduction up : state.getUnitesProductions()) {
			requestingAgents.put(up,  new ArrayList<>());
		}
	}
	
	private void setupAgentsRequests(Map<UniteProduction, List<MLKAgent>> requestingAgents, Map<MLKAgent, Action> actions) {
		for (MLKAgent agent : actions.keySet()) {
			ActionRequestResource action = (ActionRequestResource)actions.get(agent);
			requestingAgents.get(action.getUniteProduction()).add(agent);
		}
	}
	
	private void upProcessRequests(Map<MLKAgent, ResourceQuantify> receivedResource, Map<UniteProduction, List<MLKAgent>> requestingAgents) {
		for (UniteProduction up : state.getUnitesProductions()){
			receivedResource.putAll(up.processRequests(requestingAgents.get(up), QUANTITY_PER_REQUEST));
		}
	}
	
	
	private Map<MLKAgent, List<ReactionEvent>> computeEvents(Map<MLKAgent, ResourceQuantify> receivedResource) {
		Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
		for(MLKAgent agent : receivedResource.keySet()){
			List<ReactionEvent> events = new ArrayList<>();
			ReactionEvent e = new CollectResourceEvent(basePrices, receivedResource.get(agent));
			events.add(e);
			results.put(agent, events);
		}
		return results;
	}

	@Override
	public State getState() {
		return state;
	}

	public Map<MLKAgent, Action> getLastActions() {
		return lastActions;
	}

}
