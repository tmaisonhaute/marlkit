package centralizedtraining;

import simulation.MLKScheduler;

public abstract class SchedulerCentralizedCritic extends MLKScheduler {
public static final String CENTRALIZED_CRITIC_AGENT_ROLE = "AgentCentralizedCritic";
	
	private CentralizedCriticActivator centralizedCriticActivator;

	@Override
	protected void onActivation() {
		super.onActivation();
		centralizedCriticActivator = new CentralizedCriticActivator(getModelGroup(), CENTRALIZED_CRITIC_AGENT_ROLE);
		addActivator(centralizedCriticActivator);
	}

	@Override
	protected void agentsCollectExperience() {
		centralizedCriticActivator.execute();
	}
}
