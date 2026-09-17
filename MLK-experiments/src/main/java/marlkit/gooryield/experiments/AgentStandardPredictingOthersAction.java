package marlkit.gooryield.experiments;

import agent.AgentStandard;
import agent.modelofotheragent.MLKAgentPredictingOthersAction;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import experience.Experience;
import learning.Algorithm;
import learning.Policy;

public class AgentStandardPredictingOthersAction extends AgentStandard implements MLKAgentPredictingOthersAction {
	protected PredictionModelsManager modelsManager;
	
	public AgentStandardPredictingOthersAction(Policy policy, Algorithm algorithm) {
		super(policy, algorithm);
	}
	
	@Override
	public void setModelsManager(ModelsManager modelsManager) {
		this.modelsManager = (PredictionModelsManager) modelsManager;
		
	}

	@Override
	public PredictionModelsManager getModelsManager() {
		return modelsManager;
	}
	
	@Override
	public void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), MLKAgentPredictingOthersAction.DEFAULT_AGENT_ROLE);
	}
	
	@Override
    public Experience getEnvExperience() {
        return getMLKEnvironment().getExperienceJointAction(this);
    }

}