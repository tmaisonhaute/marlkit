package marlkit.gooryield.agent;

import java.util.ArrayList;
import java.util.List;

import agent.MLKAgent;
import agent.modelofotheragent.MLKAgentPredictingOthersAction;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import algorithm.QLearningJAL;
import learning.Experience;
import modelofotheragents.NActionFrequenciesDeterministicPredictionAction;

public class AgentGoOrYieldActionFrequenciesPrediction extends AgentGoOrYield implements MLKAgentPredictingOthersAction {

    protected PredictionModelsManager modelsManager;
    protected List<MLKAgent> otherAgents = new ArrayList<>();
    protected int windowSize;

    public AgentGoOrYieldActionFrequenciesPrediction(int windowSize) {
        super();
        if (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be positive.");
        }
        this.windowSize = windowSize;
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), MLKAgentPredictingOthersAction.DEFAULT_AGENT_ROLE);
    }

    @Override
    protected void initPolicyAndAlgorithm() {
        StandardGroupModelingPredictAction groupModelPrediction = new StandardGroupModelingPredictAction();

        for (MLKAgent other : otherAgents) {
            if (other != this) {
                NActionFrequenciesDeterministicPredictionAction model =
                        new NActionFrequenciesDeterministicPredictionAction(yield, windowSize);
                groupModelPrediction.addModelPredictAction(other, model);
            }
        }

        QLearningJAL qPolicy = new QLearningJAL(possibleActions, groupModelPrediction);
        setExplorationStrategy(qPolicy);

        setPolicy(qPolicy);
        setupAlgorithm(qPolicy);

        modelsManager = new PredictionModelsManager(groupModelPrediction);
    }

    public void setOtherAgents(List<? extends MLKAgent> agents) {
        this.otherAgents = new ArrayList<>();
        for (MLKAgent agent : agents) {
            if (agent != this) {
                this.otherAgents.add(agent);
            }
        }
    }

    @Override
    public Experience getEnvExperience() {
        return getMLKEnvironment().getExperienceJointAction(this);
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