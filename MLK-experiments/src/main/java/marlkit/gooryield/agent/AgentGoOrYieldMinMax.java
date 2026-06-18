package marlkit.gooryield.agent;

import agent.modelofotheragent.MLKAgentPredictingOthersAction;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import algorithm.QLearningJAL;
import learning.Experience;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import modelofotheragents.MinimaxValueFunctionPredictAction;

public class AgentGoOrYieldMinMax extends AgentGoOrYield implements MLKAgentPredictingOthersAction {
	protected PredictionModelsManager modelsManager;
	
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
	protected void initPolicyAndAlgorithm() {
		MinimaxValueFunctionPredictAction groupModelPrediction = new MinimaxValueFunctionPredictAction();
		QLearningJAL qPolicy = new QLearningJAL(possibleActions, groupModelPrediction);
		qPolicy.setExplorationStrategy(new EpsilonGreedyExponentialDecay(1.0, 0.001));
		groupModelPrediction.setActionEvaluator(qPolicy.getTable());
		
		QLearning qLearning = new QLearning(qPolicy, possibleActions, 0.2, 0.95);
		
		setPolicy(qPolicy);
		setAlgorithm(qLearning);
		
		modelsManager = new PredictionModelsManager(groupModelPrediction);
		
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
	
}
