package marlkit.gooryield.agent;

import java.util.ArrayList;
import java.util.List;

import agent.MLKAgent;
import agent.modelofotheragent.MLKAgentPredictingOthersAction;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import algorithm.QValueBasedJALPolicy;
import experience.Experience;
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
        if (modelsManager == null) {
            throw new IllegalStateException("Models manager must be configured before agent activation.");
        }

        QValueBasedJALPolicy qPolicy = new QValueBasedJALPolicy(possibleActions);

        setExplorationStrategy(qPolicy);
        setPolicy(qPolicy);
        setupAlgorithm(qPolicy);
    }

    /**
     * Sets the other agents to be modeled by this agent.
     * @param agents the list of other agents to be modeled. The agent itself will be excluded from the list.
     */
    public void setOtherAgents(List<? extends MLKAgent> agents) {
        StandardGroupModelingPredictAction groupModelPrediction = new StandardGroupModelingPredictAction();

        for (MLKAgent agent : agents) {
            if (agent != this) {
                NActionFrequenciesDeterministicPredictionAction model = new NActionFrequenciesDeterministicPredictionAction(yield, windowSize);

                groupModelPrediction.addModelPredictAction(agent, model);
            }
        }

        setModelsManager(new PredictionModelsManager(groupModelPrediction));
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