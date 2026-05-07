package agent.modelofotheragent;

import agent.MLKAgent;

public interface MLKAgentModelingOthers extends MLKAgent {
	public static final String DEFAULT_AGENT_ROLE = "MLKAgentModelingOthers";
	
//	@Override
//	public default String getRole() {
//		return DEFAULT_AGENT_ROLE;
//	}

	public void setModelsManager(ModelsManager modelsManager);

	public ModelsManager getModelsManager();

	public void updateModelsOfOtherAgents();

}
