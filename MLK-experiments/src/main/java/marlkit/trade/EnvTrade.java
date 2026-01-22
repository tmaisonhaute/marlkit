package marlkit.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.interaction.IndependantLearning;
import agent.interaction.MLKInteraction;
import environment.EnvironmentStandard;
import environment.reward.Reward;
import environment.state.State;
import util.Pair;

public class EnvTrade extends EnvironmentStandard {
	private static final int QUANTITY_PER_REQUEST = 1;
	private Map<ResourceType, Float> basePrices;
	private RewardConfiguration rewardConfig;
	private ScenarioUP scenario;
	private StateUnites state;
	protected Map<MLKAgent, Action> lastActions;

	public EnvTrade() {
		this(800, 600);
	}
	
	public EnvTrade(int width, int height) {
//		this(width, height, new RewardConfigurationMixed());
		this(width, height, new RewardConfigurationFullyCoop());
	}

	public EnvTrade(int width, int height, RewardConfiguration rewardConfig) {
		this(width, height, rewardConfig, new Scenario4());
	}

	public EnvTrade(int width, int height, RewardConfiguration rewardConfig, ScenarioUP scenario) {
		this(width, height, rewardConfig, scenario, new IndependantLearning());
	}
	
	public EnvTrade(int width, int height, RewardConfiguration rewardConfig, ScenarioUP scenario, MLKInteraction interactionMethod) {
		super(width, height);
		this.rewardConfig = rewardConfig;
		this.interactionMethod = interactionMethod;
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
	public void setupAgent(MLKAgent agent) {
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
	public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
		this.lastActions = new HashMap<>(actions);
		Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
		state.updateState();
		Map<UniteProduction, List<MLKAgent>> requestingAgents = new HashMap<>();

		for(UniteProduction up : state.getUnitesProductions()) {
			requestingAgents.put(up,  new ArrayList<>());
		}
		for (MLKAgent agent : actions.keySet()) {
			ActionRequestResource action = (ActionRequestResource)actions.get(agent);
			requestingAgents.get(action.getUniteProduction()).add(agent);
		}

		Map<MLKAgent, ResourceQuantify> receivedResource = new HashMap<>();
		for (UniteProduction up : state.getUnitesProductions()){
			receivedResource.putAll(up.processRequests(requestingAgents.get(up), QUANTITY_PER_REQUEST));
		}
		Map<MLKAgent, Reward> rewards = rewardConfig.computeRewards(receivedResource, basePrices);
		for (MLKAgent agent : actions.keySet()) {
			results.put(agent, new Pair<>(actions.get(agent), rewards.get(agent)));
		}
		return results;
	}

	@Override
	protected State getState() {
		return state;
	}

	public Map<MLKAgent, Action> getLastActions() {
		return lastActions;
	}

}
