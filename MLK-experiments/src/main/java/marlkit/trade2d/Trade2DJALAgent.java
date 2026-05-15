package marlkit.trade2d;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import agent.modelofotheragent.MLKAgentPredictingOthersAction;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import algorithm.QLearningJAL;
import environment.observation.Observation;
import learning.Experience;
import learning.algorithm.QLearning;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;
import modelofotheragents.NActionFrequenciesDeterministicPredictionAction;

/**
 * Trade2D agent using joint-action learning with deterministic N-step prediction.
 */
public class Trade2DJALAgent extends Trade2DAgent implements MLKAgentPredictingOthersAction {
	private PredictionModelsManager modelsManager;
	private final StandardGroupModelingPredictAction groupModel;
	private final Set<MLKAgent> modeledAgents;
	private final int windowSize;
	private final Action defaultAction;

	public Trade2DJALAgent(List<UniteProductionSpatial> unites, int windowSize) {
		super(unites);
		if (windowSize <= 0) {
			throw new IllegalArgumentException("windowSize must be positive.");
		}
		if (possibleActions.isEmpty()) {
			throw new IllegalStateException("possibleActions must not be empty.");
		}
		this.windowSize = windowSize;
		this.defaultAction = possibleActions.get(0);
		this.groupModel = new StandardGroupModelingPredictAction();
		this.modeledAgents = new HashSet<>();

		QLearningJAL qPolicy = new QLearningJAL(possibleActions, groupModel);
		qPolicy.setExplorationStrategy(new EpsilonGreedyExponentialDecay(1.0, 0.005));
		QLearning qLearning = new QLearning(qPolicy, possibleActions, 0.2, 0.95);

		setPolicy(qPolicy);
		setAlgorithm(qLearning);
		modelsManager = new PredictionModelsManager(groupModel);
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), MLKAgentPredictingOthersAction.DEFAULT_AGENT_ROLE);
	}

	@Override
	public Experience getEnvExperience() {
		return getMLKEnvironment().getExperienceJointAction(this);
	}

	@Override
	public void updateModelsOfOtherAgents() {
		Map<MLKAgent, Action> actions = getMLKEnvironment().getAgentsActions();
		MappedJointAction actualActions = new MappedJointAction(actions);
		Observation observation = getMLKEnvironment().getObservation(this);
		modelsManager.getGroupModelPredictAction().updateModel(
				observation,
				modelsManager.getLastPredictedAction(),
				actualActions);
	}

	@Override
	public void setModelsManager(ModelsManager modelsManager) {
		if (!(modelsManager instanceof PredictionModelsManager)) {
			throw new IllegalArgumentException("modelsManager should be a PredictionModelsManager.");
		}
		this.modelsManager = (PredictionModelsManager) modelsManager;
	}

	@Override
	public PredictionModelsManager getModelsManager() {
		return modelsManager;
	}

	public void initModels(List<MLKAgent> agents) {
		if (agents == null) {
			throw new IllegalArgumentException("agents must not be null.");
		}
		for (MLKAgent agent : agents) {
			if (agent == null || agent.equals(this) || modeledAgents.contains(agent)) {
				continue;
			}
			NActionFrequenciesDeterministicPredictionAction model =
					new NActionFrequenciesDeterministicPredictionAction(defaultAction, windowSize);
			groupModel.addModelPredictAction(agent, model);
			modeledAgents.add(agent);
		}
	}
}
