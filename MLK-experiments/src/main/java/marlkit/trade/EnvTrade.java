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
	private final int quantityPerRequest = 1;
	private Map<ResourceType, Float> basePrices;
	private RewardConfiguration rewardConfig;
	// protected List<UniteProduction> unitesProductions;
	private StateUnites state;

	public EnvTrade(int width, int height) {
		this(width, height, new RewardConfigurationMixed());
	}

	public EnvTrade(int width, int height, RewardConfiguration rewardConfig) {
		this(width, height, rewardConfig, new IndependantLearning());
	}

	public EnvTrade(int width, int height, RewardConfiguration rewardConfig, MLKInteraction interactionMethod) {
		this(width, height, rewardConfig, interactionMethod, new Scenario1());
	}
	
	public EnvTrade(int width, int height, RewardConfiguration rewardConfig, MLKInteraction interactionMethod, ScenarioUP scenario) {
		super(width, height);
		this.rewardConfig = rewardConfig;
		this.interactionMethod = interactionMethod;
		this.state = new StateUnites(agents.getAgents(), scenario.createUnites());
		basePrices = scenario.getBasePrices();
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
		for (UniteProduction up : state.getUnitesProductions()) {
			up.reset();
		}
	}

	@Override
	public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
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
			receivedResource.putAll(up.processRequests(requestingAgents.get(up), quantityPerRequest));
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

}
