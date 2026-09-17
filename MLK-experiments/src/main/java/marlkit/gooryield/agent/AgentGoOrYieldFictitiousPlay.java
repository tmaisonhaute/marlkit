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
import modelofotheragents.FictitiousPlayDeterministicPredictAction;

public class AgentGoOrYieldFictitiousPlay extends AgentGoOrYield implements MLKAgentPredictingOthersAction {

    protected PredictionModelsManager modelsManager;
    protected List<MLKAgent> otherAgents = new ArrayList<>();

    public AgentGoOrYieldFictitiousPlay() {
        super();
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


    public void setOtherAgents(List<? extends MLKAgent> agents) {
        StandardGroupModelingPredictAction groupModelPrediction = new StandardGroupModelingPredictAction();

        for (MLKAgent agent : agents) {
            if (agent != this) {
                FictitiousPlayDeterministicPredictAction model = new FictitiousPlayDeterministicPredictAction(yield);

                model.setLogger(getLogger());
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